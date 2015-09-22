package com.example.binqi.hrs;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by binqi on 9/20/15.
 */
public class InputDialog extends DialogFragment{
    public interface InputDialogListener{
        public void onDialogPositiveClick(DialogFragment dialog,String name,String id);
    }
    InputDialogListener mListener;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        try{
            mListener = (InputDialogListener)activity;
        }catch(ClassCastException e){
            throw new ClassCastException(activity.toString() + "must implement listener");
        }
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_input, null);
        builder.setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        String patientName = ((EditText) dialogView.findViewById(R.id.input_name)).getText().toString();
                        String patientID = ((EditText) dialogView.findViewById(R.id.input_id)).getText().toString();
                        mListener.onDialogPositiveClick(InputDialog.this, patientName, patientID);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        InputDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
