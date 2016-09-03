package brains.mock.firebasetest.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import brains.mock.firebasetest.R;
import brains.mock.firebasetest.model.User;
import brains.mock.firebasetest.rxfirebase.RxFirebase;
import brains.mock.firebasetest.util.Const;
import brains.mock.firebasetest.util.DialogHelper;
import brains.mock.firebasetest.util.NetworkUtil;
import brains.mock.firebasetest.util.Utils;

import java.util.Date;

import rx.functions.Action0;
import rx.functions.Action1;
import timber.log.Timber;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9001;

    private EditText vEmailEditText;
    private EditText vPasswordEditText;
    private Button vSignInButton;
    private Button vCreateFirebaseAccountButton;
    private SignInButton vGoogleSignInButton;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseDatabase mDataBase;
    private DatabaseReference mUserReference;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        vEmailEditText = (EditText) findViewById(R.id.emailEditText);
        vPasswordEditText = (EditText) findViewById(R.id.passwordEditText);
        vSignInButton = (Button) findViewById(R.id.signInButton);
        vCreateFirebaseAccountButton = (Button) findViewById(R.id.signUpButton);
        vGoogleSignInButton = (SignInButton) findViewById(R.id.googleSignInButton);

        vSignInButton.setOnClickListener(this);
        vCreateFirebaseAccountButton.setOnClickListener(this);
        vGoogleSignInButton.setOnClickListener(this);

        initClients();
    }

    private void initClients() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Timber.w(TAG, "onConnectionFailed: %1$s", connectionResult);
                        Toast.makeText(LoginActivity.this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mDataBase = FirebaseDatabase.getInstance();
        mUserReference = mDataBase.getReference(Const.REFERENCE_USER);
    }

    @Override
    public void onClick(final View view) {
        final String email = vEmailEditText.getText().toString().trim();
        final String password = vPasswordEditText.getText().toString().trim();

        NetworkUtil.checkNetworkWithAlertDialogAndRetry(LoginActivity.this, new NetworkUtil.ActionCallback() {
            @Override
            public void onAction() {
                switch (view.getId()) {
                    case R.id.signInButton:
                        signIn(email, password);
                        break;
                    case R.id.signUpButton:
                        createFirebaseAccount(email, password);
                        break;
                    case R.id.googleSignInButton:
                        signInWithGoogle();
                        break;
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                DialogHelper.showProgressDialog(LoginActivity.this, false);

                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(LoginActivity.this, "Google authentication failed.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loginSuccess(boolean isInitialized, String userUID) {
        DialogHelper.dismissProgressDialog(LoginActivity.this);

        Intent intent;
        if (isInitialized) {
            intent = new Intent(LoginActivity.this, ChatListActivity.class);
        } else {
            intent = new Intent(LoginActivity.this, UserSettingsActivity.class);
        }
        intent.putExtra(Const.EXTRA_USER_UID, userUID);

        DialogHelper.dismissProgressDialog(LoginActivity.this);

        startActivity(intent);
        finish();
    }

    private void createFirebaseAccount(String email, String password) {
        Timber.d("createFirebaseAccount: %1$s", email);
        if (!validateForm(email, password)) {
            return;
        }

        RxFirebase.Auth.createUserWithEmailAndPassword(mFirebaseAuth, email, password)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        DialogHelper.showProgressDialog(LoginActivity.this, false);
                    }
                })
                .subscribe(new Action1<AuthResult>() {
                    @Override
                    public void call(AuthResult authResult) {
                        if (authResult.getUser() != null) {
                            pushUserToDb(authResult, Const.PROVIDER_FIREBASE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DialogHelper.dismissProgressDialog(LoginActivity.this);
                        Timber.w(throwable, "createUserWithEmailAndPassword");

                        Toast.makeText(LoginActivity.this, throwable.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        RxFirebase.Auth.signInWithCredential(mFirebaseAuth, credential)
                .subscribe(new Action1<AuthResult>() {
                    @Override
                    public void call(AuthResult authResult) {
                        if (authResult.getUser() != null) {
                            pushUserToDb(authResult, Const.PROVIDER_GOOGLE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DialogHelper.dismissProgressDialog(LoginActivity.this);
                        Timber.w(throwable, "signInWithCredential");
                    }
                });
    }

    private void signIn(String email, String password) {
        Timber.d(TAG, "signInWithGoogle: %1$s", email);
        if (!validateForm(email, password)) {
            return;
        }

        RxFirebase.Auth.signInWithEmailAndPassword(mFirebaseAuth, email, password)
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        DialogHelper.showProgressDialog(LoginActivity.this, false);
                    }
                })
                .subscribe(new Action1<AuthResult>() {
                    @Override
                    public void call(AuthResult authResult) {
                        if (authResult.getUser() != null) {
                            pushUserToDb(authResult, Const.PROVIDER_FIREBASE);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        DialogHelper.dismissProgressDialog(LoginActivity.this);
                        Timber.w(throwable, "signInWithEmailAndPassword");

                        Toast.makeText(LoginActivity.this, throwable.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void signInWithGoogle() {
        if (NetworkUtil.checkInternetConnectivityWithErrorToast(this)) {
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void pushUserToDb(final AuthResult authResult, final String provider) {
        NetworkUtil.checkNetworkWithAlertDialogAndRetry(this, new NetworkUtil.ActionCallback() {
            @Override
            public void onAction() {
                final FirebaseUser firebaseUser = authResult.getUser();
                final DatabaseReference currentUserReference = mUserReference.child(firebaseUser.getUid());

                currentUserReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User currentUserFromDb = dataSnapshot.getValue(User.class);
                        if (currentUserFromDb != null) {
                            Log.e(TAG, currentUserFromDb.toString());
                            currentUserFromDb.setEmail(firebaseUser.getEmail());
                            currentUserFromDb.setLastAvailableTime(new Date().getTime());
                            currentUserFromDb.setProvider(provider);
                            currentUserReference.updateChildren(currentUserFromDb.toMap());
                            loginSuccess(currentUserFromDb.getBirthYear() != 0, firebaseUser.getUid());
                        } else {
                            Timber.w("Can`t find user in database!");
                            User user = new User(firebaseUser.getUid(), firebaseUser.getDisplayName(), firebaseUser.getEmail(), new Date().getTime(), provider);
                            currentUserReference.updateChildren(user.toMap());
                            loginSuccess(false, firebaseUser.getUid());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Timber.w(databaseError.toException(), "Listen user with id %1$s error", firebaseUser.getUid());
                    }
                });
            }
        });
    }

    private boolean validateForm(String email, String password) {
        if (!Utils.isEmailValid(email)) {
            vEmailEditText.requestFocus();
            vEmailEditText.setError(getString(R.string.emailErrorText));
            return false;
        } else {
            vEmailEditText.setError(null);
        }

        if (TextUtils.isEmpty(password) && password.length() < Const.MIN_PASSWORD_LENGTH) {
            vPasswordEditText.requestFocus();
            vPasswordEditText.setError(getString(R.string.passwordErrorText));
            return false;
        } else {
            vPasswordEditText.setError(null);
        }

        return true;
    }
}