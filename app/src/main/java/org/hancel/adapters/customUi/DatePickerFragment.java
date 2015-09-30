package org.hancel.adapters.customUi;
/*This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
Created by Javier Mejia @zenyagami
zenyagami@gmail.com
	*/

import java.util.Calendar;

import org.hansel.myAlert.Log.Log;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.widget.DatePicker;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener, DatePicker.OnDateChangedListener {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        DatePickerDialog dt = new DatePickerDialog(getActivity(), (TrackDialog) getActivity(), year, month, day);
        return dt;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {

        Log.v("fecha");
        Calendar cal = Calendar.getInstance();
        cal.set(year,month,day);
        ((TrackDialog)getActivity()).setTrackDate(cal);

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        Log.v("fecha");
    }
}