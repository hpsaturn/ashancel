package org.linphone;
/*
AboutFragment.java
Copyright (C) 2012  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
*/


import org.hansel.myAlert.MainActivity;
import org.hansel.myAlert.R;
import org.linphone.mediastream.Log;

import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ScrollView;
import android.widget.TextView;

/**
 * @author Sylvain Berfini
 */
public class AboutFragment extends Fragment implements OnClickListener {
	private FragmentsAvailable about = FragmentsAvailable.ABOUT;//_INSTEAD_OF_CHAT;
	private ScrollView _sv_about;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (getArguments() != null && getArguments().getSerializable("About") != null) {
			about = (FragmentsAvailable) getArguments().getSerializable("About");
		}
		
		View view = inflater.inflate(R.layout.about, container, false);
		
		TextView aboutText = (TextView) view.findViewById(R.id.AboutText);
		try {
			aboutText.setText(String.format(getString(R.string.about_licence), getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName));
		} 
		catch (NameNotFoundException e) {
			Log.e(e, "cannot get version name");
		}
		
		_sv_about = (ScrollView)view.findViewById(R.id.sv_about);
		Animation translatebu= AnimationUtils.loadAnimation(getActivity(), R.anim.about);
		_sv_about.startAnimation(translatebu);


		/*View issue = view.findViewById(R.id.exit);
		issue.setOnClickListener(this);
		issue.setVisibility(View.VISIBLE);*/
		
		return view;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (MainActivity.isInstanciated()) {
			MainActivity.instance().selectMenu(about);
			
			if (getResources().getBoolean(R.bool.show_statusbar_only_on_dialer)) {
				MainActivity.instance().hideStatusBar();
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		/*if (MainActivity.isInstanciated()) {

			if (getResources().getBoolean(R.bool.enable_log_collect)) {
				LinphoneUtils.collectLogs(MainActivity.instance(), getString(R.string.about_bugreport_email));
			} else {
				MainActivity.instance().exit();
			}
		}*/
	}
}
