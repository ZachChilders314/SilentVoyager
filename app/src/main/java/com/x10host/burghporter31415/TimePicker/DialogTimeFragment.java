/*
Found on: https://developer.android.com/guide/topics/ui/controls/pickers#java
Copied pretty much exactly -- I do not take credit for this code.
 */

package com.x10host.burghporter31415.TimePicker;

import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.x10host.burghporter31415.internetservices.InternetPHPRequestService;

import java.util.Calendar;

public class DialogTimeFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    private PendingIntent pendingIntent;
    private int code;

    public void setPendingIntent(PendingIntent pendingIntent, int code) {
        this.pendingIntent = pendingIntent;
        this.code = code;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        try {
            pendingIntent.send(getContext(), this.code, new Intent());
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }

    }
}