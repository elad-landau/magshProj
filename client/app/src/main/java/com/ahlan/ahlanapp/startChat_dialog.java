package com.ahlan.ahlanapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.EditText;

/**
 * Created by Administrator on 5/9/2017.
 */

public class startChat_dialog extends DialogFragment
{
    public interface NoticeDialogListener
    {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    NoticeDialogListener mListener;

    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        try
        {
            mListener = (NoticeDialogListener) activity;
        }
        catch(ClassCastException e)
        {
            Log.e("dialog","problem with casting :"+e.getMessage());
        }
    }


    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

       // builder.setTitle("enter the number you want to conact with");
        builder.setView(inflater.inflate(R.layout.dialog_startchat, null));
        builder.setPositiveButton("submit", new DialogInterface.OnClickListener()
        {
           public void onClick(DialogInterface dialog,int id)
           {
               mListener.onDialogPositiveClick(startChat_dialog.this);
           }
        });

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog,int id)
            {
                mListener.onDialogNegativeClick(startChat_dialog.this);
            }
        });

        return builder.create();
    }
}
