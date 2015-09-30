package org.hancel.customclass;
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

import org.hansel.myAlert.R;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CalendarView;
import android.widget.LinearLayout;


public class PickCalendar extends FragmentActivity{
	private Calendar currentDate = Calendar.getInstance();
	private final View.OnClickListener mActionBarListener = new View.OnClickListener() {
	    @Override
	    public void onClick(View v) {
	        onActionBarItemSelected(v.getId());
	    }
	};

	private boolean onActionBarItemSelected(int itemId) {
	    switch (itemId) {
	    case R.id.action_done:
	    	Intent i = new Intent();
	    	i.putExtra("DATE", currentDate);
	    	setResult(RESULT_OK, i);
	    	finish();
	        break;
	    case R.id.action_cancel:
	        System.err.println("cancel");
	        this.onBackPressed();
	        break;
	    }
	    return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pick_calendar);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setTitle("Pick Date");
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		 View actionBarButtons = inflater.inflate(R.layout.edit_event_custom_actionbar, new LinearLayout(this), false);

		    View cancelActionView = actionBarButtons.findViewById(R.id.action_cancel);
		    cancelActionView.setOnClickListener(mActionBarListener);

		    View doneActionView = actionBarButtons.findViewById(R.id.action_done);
		    doneActionView.setOnClickListener(mActionBarListener);
		    getActionBar().setHomeButtonEnabled(false);
		    getActionBar().setDisplayShowHomeEnabled(false);
		    getActionBar().setDisplayHomeAsUpEnabled(false);
		    getActionBar().setDisplayShowTitleEnabled(false);

		    getActionBar().setDisplayShowCustomEnabled(true);
		    getActionBar().setCustomView(actionBarButtons);
		    CalendarView calendar = (CalendarView)findViewById(R.id.calendarWeight);
		    
		    calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
			@Override
			public void onSelectedDayChange(CalendarView view, int year, int month,
					int dayOfMonth) {
				currentDate.set(year, month, dayOfMonth);
				
			}
		});	    
	
	}

}
