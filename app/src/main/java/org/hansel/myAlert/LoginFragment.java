package org.hansel.myAlert;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.hansel.myAlert.Log.Log;
import org.hansel.myAlert.Utils.SimpleCrypto;
import org.hansel.myAlert.Utils.Util;
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
import static android.content.Intent.ACTION_MAIN;

public class LoginFragment extends Fragment implements OnClickListener {
    private String mUser, mPasswd, mErrores;
    private EditText user, passwd;
    private TextView errores;
    private ProgressBar progressBar;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View loginView = inflater.inflate(R.layout.login_layout_md, container, false);

        getActivity().startService(new Intent(getActivity(), LinphoneService.class));

        user = (EditText) loginView.findViewById(R.id.txtUser);
        passwd = (EditText) loginView.findViewById(R.id.txtPassword);

        Button btnLogin = (Button) loginView.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);

        Button btnRegistration = (Button) loginView.findViewById(R.id.btnRegistration);
        btnRegistration.setOnClickListener(this);

        errores = (TextView) loginView.findViewById(R.id.tvError);
        progressBar = (ProgressBar) loginView.findViewById(R.id.progress);

        return loginView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        progressBar.setVisibility(View.VISIBLE);
        switch (id) {
            case R.id.btnLogin:

                if (AttempLogin() && isAuthenticated()) {
                    String crypto = SimpleCrypto.md5(mPasswd);
                    UsuarioDAO usuarioDAO = new UsuarioDAO(LinphoneService
                            .instance().getApplicationContext());

                    usuarioDAO.open();
                    String user = usuarioDAO.getUser();
                    usuarioDAO.Insertar(mUser, crypto, "");
                    usuarioDAO.close();

                    Util.setLoginOkInPreferences(getActivity().getApplicationContext(), true);
                    /////
                    progressBar.setVisibility(View.INVISIBLE);

                    /*if (user.length() == 0) {
                        RegisteredFragment registered = new RegisteredFragment();
                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.activityContainer, registered);
                        fragmentTransaction.commit();
                    }*/

                    ((MainActivity)getActivity()).showMainFragment();
                } else {
                    errores.setText(mErrores);
                    errores.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.INVISIBLE);
                    Util.setLoginOkInPreferences(getActivity().getApplicationContext(), false);
                    passwd.setText("");
                }
                showProgress(false);
                break;
            case R.id.btnRegistration:
                Intent i = new Intent(getActivity().getApplicationContext(), Registro.class);
                Util.setLoginOkInPreferences(getActivity().getApplicationContext(), false);
                startActivity(i);
                break;
        }

    }

    /**
     * Checking if required fields are filled
     */
    private boolean AttempLogin() {
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
            passwd.setError(getString(R.string.error_passw_required));
            focusView = passwd;
            cancel = true;
        }
        if (cancel) {
            errores.setText(mErrores);
            errores.setVisibility(View.VISIBLE);
            focusView.requestFocus();
            return false;
        } else {
            //hidding  keyboard
            try {
                ((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(passwd.getWindowToken(), 0);
            } catch (Exception ex) {
                Log.v("Error al esconder teclado: " + ex.getMessage());
            }
            // Show a progress spinner
            //mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
            showProgress(true);
        }
        return true;
    }

    /**
     * Makes the authentication process in Linphone server.
     */
    private boolean isAuthenticated() {
        try {
            showProgress(true);
            String mdPass = SimpleCrypto.md5(mPasswd).substring(0, 10);
            LinphoneAuthInfo lAuthInfo = LinphoneCoreFactory.instance()
                    .createAuthInfo(mUser, mdPass, null, getResources()
                            .getString(R.string.default_domain));
            String identity = "sip:" + mUser.toLowerCase() + "@" +
                    getResources().getString(R.string.default_domain);
            String proxy = "sip:" + getResources().getString(R.string.default_domain);


            LinphoneAddress proxyAddr = LinphoneCoreFactory.instance()
                    .createLinphoneAddress(proxy);

            proxyAddr.setTransport(TransportType.LinphoneTransportTls);

            LinphoneCore lc = LinphoneManager.getLcIfManagerNotDestroyedOrNull();

            if (!lc.isNetworkReachable()) {
                mErrores = getResources().getString(R.string.network_unavailable);
                return false;
            }

            removeAttempts(lc);

            LinphoneProxyConfig proxycon = lc.createProxyConfig(identity,
                    proxyAddr.asStringUriOnly(), proxyAddr.asStringUriOnly(), true);

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
                            attempts < 10) {
                Log.v("Intento de conexion Nro: " + attempts++);
                Thread.sleep(1000);
            }

            if (proxycon.getState() == RegistrationState.RegistrationFailed) {
                mErrores = getResources().getString(R.string.login_failed);
                return false;
            }
            if (proxycon.getState() != RegistrationState.RegistrationOk) {
                mErrores = getString(R.string.login_SIP_unavailable);
                return false;
            }
            return true;
        } catch (LinphoneCoreException e) {
            Log.v(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(progressBar!=null)progressBar.setVisibility(View.GONE);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (show)
            progressBar.setVisibility(View.VISIBLE);
        else
            progressBar.setVisibility(View.GONE);
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(
                    android.R.integer.config_shortAnimTime);


			/*mLoginStatusView.setVisibility(View.VISIBLE);
			mLoginStatusView.animate().setDuration(shortAnimTime)
			.alpha(show ? 1 : 0)
			.setListener(new AnimatorListenerAdapter() {
				@Override
				public void onAnimationEnd(Animator animation) {
					mLoginStatusView.setVisibility(show ? View.VISIBLE
							: View.GONE);
				}
			});*/

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
        } //else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            //mLoginStatusView.setVisibility(show ? View.VISIBLE : View.GONE);
            //mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
       // }
    }

    /*
     * removes past user login attempts.
     */
    private void removeAttempts(LinphoneCore lc) {
        LinphoneAuthInfo[] authInfosList = lc.getAuthInfosList();
        if (authInfosList != null)
            lc.clearAuthInfos();

        int accounts = LinphonePreferences.instance().getAccountCount();
        while (accounts > 0) {
            LinphonePreferences.instance().deleteAccount(accounts - 1);
            accounts -= 1;
        }
    }
}
