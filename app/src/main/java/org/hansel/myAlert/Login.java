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


import static android.content.Intent.ACTION_MAIN;

import java.util.Calendar;

import org.hansel.myAlert.Log.Log;
import org.hansel.myAlert.Utils.PreferenciasHancel;
import org.hansel.myAlert.Utils.SimpleCrypto;
import org.hansel.myAlert.Utils.Util;
import org.hansel.myAlert.WelcomeInfo.ScreenSlidePageAdapter;
import org.hansel.myAlert.dataBase.UsuarioDAO;
import org.linphone.LinphoneManager;
import org.linphone.LinphonePreferences;
import org.linphone.LinphoneService;
import org.linphone.core.LinphoneAddress;
import org.linphone.core.LinphoneAddress.TransportType;
import org.linphone.core.LinphoneAuthInfo;
import org.linphone.core.LinphoneCore;
import org.linphone.core.LinphoneCore.RegistrationState;
import org.linphone.core.LinphoneCoreException;
import org.linphone.core.LinphoneCoreFactory;
import org.linphone.core.LinphoneProxyConfig;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Login.Java
 * Login process for a Hancel User
 * @author Javier Mejia, izel
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class Login extends FragmentActivity {

	private String mUser, mPasswd, mErrores;
	private EditText user, passwd;
	private TextView errores;
	private View  mLoginStatusView; /*, mLoginFormView;*/
	private TextView mLoginStatusMessageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.login_layout);				
				
		if(PreferenciasHancel.getLoginOk(getApplicationContext())){
			Intent i = new Intent(getApplicationContext(),MainActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK 
					| Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
		}
			
		user = (EditText)findViewById(R.id.txtUser);
		passwd = (EditText) findViewById(R.id.txtPassword);
						
		Button btnLogin = (Button)findViewById(R.id.btnLogin);
		btnLogin.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if(AttempLogin() && isAuthenticated()){					
					String crypto = SimpleCrypto.md5(mPasswd);
					UsuarioDAO usuarioDAO = new UsuarioDAO(LinphoneService
							.instance().getApplicationContext());
					usuarioDAO.open();
					usuarioDAO.Insertar(mUser,crypto,"");
					usuarioDAO.close();
					Util.setLoginOkInPreferences(getApplicationContext(), true);					
					PreferenciasHancel.setUserId(getApplicationContext(), 
							(int)Calendar.getInstance().getTimeInMillis());
					//Util.insertNewTrackId(getApplicationContext(), 0);
					//launchMainActivity();	
					//finish();
				}
				else{
					errores.setText(mErrores);
					errores.setVisibility(View.VISIBLE);
					Util.setLoginOkInPreferences(getApplicationContext(), false);
				}
			}
		});

		Button register = (Button)findViewById(R.id.btnRegister);
		register.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent i = new Intent(getApplicationContext(), Registro.class);
				Util.setLoginOkInPreferences(getApplicationContext(), false);
				startActivity(i);
			}
		});

		errores =(TextView)findViewById(R.id.tvError);
		//mLoginFormView = findViewById(R.id.login_form1);
		//mLoginStatusView = findViewById(R.id.login_status1);
		//mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message1);

		ViewPager mpager = (ViewPager) findViewById(R.id.pager);
		PagerAdapter mpagerAdapter = new ScreenSlidePageAdapter(getSupportFragmentManager());
		mpager.setAdapter(mpagerAdapter);
	}

	
	/**
	 * Checking if required fields are filled
	 */
	private boolean AttempLogin(){
		user.setError(null);
		passwd.setError(null);		
		mUser = user.getText().toString().toLowerCase();
		mPasswd = passwd.getText().toString();
		
		boolean cancel = false;
		View focusView = null;

		// Check for a valid user and password.
		if (TextUtils.isEmpty(mUser)) {
			user.setError(getString(R.string.error_field_required));
			focusView = user;
			cancel = true;
		} 
		if (TextUtils.isEmpty(mPasswd)) {
			passwd.setError(getString(R.string.error_field_required));
			focusView = passwd;
			cancel = true;
		}
		if (cancel) {
			errores.setText(mErrores);
			errores.setVisibility(View.VISIBLE);
			focusView.requestFocus();
			return false;
		} 
		else {
			//hidding  keyboard
			try{
				((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(passwd.getWindowToken(), 0);
			}
			catch(Exception ex){
				Log.v("Error al esconder teclado: "+ex.getMessage() );
			}
			// Show a progress spinner
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(true);								
		}
		return true;
	}
	
	/**
	 * Shows the progress UI and hides the login form.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
	private void showProgress(final boolean show) {
		// On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
		// for very easy animations. If available, use these APIs to fade-in
		// the progress spinner.
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
			int shortAnimTime = getResources().getInteger(
					android.R.integer.config_shortAnimTime);

			mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});

			/*mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
			.alpha(show ? 0 : 1)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginFormView.setVisibility(show ? View.GONE
							: View.VISIBLE);
				}
			});*/
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			//mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
	}

	/**
	 * Makes the authentication process in Linphone server.
	 */
	private boolean isAuthenticated(){
		try{				
			String mdPass = SimpleCrypto.md5(mPasswd).substring(0, 10);
			LinphoneAuthInfo lAuthInfo =  LinphoneCoreFactory.instance()
					.createAuthInfo(mUser, mdPass, null, getResources()
							.getString(R.string.default_domain));												 
			String identity = "sip:" + mUser.toLowerCase() + "@" + 
					getResources().getString(R.string.default_domain);				
			String proxy = "sip:" + getResources().getString(R.string.default_domain);
				
			LinphoneAddress proxyAddr = LinphoneCoreFactory.instance()
					.createLinphoneAddress(proxy);						
			
			proxyAddr.setTransport(TransportType.LinphoneTransportTls);										
				
			LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();
			
			if(!lc.isNetworkReachable()){
				mErrores = getResources().getString(R.string.network_unavailable);				
				return false;
			}		
			
			removeAttempts(lc);
			
			LinphoneProxyConfig proxycon = lc.createProxyConfig(identity, 
					proxyAddr.asStringUriOnly(),proxyAddr.asStringUriOnly(),true);
						
			lc.addProxyConfig(proxycon);
			lc.setDefaultProxyConfig(proxycon);
	
			LinphoneProxyConfig lDefaultProxyConfig = lc.getDefaultProxyConfig();
			
			if (lDefaultProxyConfig != null) {			
				lDefaultProxyConfig.setDialEscapePlus(false);
			} 
								
			lc.addAuthInfo(lAuthInfo);
			int attempts = 0;
			
			while (proxycon.getState() == RegistrationState.RegistrationNone ||
					proxycon.getState() == RegistrationState.RegistrationProgress &&
					attempts < 10){	
				Log.v("Intento de conexion Nro: " + attempts++);
				Thread.sleep(1000);						
			}
								
			if(proxycon.getState() == RegistrationState.RegistrationFailed){
				mErrores = getResources().getString(R.string.login_failed);
				return false;
			}
			if(proxycon.getState() != RegistrationState.RegistrationOk){
				mErrores = getString(R.string.login_SIP_unavailable);					
				return false;
			}			
			return true;		
		} 
		catch(LinphoneCoreException e){
			Log.v(e.getMessage());			
		} 		
		catch (InterruptedException e) {	
			e.printStackTrace();
		}
		return false;		
	}
	
	/*
	 * Shows the error message at login process 
	 */
	private void showErrorMessage(){
		errores.setText(mErrores);
		errores.setVisibility(View.VISIBLE);
		passwd.setText("");
		showProgress(false);
	}
	
	/*
	 * Starts the MainActivity
	 
	private void launchMainActivity(){
		Intent i = new Intent(getApplicationContext(),MainActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK 
			|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		showProgress(false);			
		startActivity(i);		
		Log.v("=== Lanzando Main Activity");				
	}
	
	/*
	 * removes past user login attempts. 
	 */
	private void removeAttempts(LinphoneCore lc){
		LinphoneAuthInfo[] authInfosList = lc.getAuthInfosList();			
		if(authInfosList != null)
			lc.clearAuthInfos();
		
		int accounts = LinphonePreferences.instance().getAccountCount();
		while(accounts > 0){
			LinphonePreferences.instance().deleteAccount(accounts - 1);					
			accounts -= 1;
		}
	}
}
