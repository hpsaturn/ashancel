package org.hansel.myAlert.WelcomeInfo;
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
import org.hansel.myAlert.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WelcomeFragment extends Fragment {

	private int number;
	
	public void setNumber(int num){
		number = num;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		ViewGroup rootView = (ViewGroup) inflater.inflate(
                org.hansel.myAlert.R.layout.welcome, container, false);			
		
		TextView texto = (TextView) rootView.findViewById(R.id.tv_welcome_desc);
		RelativeLayout layout = (RelativeLayout)rootView.findViewById(R.id.rl_welcome_bigimage);
		switch (number) {
		case 0:			
			texto.setText(getResources().getString(R.string.slide0));
			layout.setBackgroundResource(R.drawable.slide0);
			break;
		case 1:
			texto.setText(getResources().getString(R.string.slide1));
			layout.setBackgroundResource(R.drawable.slide1);
			break;
		case 2:
			texto.setText(getResources().getString(R.string.slide2));
			layout.setBackgroundResource(R.drawable.slide2);
			break;
		case 3:
			texto.setText(getResources().getString(R.string.slide3));
			layout.setBackgroundResource(R.drawable.slide3);
			break;
		case 4:
			texto.setText(getResources().getString(R.string.slide4));
			layout.setBackgroundResource(R.drawable.slide4);
			break;
		case 5:
			texto.setText(getResources().getString(R.string.slide5));
			layout.setBackgroundResource(R.drawable.slide5);
			break;
		default:
			break;
		}
		
        return rootView;
		
	}
}
