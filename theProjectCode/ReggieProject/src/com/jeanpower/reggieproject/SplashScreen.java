package com.jeanpower.reggieproject;

/**
 * Splash screen for Reggie
 * 
 * Launch activity of Splash Screen which delays, and then starts the option screen
 * 
 * Font from:
 * http://www.fontspace.com/qbotype/zian
 * 
 **/

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class SplashScreen extends Activity {

	private final int TIME = 3000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.splash);

		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Zian free promo.ttf"); 
		TextView text = (TextView) findViewById(R.id.splashText);
		text.setTypeface(type);

		//Handler bound to main thread - schedules OptionScreen to start in 3000ms
		new Handler().postDelayed(new Runnable() {

			@Override
			public void run() {

				//Start option screen
				Intent i = new Intent(SplashScreen.this, OptionScreen.class);
				startActivity(i);

				// close splash screen
				finish();
			}
		}, TIME);
	}

}