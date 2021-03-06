package brains.mock.firebasetest;

import android.app.Application;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import timber.log.Timber;

public class FirebaseTestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new CrashReportingTree());
        }
    }

    private static class CrashReportingTree extends Timber.Tree {
        @Override
        protected void log(int priority, String tag, String message, Throwable t) {
            if (priority == Log.VERBOSE || priority == Log.DEBUG) {
                return;
            }

            FirebaseCrash.logcat(priority, tag, message);

            if (t != null) {
                if (priority == Log.ERROR) {
                    FirebaseCrash.report(t);
                } else if (priority == Log.WARN) {
                    FirebaseCrash.report(t);
                }
            }
        }
    }
}
