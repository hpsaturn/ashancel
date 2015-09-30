package org.linphone.gcm;

import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.hansel.myAlert.R;
import org.linphone.mediastream.Log;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;


/**
 * @author Sylvain Berfini
 */
// Warning ! Do not rename the service !
public class GCMService extends IntentService {

	public GCMService(){
        super("org.linphone.gcm.GCMService");
    }

    public GCMService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d("Push notification received");
		if (LinphoneManager.isInstanciated() && LinphoneManager.getLc().getCallsNb() == 0) {
			LinphoneManager.getLc().setNetworkReachable(false);
			LinphoneManager.getLc().setNetworkReachable(true);
		}
	}

	    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     *
     * @param ctx
     */

    public static void registerInBackground(final Context ctx) {


        final GoogleCloudMessaging[] gcm = {GoogleCloudMessaging.getInstance(ctx)};

        new AsyncTask(){

            @Override
            protected Object doInBackground(Object[] params) {

                String msg = "";
                try {
                    if (gcm[0] == null) {
                        gcm[0] = GoogleCloudMessaging.getInstance(ctx);
                    }
                    String regId = gcm[0].register(ctx.getString(R.string.push_sender_id));
                    msg = "[GCM] Device registered, registration ID=" + regId;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
//                    Settings.setPushToken(ctx,regid);
					Log.d("Registered push notification : " + regId);
					LinphonePreferences.instance().setPushNotificationRegistrationID(regId);
                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
//                storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "[GCM] Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;

            }

        }.execute();

    }

}
