package org.hansel.myAlert.WelcomeInfo;

import android.graphics.Color;
import android.os.Bundle;

import com.github.paolorotolo.appintro.AppIntro;
import com.github.paolorotolo.appintro.AppIntro2;
import com.github.paolorotolo.appintro.AppIntroFragment;

import org.hansel.myAlert.MainActivity;
import org.hansel.myAlert.R;
import org.linphone.FragmentsAvailable;

/**
 * Created by hasus on 8/23/15.
 */
public class DefaultIntro extends AppIntro2 {
    @Override
    public void init(Bundle savedInstanceState) {

//        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name), getString(R.string.slide0), R.drawable.slide0, Color.parseColor("#2196F3")));
//        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name),getString(R.string.slide1), R.drawable.slide1,Color.parseColor("#222222")));
//        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name),getString(R.string.slide2), R.drawable.slide2,Color.parseColor("#00BCD4")));
//        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name),getString(R.string.slide3), R.drawable.slide3,Color.parseColor("#5C6BC0")));
//        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name),getString(R.string.slide4), R.drawable.slide4,Color.parseColor("#4CAF50")));
//        addSlide(AppIntroFragment.newInstance(getString(R.string.app_name),getString(R.string.slide5), R.drawable.slide5,Color.parseColor("#452356")));


        addSlide(SlideItem.newInstance(R.string.slide0, R.drawable.slide0));
        addSlide(SlideItem.newInstance(R.string.slide1, R.drawable.slide1));
        addSlide(SlideItem.newInstance(R.string.slide2, R.drawable.slide2));
        addSlide(SlideItem.newInstance(R.string.slide3, R.drawable.slide3));
        addSlide(SlideItem.newInstance(R.string.slide4, R.drawable.slide4));
        addSlide(SlideItem.newInstance(R.string.slide5, R.drawable.slide5));

        // OPTIONAL METHODS
        // Override bar/separator color
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

    }

//    @Override
//    public void onSkipPressed() {
//       loadMainActivity();
//    }

    private void loadMainActivity(){
        finish();
    }

    @Override
    public void onDonePressed() {
        loadMainActivity();
    }

}
