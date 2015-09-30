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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import org.hancel.adapters.customUi.TrackDialog;
import org.hancel.customclass.TrackDate;
import org.hansel.myAlert.Log.Log;
import org.hansel.myAlert.Utils.PreferenciasHancel;
import org.hansel.myAlert.Utils.SimpleCrypto;
import org.hansel.myAlert.Utils.Util;
import org.hansel.myAlert.dataBase.TrackDAO;
import org.hansel.myAlert.dataBase.UsuarioDAO;
import org.hansel.myAlert.services.TrackLocationService;
import org.hansel.myAlert.services.StatusScheduleReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.internal.view.ContextThemeWrapper;
import android.view.View.OnClickListener;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PanicButtonFragment extends Fragment implements OnClickListener{	
	private static final int RQS_1 = 12;
	private static final boolean DEBUG = Config.DEBUG;
	private final int REQUEST_CODE = 0;
	private boolean running=false;
	private int range;
	private UsuarioDAO usuarioDao;
	private AlarmManager alarmManager;
	private TrackDAO track;	
	private View trackingOptions, trackInfo, statusTrack;
	private TextView txttrackingOptions, actionDescription;//, txtLastPanic;
	private TrackDate trackDate;
	private Button btnTracking, btnCancelCurrentTrack, btnModifyCurrentTrack, btnShareCurrentTrack;
    private LinearLayout panicScreenRoot;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View v = inflater.inflate(R.layout.fragment_panic, container,false);
		Button btnPanico = (Button)v.findViewById(R.id.btnAlert);
		//txtLastPanic =(TextView)v.findViewById(R.id.txtLastPanic);
		
		btnPanico.setOnClickListener(new View.OnClickListener() {						
			@Override
			public void onClick(View v) {
				AlertDialog.Builder alt_bld = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
				alt_bld.setMessage(getResources().getString(R.string.tracking_send_alert))
				.setCancelable(false)
				.setPositiveButton(getResources().getString(R.string.tracking_send_alert_yes), new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int id) {

//                        getActivity().startService(new Intent(getActivity(), SendPanicService.class));
                        ((MainActivity)getActivity()).getHardwareButtonService().sendAlertSMS();

						/*btnTracking.setText(getString(R.string.stop_tracking));
						btnTracking.setVisibility(View.VISIBLE);*/

						actionDescription.setText(getResources().getString(R.string.panic_sent));

						trackInfo.setVisibility(View.VISIBLE);
						trackingOptions.setVisibility(View.GONE);
					}
				})
				.setNegativeButton(getResources().getString(R.string.tracking_send_alert_no), 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();					
					}
				});

				AlertDialog alert = alt_bld.create();
				alert.setTitle(getResources().getString(R.string.alert_confirmation_title));
				alert.show();				
			}
		});

		panicScreenRoot = (LinearLayout)v.findViewById(R.id.panicScreenRoot);
		
		//layout for tracking status show (Progress bar)
		statusTrack = v.findViewById(R.id.statusTrack);

		//LinkButton to start tracking function
		btnTracking = (Button)v.findViewById(R.id.iniciaTrackId);
		usuarioDao = new UsuarioDAO(getActivity().getApplicationContext());
		usuarioDao.open();
		range = Util.getTrackingMinutes(getActivity().getApplicationContext());

		//Text to describe the Alarm action when is activated
		actionDescription = (TextView) v.findViewById(R.id.actionDescription);

		//Layout whith tracking Information
		trackingOptions = v.findViewById(R.id.layoutTrackOptions);

		//Information about the last tracking
		trackInfo = v.findViewById(R.id.layoutCurrentTrack);
		txttrackingOptions = (TextView)v.findViewById(R.id.txtUltimaAlerta);

		//Layout tracking Information buttons
		btnCancelCurrentTrack = (Button)v.findViewById(R.id.btnCancelCurrentTrack);
		btnModifyCurrentTrack = (Button)v.findViewById(R.id.btnModifyCurrentTrack);
		btnShareCurrentTrack = (Button)v.findViewById(R.id.btnShareCurrentTrack);
		btnCancelCurrentTrack.setOnClickListener(this);
		btnModifyCurrentTrack.setOnClickListener(this);
		btnShareCurrentTrack.setOnClickListener(this);

		showtrackingOptions(false);

		if(savedInstanceState!=null){
			running = savedInstanceState.getBoolean("run");
			if(running){
				Log.v("=== Traking esta corriendo");
				//btnTracking.setText(getString(R.string.stop_tracking));
				trackInfo.setVisibility(View.VISIBLE);
				trackingOptions.setVisibility(View.GONE);
				statusTrack.setVisibility(View.VISIBLE);
			}
			else{
				Log.v("=== Tracking no esta corriendo");				
				btnTracking.setText(getString(R.string.start_tracking));
				trackInfo.setVisibility(View.GONE);
				trackingOptions.setVisibility(View.GONE);
				statusTrack.setVisibility(View.GONE);
			}
		}
		btnTracking.setOnClickListener(this); 	
		//REASTREO
		return v;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		//txtLastPanic.setText(PreferenciasHancel.getLastPanicAlert(getActivity().getApplicationContext()));
		txttrackingOptions.setText(PreferenciasHancel.getLastPanicAlert(getActivity().getApplicationContext()));
		//tracking
		long time = PreferenciasHancel.getAlarmStartDate(getActivity());
		if(time != 0){
			Calendar currentTime = Calendar.getInstance();
			Calendar alarmTime = Calendar.getInstance();
			alarmTime.setTimeInMillis(time);
			//checking date to verify the tracking function is  running
			if(alarmTime.compareTo(currentTime)!=-1) {
				showtrackingOptions(true);
			}
			txttrackingOptions.setText(Util.getSimpleDateFormatTrack(alarmTime) );
		}
		running = Util.isTrackLocationServiceRunning(getActivity().getApplicationContext());
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//tracking
		alarmManager = (AlarmManager)getActivity().getSystemService(Activity.ALARM_SERVICE);
		track = new TrackDAO(getActivity().getApplicationContext());
		track.open();
		//tracking
		try{
			ActivaRadios();
		}
		catch(Exception ex){
			Log.v("Error al activar los radios!!!!");
			ex.printStackTrace();
		}
		Bundle datos = getArguments();

	}

	private void ActivaRadios(){	
		Log.v("Intentamos activar Datos");
		try {
			setMobileDataEnabled(getActivity().getApplicationContext(), true);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		Log.v("Activamos WIFI");
		//WifiManager wifiManager = (WifiManager)getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		//wifiManager.setWifiEnabled(true);
	}

	@SuppressWarnings("rawtypes")
	private void setMobileDataEnabled(Context context, boolean enabled)

            throws ClassNotFoundException, IllegalArgumentException, IllegalAccessException,
					NoSuchMethodException, NoSuchFieldException, InvocationTargetException {
        
        final ConnectivityManager conman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class<?> conmanClass = Class.forName(conman.getClass().getName());
        final Field iConnectivityManagerField = conmanClass.getDeclaredField("mService");
        iConnectivityManagerField.setAccessible(true);
        final Object iConnectivityManager = iConnectivityManagerField.get(conman);
        final Class<?> iConnectivityManagerClass =  Class.forName(iConnectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = iConnectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);
        setMobileDataEnabledMethod.invoke(iConnectivityManager, enabled);
	}

	//tracking
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data!=null){
			cancelAlarms();
			PreferenciasHancel.setReminderCount(getActivity(), 0);
			trackDate = (TrackDate) data.getExtras().get(TrackDialog.TRACK_EXTRA);
			alarmManager.set(AlarmManager.RTC_WAKEUP, trackDate.getStartTimeTrack().getTimeInMillis(), Util.getReminderPendingIntennt(getActivity()));
			Log.v("Finalizando en: " + new Date(trackDate.getEndTimeTrack().getTimeInMillis()));
			alarmManager.set(AlarmManager.RTC_WAKEUP, trackDate.getEndTimeTrack().getTimeInMillis(), Util.getStopSchedulePendingIntentWithExtra(getActivity()));
			//guardamos inicio de alarma
			PreferenciasHancel.setAlarmStartDate(getActivity(), trackDate.getEndTimeTrack().getTimeInMillis());
			Log.v("=== OnActivityResult");
			showtrackingOptions(false); //TRUE
			txttrackingOptions.setText(Util.getSimpleDateFormatTrack(trackDate.getStartTimeTrack()));
            panicScreenRoot.postInvalidate();
		}
		else{
			super.onActivityResult(requestCode, resultCode, data);
		}

    }

    @Override
	public void onPause() {
		super.onPause();
	}

	@Override  //tracking
	public void onDestroy() {
		super.onDestroy();
		try {
			if(track!=null)	{
				track.close();
			}
		} 
		catch (Exception e) {
		}
	}

	@Override //RASTREO
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnCancelCurrentTrack:			
		case R.id.btnModifyCurrentTrack:
			createPasswordDialog((Button) v);	
			break;
		case R.id.btnShareCurrentTrack:
            Log.v("=== Click en compartir Traza!!!");
			shareTrace();
			break;
		case R.id.iniciaTrackId:
			if(!running){
				startActivityForResult(new Intent(getActivity(), 
						TrackDialog.class),REQUEST_CODE );				
			}
			else{				
				createPasswordDialog(btnTracking);
			}
			break;
		default:
			break;
		}
	}
	
	@Override  //tracking
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		if(outState!=null){
			outState.putBoolean("run", running);
		}
	}

	//tracking
	protected void createPasswordDialog(final Button btnPanico) {
		AlertDialog.Builder alert = new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogCustom));
		alert.setTitle(getResources().getString(R.string.tracking_cancel_password));  
		alert.setMessage(getResources().getString(R.string.tracking_password));                

		// Set an EditText view to get user input   
		final EditText input = new EditText(getActivity());
		input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		alert.setView(input);

		alert.setPositiveButton(getResources().getString(R.string.tracking_cancel_ok), new DialogInterface.OnClickListener() {  
			public void onClick(DialogInterface dialog, int whichButton) {  
				String value = input.getText().toString();
				String crypto = SimpleCrypto.md5(value);	  

				boolean isOK = usuarioDao.getPassword(crypto);	           	          

				if(isOK && btnPanico.getId()== R.id.iniciaTrackId ){
					Log.v("===Detener Rastreo");
					alarmManager.cancel(getPendingAlarm());
					btnPanico.setText(getString(R.string.start_tracking));
					trackInfo.setVisibility(View.GONE);
					trackingOptions.setVisibility(View.GONE);
					Util.setRunningService(getActivity().getApplicationContext(), false);
				    stopTrackLocationService();
					alarmManager.cancel(Util.getPendingAlarmPanicButton(getActivity().getApplicationContext()));
					running = false;

					Toast.makeText(getActivity(), getResources().getString(R.string.tracking_stopped), Toast.LENGTH_SHORT).show();
					PreferenciasHancel.setAlarmStartDate(getActivity(), 0);

					return;                  
				}
				else if(isOK && btnPanico.getId() == R.id.btnCancelCurrentTrack ){
					//cancelamos alarma para iniciar servicio
					//alarmManager.cancel(Util.getServicePendingIntent (getActivity()));
					cancelAlarms();
					Toast.makeText(getActivity(), getResources().getString(R.string.tracking_stopped),
							Toast.LENGTH_SHORT).show();
					showtrackingOptions(false);
					PreferenciasHancel.setAlarmStartDate(getActivity(), 0);
				}
				else if(isOK && btnPanico.getId()==R.id.btnModifyCurrentTrack){
					startActivityForResult(new Intent(getActivity(), TrackDialog.class),REQUEST_CODE );

				}
				else{
					Toast.makeText(getActivity(), getResources().getString(R.string.tracking_wrong_password), Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
				return;   
			}
		});
		alert.show();

	}

	private void stopTrackLocationService() {
        if(DEBUG)Log.v("[MainActivity] stopTrackLocationService");
        StatusScheduleReceiver.stopSheduleService(getActivity());
        getActivity().stopService(new Intent(getActivity(), TrackLocationService.class));
    }

	private void shareTrace() {
		Intent share = new Intent(android.content.Intent.ACTION_SEND);
		share.setType("text/plain");
		share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
		share.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.hancel_track_title));

		String h = getString(R.string.tracking_map_url) + SimpleCrypto.md5(String.valueOf(Util.getLastTrackId(getActivity().getApplicationContext())));
		String message = getString(R.string.share_trace_message).replace("%map", h);

        Log.v("=== Mensaje para traza: " + message);
		share.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(Intent.createChooser(share, getString(R.string.hancel_track_window_title)));
	}
	

	private void cancelAlarms() {
		alarmManager.cancel(Util.getReminderPendingIntennt(getActivity()));
		alarmManager.cancel(Util.getStopSchedulePendingIntentWithExtra(getActivity()));
		showtrackingOptions(false);
        stopTrackLocationService();
	}
	

	private PendingIntent getPendingAlarm()	{
		Intent intent = new Intent(getActivity().getApplicationContext(), AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getActivity(getActivity(), RQS_1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		return pendingIntent;
	}

	private void showtrackingOptions(boolean showTrackInfo){
		Log.v("=== ShowTrackInfo " + showTrackInfo);
		
		if(trackInfo.getVisibility() == View.GONE)
			Log.v("=== TrackInfo es GONE " +  View.GONE);
		
		if(trackInfo.getVisibility() == View.VISIBLE)
			Log.v("=== TrackInfo es VISIBLE " + View.VISIBLE );
		
		if(showTrackInfo){			
			trackingOptions.setVisibility(View.VISIBLE);
            actionDescription.setText(getString(R.string.tracking_started));
			statusTrack.setVisibility(View.VISIBLE);
			trackInfo.setVisibility(View.VISIBLE);
			btnTracking.setVisibility(View.GONE);
		}
		else{
			trackingOptions.setVisibility(View.GONE);
			statusTrack.setVisibility(View.GONE);
			trackInfo.setVisibility(View.GONE);
			btnTracking.setVisibility(View.VISIBLE);
            actionDescription.setText(getString(R.string.panic_tracking_description));
		}
	}
}
