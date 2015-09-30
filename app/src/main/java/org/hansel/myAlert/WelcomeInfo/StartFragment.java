package org.hansel.myAlert.WelcomeInfo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;


import org.hansel.myAlert.MainActivity;
import org.hansel.myAlert.R;
import org.hansel.myAlert.Registro;
import org.hansel.myAlert.Utils.Util;
import org.linphone.FragmentsAvailable;

public class StartFragment extends Fragment {

    public static String TAG = StartFragment.class.getSimpleName();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = (ViewGroup) inflater.inflate(org.hansel.myAlert.R.layout.start, container, false);

        ViewPager mpager = (ViewPager) rootView.findViewById(R.id.pager);
        ScreenSlidePageAdapter s = new ScreenSlidePageAdapter(getActivity().getSupportFragmentManager());
        mpager.setAdapter(s);
//		CirclePageIndicator indicator = (CirclePageIndicator)rootView.findViewById(R.id.pagerIndicator);
//		indicator.setViewPager(mpager);
//
        Button login = (Button) rootView.findViewById(R.id.btnLogin);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.instance().changeCurrentFragment(FragmentsAvailable.LOGIN, null);
            }

            ;
        });
        Button register = (Button) rootView.findViewById(R.id.btnRegister);
        register.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity().getApplicationContext(), Registro.class);
                Util.setLoginOkInPreferences(getActivity().getApplicationContext(), false);
                startActivity(i);
            }
        });

        return rootView;

    }

}
