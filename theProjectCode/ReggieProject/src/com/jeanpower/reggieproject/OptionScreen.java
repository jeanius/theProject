package com.jeanpower.reggieproject;

/**
 * Option Screen for Reggie. 
 *<p>
 * Currently holds "Start Game" but allows for extension to hold "Load Game" in future
 * 
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class OptionScreen extends Activity implements View.OnClickListener{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.option);

		Typeface type = Typeface.createFromAsset(getAssets(),"fonts/Zian free promo.ttf"); 

		Button newButton = (Button) findViewById(R.id.new_button);
		newButton.setTypeface(type);

		newButton.setOnClickListener(this);

	}
/**
 * When "New Game" button is clicked, the option screen activity is finished, and the MainActivity is started 
 */
	@Override
	public void onClick(View v) {

		int resid = v.getId();

		if (resid == R.id.new_button){

			Intent i = new Intent(OptionScreen.this, MainActivity.class);
			startActivity(i);

			// close this activity
			finish();

		}	
	}	
}