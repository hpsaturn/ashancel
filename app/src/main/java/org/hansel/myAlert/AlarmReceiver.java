package org.hansel.myAlert;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class AlarmReceiver extends FragmentActivity{
	
	@Override
	protected void onStart() {
		super.onStart();
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AlarmFragment alarm = new AlarmFragment();
		alarm.show();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	


}
