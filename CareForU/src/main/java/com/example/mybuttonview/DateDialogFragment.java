package com.example.mybuttonview;

import android.support.v7.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

/**
 * Created by Administrator on 2018/1/2.
 */

public class DateDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    String datestring;
    String dayofweek;

    public interface NoticeDialogListener {
        public void onDateDialogDateSet(DialogFragment dialog);
    }
    // Use this instance of the interface to deliver action events

   NoticeDialogListener mListener;
    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener


    public NoticeDialogListener getListener(){
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) getActivity();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
        return mListener;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), R.style.DialogStyle, this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        mListener=getListener();
        datestring=""+year+"年"+(month+1)+"月"+dayOfMonth+"日";
        Calendar c=Calendar.getInstance();
        c.set(year,month,dayOfMonth);
        dayofweek=""+c.get(Calendar.DAY_OF_WEEK);
        mListener.onDateDialogDateSet(this);
    }

    public String getDateString(){
        return datestring;
    }

    public String getDayofWeek(){return dayofweek;}
}
