package org.hansel.myAlert;
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
import java.io.IOException;
import java.util.Calendar;

import org.hansel.myAlert.Log.Log;
import org.hansel.myAlert.Utils.Util;
import org.hansel.myAlert.dataBase.UsuarioDAO;
import org.hansel.myAlert.services.TrackLocationService;
import org.hansel.myAlert.services.StatusScheduleReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;
import android.widget.Toast;


public class AlarmFragment extends DialogFragment   {
	private static final boolean DEBUG = Config.DEBUG;
	private  AlarmManager alarmManager;
	private UsuarioDAO usuarioDao;
	private MediaPlayer mMediaPlayer;
	private Vibrator mVibrator;
	//handler para quitar la vibraci�n en caso que no se apague la alarma
	private Handler handler = new Handler();
	private Runnable stop= new Runnable() {
		@Override
		public void run() {
			if(mVibrator!=null)
			{
				mVibrator.cancel();
			}
			if(mMediaPlayer!=null)
			{
				mMediaPlayer.stop();
			}
		}
	};
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		try {
			if(mMediaPlayer.isPlaying())
					mMediaPlayer.stop();
		} catch (Exception e) {
			
		}
		mVibrator.cancel();
		handler.removeCallbacks(stop);
	}

	private void Vibra(){
		int dot = 200;      // Length of a Morse Code "dot" in milliseconds
		int dash = 500;     // Length of a Morse Code "dash" in milliseconds
		int short_gap = 200;    // Length of Gap Between dots/dashes
		int medium_gap = 500;   // Length of Gap Between Letters
		int long_gap = 1000;    // Length of Gap Between Words
		long[] pattern = {
				0,  // Start immediately
				dot, short_gap, dot, short_gap, dot,    // s
				medium_gap,
				dash, short_gap, dash, short_gap, dash, // o
				medium_gap,
				dot, short_gap, dot, short_gap, dot,    // s
				long_gap
		};
		mVibrator.vibrate(pattern, 0);
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		getActivity().getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD);
		mVibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
		alarmManager = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
		usuarioDao = new UsuarioDAO(getActivity().getApplicationContext());
		usuarioDao.open();
		setAlarm();
		Vibra();
		// tocamos la alarma:
		String _uri = Util.getRingtone(getActivity().getApplicationContext());
		if(_uri != null && _uri.length() > 0){

			//handler.post(vibrate);
			playSound(getActivity().getApplicationContext(), Uri.parse(_uri));
		}
		//preparamos handler para terminar alarma;
		handler.postDelayed(stop, 1000*60*2);		
		AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());                 
		alert.setTitle(getString(R.string.alarm_cancel_password));  
		alert.setMessage(R.string.Contrasenia);                

		// Set an EditText view to get user input   
		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		alert.setView(input);

		alert.setPositiveButton(getString(R.string.alarm_accept), new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				String value = input.getText().toString();
				
				if(usuarioDao.getPassword(value.trim())){ //buscar en la BD la contraseña
					Log.v("Detener Rastreo");
//					getActivity().stopService(new Intent(getActivity().getApplicationContext(),LocationManagement.class));
					stopTrackLocationService();

					alarmManager.cancel(Util.getPendingAlarmPanicButton(getActivity().getApplicationContext()));

					Toast.makeText(getActivity(), getString(R.string.tracking_stopped), Toast.LENGTH_SHORT).show();
					getActivity().finish();
					return;                  
				}
				else{
					Toast.makeText(getActivity(), getString(R.string.wrong_passwd), 
							Toast.LENGTH_SHORT).show();
					return;
				}
			}
		});  

		alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int which) {

				getActivity().finish();
				return;   
			}
		});

		return alert.create();

	}


    private void stopTrackLocationService() {
        if(DEBUG)Log.v("[MainActivity] stopTrackLocationService");
        StatusScheduleReceiver.stopSheduleService(getActivity());
        getActivity().stopService(new Intent(getActivity(), TrackLocationService.class));
    }

	private void setAlarm() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, Util.getPanicDelay(getActivity().getApplicationContext()));
		alarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), 
				Util.getPendingAlarmPanicButton(getActivity().getApplicationContext()));

	}
	private void playSound(Context context, Uri alert) {
		mMediaPlayer = new MediaPlayer();
		try {
			mMediaPlayer.setDataSource(context, alert);
			final AudioManager audioManager = (AudioManager) context
					.getSystemService(Context.AUDIO_SERVICE);
			if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
				mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
				mMediaPlayer.prepare();
				mMediaPlayer.start();
			}
		} catch (IOException e) {
			System.out.println("Error tocando alarma");
		}
	}


	public void show() {
		getDialog().show();
	}
}
