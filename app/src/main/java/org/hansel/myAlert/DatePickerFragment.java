package org.hansel.myAlert;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by hasus on 4/8/15.
 */
public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

//    private static final boolean DEBUG = Config.DEBUG;
    public static final String TAG = DatePickerFragment.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return new DatePickerDialog(getActivity(),this,year,month,day);

    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
//        if (DEBUG) Log.d(TAG, "DatePicker onDateSet: " + monthOfYear);
//        getMain().getAddDialogFragment().updateDateField(year, monthOfYear, dayOfMonth);
    }

    public MainActivity getMain() {
        return ((MainActivity)getActivity());
    }

}
