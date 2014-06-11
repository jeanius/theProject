//View - handles UI components and events

package com.jeanpower.reggieproject;

import android.os.Bundle;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.text.Layout;
import android.util.Log;
import android.view.DragEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.app.ActionBar;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener{

	private Game game;
	final int MAXREGISTERS = 10;
	private static final int[] BUTTON_IDS = {
		R.id.register0,
		R.id.register1, 
		R.id.register2,
		R.id.register3,
		R.id.register4,
		R.id.register5, 
		R.id.register6,
		R.id.register7,
		R.id.register8,
		R.id.register9
		};


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		game = new Game(this);
		
		for (int i=0; i<BUTTON_IDS.length; i++){
			Button button = (Button) findViewById(BUTTON_IDS[i]);
			button.setOnClickListener(this);
			button.setOnLongClickListener(this);
		}
	}



	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// ignore orientation/keyboard change
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onStart(){
		super.onStart();
	}

	public void setRegisters(){

		for (int i = 0; i<MAXREGISTERS; i++){

			String regName = ("register" + i);
			int resID = getResources().getIdentifier(regName, "id", getPackageName()); //Get register button unique resource ID
			Button b = (Button) findViewById(resID);
			String data = game.getRegData(i) + "";
			b.setText(data);
		}
	}

	public int getMaxReg(){
		return MAXREGISTERS;
	}


	@Override
	public void onClick(View v) {

		int resid = v.getId();
		Log.d("onClick", "listener caught");
		
		for (int i = 0; i<BUTTON_IDS.length; i++)
		{
			if (resid==BUTTON_IDS[i]){
				Log.d("whichreg", i +"");
				game.incrementReg(i);
			}
		}
	}



	@Override
	public boolean onLongClick(View v) {
		
		int resid = v.getId();
		Log.d("ondrag", "long listener caught");
		
		for (int i = 0; i<BUTTON_IDS.length; i++)
		{
			if (resid==BUTTON_IDS[i]){
				Log.d("onClick", "found the longclick id");
				game.zeroReg(i);
			}
		}

		return true;
	}
}
