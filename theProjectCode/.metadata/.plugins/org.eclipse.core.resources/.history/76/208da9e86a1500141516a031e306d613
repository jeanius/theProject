/**
 * Code from: 
 * http://www.androidhive.info/2013/07/how-to-implement-android-splash-screen-2/
 * 
 * 
 * Font from:
 * http://www.fontspace.com/qbotype/zian
 * 
 */

package com.jeanpower.reggieproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.widget.TextView;
 
public class SplashScreen extends Activity {
	
    private static int SPLASH_TIME_OUT = 3000;
 
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
       /* getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();*/
        
        setContentView(R.layout.splash);
        
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Zian free promo.ttf"); 
        TextView text = (TextView) findViewById(R.id.splashText);
        text.setTypeface(type);
 
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
            	
            	//Start new intent
                Intent i = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(i);
 
                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
 
}