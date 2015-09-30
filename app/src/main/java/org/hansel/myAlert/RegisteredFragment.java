package org.hansel.myAlert;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by izel
 */
public class RegisteredFragment extends Fragment implements View.OnClickListener {
    private Button rings;
    private TextView later;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View registeredView = inflater.inflate(R.layout.registered, container, false);
        rings = (Button) registeredView.findViewById(R.id.newRing);
        later = (TextView) registeredView.findViewById(R.id.later);

        rings.setOnClickListener(this);
        later.setOnClickListener(this);

        return registeredView;
    }


    @Override
    public void onClick(View view) {
        Bundle data = new Bundle();

            /*if (view.getId() == R.id.newRing)
                data.putBoolean("createRing", true);

            MainActivity.instance().showMainFragment(data);*/
    }
}