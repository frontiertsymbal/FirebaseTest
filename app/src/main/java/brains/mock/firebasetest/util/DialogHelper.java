package brains.mock.firebasetest.util;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;

import brains.mock.firebasetest.ui.dialog.ProgressDialog;

public class DialogHelper {

    //************************************************************************* Progress dialog methods **************************************************************************//
    public static void showProgressDialog(Context context, int textResId, boolean cancellable) {
        showProgressDialog(context, textResId, null, cancellable);
    }

    public static void showProgressDialog(Context context, String message, boolean cancellable) {
        showProgressDialog(context, 0, message, cancellable);
    }

    public static void showProgressDialog(Context context, boolean cancellable) {
        showProgressDialog(context, 0, null, cancellable);
    }

    private static void showProgressDialog(Context context, int textResId, String message, boolean cancellable) {
        if (context == null) return;
        FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
        Fragment prev = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if (prev != null) {
            ft.remove(prev);
        }

        ProgressDialog progressDialog;
        if (textResId != 0) {
            progressDialog = ProgressDialog.newInstance(textResId, cancellable);
        } else if (!TextUtils.isEmpty(message)) {
            progressDialog = ProgressDialog.newInstance(message, cancellable);
        } else {
            progressDialog = ProgressDialog.newInstance();
        }
        progressDialog.show(ft, ProgressDialog.TAG);
    }

    public static void dismissProgressDialog(Context context) {
        if (context == null) return;
        Fragment dialog = ((FragmentActivity) context).getSupportFragmentManager().findFragmentByTag(ProgressDialog.TAG);
        if (dialog != null) {
            FragmentTransaction ft = ((FragmentActivity) context).getSupportFragmentManager().beginTransaction();
            ft.remove(dialog);
            ft.commitAllowingStateLoss();
        }
    }

    // TODO AlexTsymbal: need to create observable dialog with timer in UI thread
    //****************************************************************************************************************************************************************************//

}
