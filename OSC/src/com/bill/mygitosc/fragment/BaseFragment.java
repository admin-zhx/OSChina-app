package com.bill.mygitosc.fragment;

import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;

import com.bill.mygitosc.R;
import com.bill.mygitosc.common.AppContext;

/**
 * Created by liaobb on 2015/8/6.
 */
public class BaseFragment extends Fragment {

    public AlertDialog.Builder generateAlterDialog() {
        int dialogTheme;
        if (AppContext.getInstance().getCurrentTheme() == R.style.AppBaseTheme) {
            dialogTheme = R.style.BlueDialogTheme;
        } else {
            dialogTheme = R.style.GreenDialogTheme;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), dialogTheme);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        return builder;
    }
}
