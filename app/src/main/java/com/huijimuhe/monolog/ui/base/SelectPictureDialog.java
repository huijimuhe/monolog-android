package com.huijimuhe.monolog.ui.base;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class SelectPictureDialog extends DialogFragment {

    public static SelectPictureDialog newInstance() {
        return new SelectPictureDialog();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        String[] items = {"相机", "相册"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setTitle("选择图片").setItems(items,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                mOnItemClick.onItemClick(dialog, item);
                            }
                        });
        return builder.create();
    }

    private OnItemClickListener mOnItemClick;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClick = listener;
    }

    public interface OnItemClickListener {
        public void onItemClick(DialogInterface dialog, int item);
    }
}
