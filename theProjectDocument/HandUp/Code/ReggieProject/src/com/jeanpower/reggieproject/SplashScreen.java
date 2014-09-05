package com.jeanpower.reggieproject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

/**
 * Splash screen for Reggie<p>
 * 
 * Launch activity of Splash Screen which delays, and then starts the option screen<br>
 * Font from:
 * http://www.fontspace.com/qbotype/zian
 * 
 **/
public class SplashScreen extends Activity {

	private final int TIME = 3000;

	/**
	 * Called when activity is created<p>
	 * Sets typeface, delays showing option screen for 3 seconds.
	 * <p>
	 * @param savedInstanceState - Bundle contains previous state information
	 * @return void
	 */
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