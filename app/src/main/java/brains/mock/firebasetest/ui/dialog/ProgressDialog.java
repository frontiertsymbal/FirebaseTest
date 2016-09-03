package brains.mock.firebasetest.ui.dialog;

import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import brains.mock.firebasetest.R;

public class ProgressDialog extends DialogFragment {

    public static final String TAG = ProgressDialog.class.getSimpleName();
    private static boolean DIALOG_CANCELABLE = true;
    private static final String KEY_DIALOG_CANCELABLE = "DIALOG_CANCELABLE";
    private static final String KEY_STRING_RESOURCE = "STRING_RESOURCE";
    private static final String KEY_MESSAGE = "MESSAGE";

    public static ProgressDialog newInstance() {
        return newInstance(R.string.loading);
    }

    public static ProgressDialog newInstance(int message) {
        return newInstance(message, null, DIALOG_CANCELABLE);
    }

    public static ProgressDialog newInstance(String message) {
        return newInstance(0, message, DIALOG_CANCELABLE);
    }

    public static ProgressDialog newInstance(String message, boolean cancellable) {
        return newInstance(0, message, cancellable);
    }

    public static ProgressDialog newInstance(int message, boolean cancellable) {
        return newInstance(message, null, cancellable);
    }

    private static ProgressDialog newInstance(@StringRes int messageResId, String message, boolean cancelable) {
        ProgressDialog loadDataProgressDialog = new ProgressDialog();

        Bundle bundle = new Bundle();
        bundle.putInt(KEY_STRING_RESOURCE, messageResId);
        bundle.putString(KEY_MESSAGE, message);
        bundle.putBoolean(KEY_DIALOG_CANCELABLE, cancelable);
        loadDataProgressDialog.setArguments(bundle);

        return loadDataProgressDialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setCancelable(getArguments().getBoolean(KEY_DIALOG_CANCELABLE));
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        @StringRes int messageResId = getArguments().getInt(KEY_STRING_RESOURCE);
        String message = getArguments().getString(KEY_MESSAGE);

        View view = inflater.inflate(R.layout.progress_dialog, null);

        ProgressBar v = (ProgressBar) view.findViewById(R.id.loadingProgress);
        v.getIndeterminateDrawable().setColorFilter(ContextCompat.getColor(getContext(), R.color.progressColor),
                PorterDuff.Mode.SRC_IN);

        TextView messageView = (TextView) view.findViewById(R.id.dialogMessage);
        if (messageResId != 0) {
            messageView.setText(messageResId);
        } else {
            messageView.setText(message);
        }

        return view;
    }
}
