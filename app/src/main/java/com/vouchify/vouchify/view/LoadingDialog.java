package com.vouchify.vouchify.view;

import android.app.Dialog;
import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import android.widget.ProgressBar;

import com.vouchify.vouchify.R;

public class LoadingDialog extends Dialog {

    /**
     * Show loading dialog
     *
     * @param context Context
     */
    public static LoadingDialog show(Context context) {
        LoadingDialog dialog = new LoadingDialog(context);
        dialog.setTitle("");
        dialog.setCancelable(false);
        dialog.addContentView(new ProgressBar(context),
                new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        dialog.show();

        return dialog;
    }

    public LoadingDialog(Context context) {
        super(context, R.style.newDialog);
    }
}
