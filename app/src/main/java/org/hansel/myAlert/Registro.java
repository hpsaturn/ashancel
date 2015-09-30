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
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import org.hancel.http.HttpUtils;
import org.hansel.myAlert.Log.Log;
import org.hansel.myAlert.Utils.SimpleCrypto;
import org.hansel.myAlert.Utils.Util;
import org.hansel.myAlert.dataBase.UsuarioDAO;
import org.json.JSONObject;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Registro extends AppCompatActivity{

	private EditText vUsuario, vPassword, vPasswordConfirm, vEmail, vEmailConfirm;
	private String mUsuario, mPassword, mPasswordConfirm, mEmail, mEmailConfirm, mErrores;
	private RegistrationTask mAuthTask;
	private View mLoginFormView, mLoginStatusView;
	private TextView mLoginStatusMessageView, errores;
	private UsuarioDAO usuarioDAO;
	private Toolbar toolbar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Util.setLoginOkInPreferences(getApplicationContext(), false);
		setContentView(R.layout.registro_layout);
		vUsuario = (EditText) findViewById(R.id.reg_fullname);
		vPassword = (EditText) findViewById(R.id.reg_password2);
		vPasswordConfirm = (EditText) findViewById(R.id.reg_password);
		vEmail = (EditText) findViewById(R.id.reg_email);
		vEmailConfirm = (EditText) findViewById(R.id.reg_email_confirm);
		errores = (TextView) findViewById(R.id.err_registro);
		Button btnCreate = (Button) findViewById(R.id.btnRegister);
		btnCreate.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {		
				AttempCreate();
			}
		});
		
		Button btnCancelar = (Button) findViewById(R.id.btnCancelar);
		btnCancelar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent i = new Intent(Registro.this, Login.class);
				i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
						| Intent.FLAG_ACTIVITY_CLEAR_TOP);
				if (Util.isICS())
					i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
				finish();
			}
		});
		usuarioDAO = new UsuarioDAO(this);
		usuarioDAO.open();
		mLoginFormView = findViewById(R.id.reg_form);
		mLoginStatusView = findViewById(R.id.reg_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.reg_status_message);

	}

	protected void AttempCreate() {
		if (mAuthTask != null) {
			return;
		}
		
		boolean cancel = false;
		View focusView = null;

		try {
			mUsuario = vUsuario.getText().toString().trim().toLowerCase();
			mPassword = vPassword.getText().toString().trim();
			mPasswordConfirm = vPasswordConfirm.getText().toString().trim();
			mEmail = vEmail.getText().toString().trim();
			mEmailConfirm = vEmailConfirm.getText().toString().trim();
		} 
		catch (NullPointerException e) {
			vUsuario.setError(getString(R.string.invalid_empty));
			focusView = vUsuario;
			cancel = true;
		}

		// Check for a valid user.
		if (TextUtils.isEmpty(mUsuario)) {
			vUsuario.setError(getString(R.string.error_field_required));
			focusView = vUsuario;
			cancel = true;
		}
		if (TextUtils.isEmpty(mPassword)) {
			vPassword.setError(getString(R.string.error_field_required));
			focusView = vPassword;
			cancel = true;
		}
		if (TextUtils.isEmpty(mPasswordConfirm)) {
			vPasswordConfirm.setError(getString(R.string.error_field_required));
			focusView = vPassword;
			cancel = true;
		}

		if (mPasswordConfirm.compareTo(mPassword) != 0) {
			vPassword.setError(getString(R.string.error_password_confirm));
			focusView = vPassword;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(mEmail)) {
			vEmail.setError(getString(R.string.error_field_required));
			focusView = vEmail;
			cancel = true;
		}else if (!mEmail.contains("@")) {
			vEmail.setError(getString(R.string.invalid_email));
			focusView = vEmail;
			cancel = true;
		}
		
		if (TextUtils.isEmpty(mEmailConfirm)) {
			vEmailConfirm.setError(getString(R.string.error_field_required));
			focusView = vEmailConfirm;
			cancel = true;
		} else if (!mEmailConfirm.contains("@")) {
			vEmailConfirm.setError(getString(R.string.invalid_email));
			focusView = vEmailConfirm;
			cancel = true;
		}
		
		if (mEmailConfirm.compareTo(mEmail) != 0) {
			vEmailConfirm.setError(getString(R.string.error_email_confirm));
			focusView = vEmailConfirm;
			cancel = true;
		}

		if (cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			vPassword.setText("");
			vPasswordConfirm.setText("");
			vEmailConfirm.setText("");			
			focusView.requestFocus();
		} 
		else {
			// hidding keyboard
			try {
				((InputMethodManager) this
					.getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(vPassword.getWindowToken(), 0);
			} 
			catch (Exception ex) {
				Log.v("Error al esconder teclado: " + ex.getMessage());
			}			
			mAuthTask  = new RegistrationTask();
			try {
				if(mAuthTask.execute().get()){	
					AlertDialog.Builder alert = new AlertDialog.Builder(this);                 
					alert.setTitle(getText(R.string.registration_confirm));  
					alert.setMessage(getString(R.string.registration_confirm_msg));                
					alert.setPositiveButton(getString(R.string.registration_accept), 
							new DialogInterface.OnClickListener() {  
					public void onClick(DialogInterface dialog, int whichButton) {
						finish();
					}});
					alert.show();
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}			
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

			mLoginFormView.setVisibility(View.VISIBLE);
			mLoginFormView.animate().setDuration(shortAnimTime)
					.alpha(show ? 0 : 1)
					.setListener(new AnimatorListenerAdapter() {
						@Override
						public void onAnimationEnd(Animator animation) {
							mLoginFormView.setVisibility(show ? View.GONE
									: View.VISIBLE);
						}
					});
		} else {
			// The ViewPropertyAnimator APIs are not available, so simply show
			// and hide the relevant UI components.
			mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
			mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
		}
			
	}
	
	
	/**
	 * RegistrationTask
	 * Task for SIP server (Linphone) registration
	 * @author izel
	 */
	@SuppressLint("DefaultLocale")
	public class RegistrationTask extends AsyncTask<Void, Void, Boolean> {			
		@Override
		protected Boolean doInBackground(Void...v) {		
			JSONObject result = null;
			String id = null, crypto = null;
			
			//Sending request
			try {								
				id = SimpleCrypto.md5(String.valueOf(Calendar.getInstance()
						.getTimeInMillis()));
				crypto = SimpleCrypto.md5(mPassword);
				result = HttpUtils.Register(mUsuario,crypto,mEmail);
			} 
			catch (Exception ex) {
				mErrores = getString(R.string.registration_hancel_unavailable);
			 	errores.setVisibility(View.VISIBLE);
				Log.v("Error login: " + ex.getMessage());
				return false;
			}	
			
			//Handling response
			try {				
				if (result.optString("resultado").equals("ok")) {
					JSONObject jObject = result.getJSONObject("descripcion");					
					Util.setLoginOkInPreferences(getApplicationContext(),false);												
					int idUsr = (int) usuarioDAO.Insertar(mUsuario,crypto, mEmail);
					usuarioDAO.close();
					if (idUsr != 0) {						
						return true;
					}
				} 
				else{
					JSONObject jObject = result.getJSONObject("descripcion");
					String msg = jObject.getString("msg");
					if(msg.equalsIgnoreCase("duplicated")){
						mErrores = getString(R.string.registration_username_used);
					}
					else{
						mErrores = getString(R.string.registration_hancel_unavailable);
					}					
				}
				return false;
			} 
			catch (Exception e) {
				Log.v("Error al parsear JSON: " + result);
				mErrores = getString(R.string.registration_hancel_data_error);
				return false;
			}							
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProgress(true);
			findViewById(R.id.actions1).setVisibility(View.GONE);
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			mErrores = "";
			errores.setVisibility(View.GONE);
		}

		@Override
		protected void onPostExecute(final Boolean success) {
			mAuthTask = null;
			showProgress(false);
			if(!success){
				errores.setText(mErrores);
				errores.setVisibility(View.VISIBLE);
				findViewById(R.id.actions1).setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			findViewById(R.id.actions1).setVisibility(View.VISIBLE);
			showProgress(false);
		}		
	}	

}
