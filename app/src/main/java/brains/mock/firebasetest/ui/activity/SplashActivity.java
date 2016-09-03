package brains.mock.firebasetest.ui.activity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import brains.mock.firebasetest.R;
import brains.mock.firebasetest.model.User;
import brains.mock.firebasetest.rxfirebase.RxFirebase;
import brains.mock.firebasetest.util.Const;
import brains.mock.firebasetest.util.NetworkUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import timber.log.Timber;

public class SplashActivity extends BaseActivity {

    private String currentUserUID;
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        NetworkUtil.checkNetworkWithAlertDialogAndRetry(this, new NetworkUtil.ActionCallback() {
            @Override
            public void onAction() {
                mCompositeSubscription.add(
                        Observable.zip(Observable.timer(Const.SPLASH_SCREEN_TIMEOUT, TimeUnit.SECONDS), RxFirebase.Auth.observeAuthState(mFirebaseAuth), new Func2<Long, FirebaseUser, FirebaseUser>() {
                            @Override
                            public FirebaseUser call(Long aLong, FirebaseUser firebaseUser) {
                                return firebaseUser;
                            }
                        }).flatMap(new Func1<FirebaseUser, Observable<?>>() {
                            @Override
                            public Observable<?> call(FirebaseUser firebaseUser) {
                                if (firebaseUser != null) {
                                    currentUserUID = firebaseUser.getUid();
                                    Query userQuery = FirebaseDatabase.getInstance()
                                            .getReference(Const.REFERENCE_USER)
                                            .child(currentUserUID);
                                    return RxFirebase.Database.observeSingleValue(userQuery, User.class);
                                } else {
                                    return Observable.just(null);
                                }
                            }
                        }).subscribe(new Action1<Object>() {
                            @Override
                            public void call(final Object result) {
                                Timber.i("User logged in = %1$s", result != null);

                                Intent intent;
                                if (result != null) {
                                    User user = (User) result;
                                    if (user.getBirthYear() != 0) {
                                        intent = new Intent(SplashActivity.this, ChatListActivity.class);
                                    } else {
                                        intent = new Intent(SplashActivity.this, UserSettingsActivity.class);
                                    }
                                    intent.putExtra(Const.EXTRA_USER_UID, currentUserUID);
                                } else {
                                    intent = new Intent(SplashActivity.this, LoginActivity.class);
                                }

                                startActivity(intent);
                                finish();
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Timber.e(throwable, "Error getting user data");

                                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        })
                );
            }
        });
    }
}
