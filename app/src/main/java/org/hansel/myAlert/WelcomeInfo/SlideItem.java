package org.hansel.myAlert.WelcomeInfo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.hansel.myAlert.R;

/**
 * Created by hasus on 8/23/15.
 */
public class SlideItem extends Fragment {

    public static String TAG = SlideItem.class.getSimpleName();

    private static final String ARG_LAYOUT_RES_ID = "layoutResId";
    private static final String ARG_LAYOUT_TEXT_ID = "layoutTextId";
    private static final String ARG_LAYOUT_BG_ID = "layoutBackgroundId";
    private int layoutResId;
    private int layoutTextId;
    private int layoutBgId;

    public static SlideItem newInstance(int layoutResId) {
        SlideItem sampleSlide = new SlideItem();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_RES_ID, layoutResId);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }

    public static SlideItem newInstance(int text, int background){

        SlideItem sampleSlide = new SlideItem();

        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_TEXT_ID, text);
        args.putInt(ARG_LAYOUT_BG_ID, background);
        sampleSlide.setArguments(args);

        return sampleSlide;
    }


    public SlideItem() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_RES_ID))
            layoutResId = getArguments().getInt(ARG_LAYOUT_RES_ID);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_TEXT_ID))
            layoutTextId = getArguments().getInt(ARG_LAYOUT_TEXT_ID);
        if (getArguments() != null && getArguments().containsKey(ARG_LAYOUT_BG_ID))
            layoutBgId = getArguments().getInt(ARG_LAYOUT_BG_ID);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView "+layoutTextId+" "+layoutBgId+" "+layoutResId);

        if(layoutTextId!=0&&layoutBgId!=0) {
            View rootView = inflater.inflate(R.layout.welcome, container, false);
            TextView text = (TextView) rootView.findViewById(R.id.tv_welcome_desc);
            RelativeLayout layout = (RelativeLayout) rootView.findViewById(R.id.rl_welcome_bigimage);
            text.setText(layoutTextId);
            layout.setBackgroundResource(layoutBgId);
            return rootView;

        } else if(layoutResId!=0)
            return inflater.inflate(layoutResId, container, false);

        return null;
    }

}

