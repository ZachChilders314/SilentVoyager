/*
Found on: https://developer.android.com/guide/topics/ui/controls/pickers#java
Copied pretty much exactly -- I do not take credit for this code.
 */
package com.x10host.burghporter31415.TimePicker;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;

public class DialogDateFragment extends DialogFragment
                            implements DatePickerDialog.OnDateSetListener {

    private PendingIntent pendingIntent;
    private int code;

    public void setPendingIntent(PendingIntent pendingIntent, int code) {
        this.pendingIntent = pendingIntent;
        this.code=code;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        try {
            Intent intent = new Intent();

            /*D1, D2, D3 are the data parameters being sent back*/
            intent.putExtra("d1", year);
            intent.putExtra("d2", month);
            intent.putExtra("d3", dayOfMonth);

            pendingIntent.send(getContext(), this.code, intent);

        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

}
