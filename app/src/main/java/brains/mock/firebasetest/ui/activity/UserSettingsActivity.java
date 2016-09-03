package brains.mock.firebasetest.ui.activity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import brains.mock.firebasetest.R;
import brains.mock.firebasetest.model.User;
import brains.mock.firebasetest.ui.view.maskededittext.MaskedEditText;
import brains.mock.firebasetest.util.Const;
import brains.mock.firebasetest.util.DialogHelper;
import brains.mock.firebasetest.util.NetworkUtil;
import brains.mock.firebasetest.util.Utils;

import java.util.Date;

import timber.log.Timber;

public class UserSettingsActivity extends BaseActivity implements View.OnClickListener {

    private String mUserUID;

    private EditText vNameField;
    private EditText vEmailField;
    private EditText vNickNameField;
    private MaskedEditText vPhoneNumberField;
    private EditText vBirthDateField;
    private Button vSaveButton;
    private Button vLogOutButton;

    private String mNameTemp;
    private String mNickNameTemp;
    private String mPhoneNumberTemp;
    private int mBirthYearTemp;
    private int mBirthMonthTemp;
    private int mBirthDayTemp;

    private User mCurrentUser;
    private DatabaseReference mUserReference = FirebaseDatabase.getInstance().getReference(Const.REFERENCE_USER);
    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);

        DialogHelper.showProgressDialog(UserSettingsActivity.this, false);

        vNameField = (EditText) findViewById(R.id.userNameField);
        vEmailField = (EditText) findViewById(R.id.emailField);
        vNickNameField = (EditText) findViewById(R.id.nickNameField);
        vPhoneNumberField = (MaskedEditText) findViewById(R.id.phoneNumberField);
        vBirthDateField = (EditText) findViewById(R.id.birthDateField);

        vSaveButton = (Button) findViewById(R.id.saveButton);
        vLogOutButton = (Button) findViewById(R.id.logOutButton);

        vBirthDateField.setOnClickListener(this);
        vSaveButton.setOnClickListener(this);
        vLogOutButton.setOnClickListener(this);

        mUserUID = getIntent().getStringExtra(Const.EXTRA_USER_UID);

        NetworkUtil.checkNetworkWithAlertDialogAndRetry(this, new NetworkUtil.ActionCallback() {
            @Override
            public void onAction() {
                mUserReference.child(mUserUID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User currentUserFromDb = dataSnapshot.getValue(User.class);
                                if (currentUserFromDb != null) {
                                    initTempVarsAndFields(currentUserFromDb);

                                    //Init google play services is user logged in with Google account
                                    if (currentUserFromDb.getProvider().equals(Const.PROVIDER_GOOGLE)) {
                                        mGoogleApiClient = new GoogleApiClient.Builder(UserSettingsActivity.this)
                                                .enableAutoManage(UserSettingsActivity.this, new GoogleApiClient.OnConnectionFailedListener() {
                                                    @Override
                                                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                                                        Timber.w(TAG, "onConnectionFailed: %1$s", connectionResult);
                                                        Toast.makeText(UserSettingsActivity.this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addApi(Auth.GOOGLE_SIGN_IN_API)
                                                .build();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Timber.w(databaseError.toException(), "onCancelled");
                            }
                        });
            }
        });
    }


    private void initTempVarsAndFields(User currentUserFromDb) {
        vSaveButton.requestFocus();
        mCurrentUser = currentUserFromDb;

        mNameTemp = currentUserFromDb.getUserName();
        mNickNameTemp = currentUserFromDb.getNickName();
        mPhoneNumberTemp = currentUserFromDb.getPhoneNumber();
        mBirthYearTemp = currentUserFromDb.getBirthYear();
        mBirthMonthTemp = currentUserFromDb.getBirthMonth();
        mBirthDayTemp = currentUserFromDb.getBirthDay();

        vEmailField.setText(currentUserFromDb.getEmail());

        if (!TextUtils.isEmpty(mNameTemp)) {
            vNameField.setText(mNameTemp);
            if (currentUserFromDb.getProvider().equals(Const.PROVIDER_GOOGLE)) {
                vNameField.setFocusable(false);
                vNameField.setFocusableInTouchMode(false);
            } else {
                vNameField.requestFocus();
                vNameField.setSelection(mNameTemp.length());
            }
        }
        if (!TextUtils.isEmpty(mNickNameTemp)) {
            vNickNameField.setText(mNickNameTemp);
            if (currentUserFromDb.getProvider().equals(Const.PROVIDER_GOOGLE)) {
                vNickNameField.setSelection(mNickNameTemp.length());
            }
        }

        if (!TextUtils.isEmpty(mPhoneNumberTemp)) {
            if (mPhoneNumberTemp.startsWith("8")) {
                vPhoneNumberField.setText(mPhoneNumberTemp.substring(1));
            } else if (mPhoneNumberTemp.startsWith("38")) {
                vPhoneNumberField.setText(mPhoneNumberTemp.substring(2));
            } else if (mPhoneNumberTemp.startsWith("+38")) {
                vPhoneNumberField.setText(mPhoneNumberTemp.substring(3));
            } else {
                vPhoneNumberField.setText(mPhoneNumberTemp);
            }
        }

        if (mBirthDayTemp != 0 && mBirthMonthTemp != 0 && mBirthYearTemp != 0) {
            vBirthDateField.setText(getString(R.string.dateTemplate,
                    Utils.formatNumberString(mBirthDayTemp),
                    Utils.formatNumberString(mBirthMonthTemp),
                    String.valueOf(mBirthYearTemp)));
        }
        DialogHelper.dismissProgressDialog(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.birthDateField:
                openDataPicker();
                break;
            case R.id.saveButton:
                NetworkUtil.checkNetworkWithAlertDialogAndRetry(UserSettingsActivity.this, new NetworkUtil.ActionCallback() {
                    @Override
                    public void onAction() {
                        checkFieldsAndSave();
                    }
                });
                break;
            case R.id.logOutButton:
                DialogHelper.showProgressDialog(UserSettingsActivity.this, false);
                NetworkUtil.checkNetworkWithAlertDialogAndRetry(UserSettingsActivity.this, new NetworkUtil.ActionCallback() {
                    @Override
                    public void onAction() {
                        FirebaseAuth.getInstance().signOut();
                        if (mCurrentUser.getProvider().equals(Const.PROVIDER_GOOGLE)) {
                            Auth.GoogleSignInApi.signOut(mGoogleApiClient)
                                    .setResultCallback(new ResultCallback<Status>() {
                                        @Override
                                        public void onResult(@NonNull Status status) {
                                            openSplashScreen();
                                        }
                                    });
                        } else {
                            openSplashScreen();
                        }
                    }
                });
                break;
        }
    }

    private void openSplashScreen() {
        DialogHelper.dismissProgressDialog(this);
        Intent intent = new Intent(UserSettingsActivity.this, SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void checkFieldsAndSave() {
        String currentName = vNameField.getText().toString().trim();
        String currentNickName = vNickNameField.getText().toString().trim();
        String currentPhoneNumber = Utils.refactorPhone(vPhoneNumberField.getText().toString().trim());

        if (!TextUtils.isEmpty(currentName)) {
            vNameField.setError(null);
            mCurrentUser.setUserName(currentName);
        } else {
            vNameField.setError(getString(R.string.enterUserNameError));
            vNameField.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(currentNickName)) {
            vNickNameField.setError(null);
            mCurrentUser.setNickName(currentNickName);
        } else {
            vNickNameField.setError(getString(R.string.enterNickNameError));
            vNickNameField.requestFocus();
            return;
        }
        if (!TextUtils.isEmpty(vBirthDateField.getText().toString().trim())) {
            vBirthDateField.setError(null);
        } else {
            vBirthDateField.setError(getString(R.string.birthdayError));
            vBirthDateField.requestFocus();
            return;
        }

        if (!TextUtils.isEmpty(currentPhoneNumber)) {
            mCurrentUser.setPhoneNumber(currentPhoneNumber);
        }
        mCurrentUser.setLastAvailableTime(new Date().getTime());

        mUserReference.child(mUserUID).updateChildren(mCurrentUser.toMap());

        Intent intent = new Intent(this, ChatListActivity.class);
        intent.putExtra(Const.EXTRA_USER_UID, mUserUID);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (!isFieldsChanged()) {
            super.onBackPressed();
        } else {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.userSettingsWarning))
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UserSettingsActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    }).create();
            dialog.show();
        }
    }

    private boolean isFieldsChanged() {
        String currentName = vNameField.getText().toString().trim();
        String currentNickName = vNickNameField.getText().toString().trim();
        String currentPhoneNumber = Utils.refactorPhone(vPhoneNumberField.getText().toString().trim());
        if (mNameTemp != null && !mNameTemp.equals(currentName)) {
            return true;
        }
        if (mNickNameTemp != null && !mNickNameTemp.equals(currentNickName)) {
            return true;
        }
        if (mPhoneNumberTemp != null && !mPhoneNumberTemp.equals(currentPhoneNumber)) {
            return true;
        }
        if (mBirthYearTemp != mCurrentUser.getBirthYear()) {
            return true;
        }
        if (mBirthMonthTemp != mCurrentUser.getBirthMonth()) {
            return true;
        }
        if (mBirthDayTemp != mCurrentUser.getBirthDay()) {
            return true;
        }

        return false;
    }

    private void openDataPicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(UserSettingsActivity.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                mCurrentUser.setBirthDay(datePicker.getDayOfMonth());
                mCurrentUser.setBirthMonth(datePicker.getMonth() + 1);
                mCurrentUser.setBirthYear(datePicker.getYear());

                vBirthDateField.setText(getString(R.string.dateTemplate,
                        Utils.formatNumberString(mCurrentUser.getBirthDay()),
                        Utils.formatNumberString(mCurrentUser.getBirthMonth()),
                        String.valueOf(mCurrentUser.getBirthYear())));
            }
        },
                mCurrentUser.getBirthYear() != 0 ? mCurrentUser.getBirthYear() : 1970, //For default set 1970
                mCurrentUser.getBirthMonth() != 0 ? mCurrentUser.getBirthMonth() : 0, //For default set January
                mCurrentUser.getBirthDay() != 0 ? mCurrentUser.getBirthDay() : 1); //For default set 1
        datePickerDialog.show();
    }
}
