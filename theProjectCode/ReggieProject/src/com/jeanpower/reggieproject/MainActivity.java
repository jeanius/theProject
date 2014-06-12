//View - handles UI components and events

package com.jeanpower.reggieproject;

import java.util.List;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.app.ActionBar;

public class MainActivity extends Activity implements View.OnClickListener, View.OnLongClickListener{

	private Game game;
	final int MAXREGISTERS = 10;
	int[] buttonColours;

	/**
	 * Called when application is opened.                  
	 * <p>
	 * Sets the view to first screen, adds register buttons
	 * Adds listeners to activity buttons
	 * <p>
	 * @return void
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main); 
		game = new Game(this);

		//Array of colours, from color.xml
		buttonColours = getResources().getIntArray(R.array.rainbow);
		LinearLayout container = (LinearLayout) findViewById(R.id.register_frame);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT); 

		for (int i=0; i<MAXREGISTERS; i++){
			Button button = new Button(this);
			button.setText(game.getRegData(i) + ""); //Set initial text to 0
			button.setId(i);
			button.setLayoutParams(lp); //For wrap content
			button.setBackgroundColor(buttonColours[i]);
			button.setOnClickListener(this);
			button.setOnLongClickListener(this);
			container.addView(button); //Add to register frame
		}

		//Add listeners to activity buttons. //TODO Is there a better way to do this?
		Button arrowButton = (Button) findViewById(R.id.new_arrow_button);
		arrowButton.setOnClickListener(this);

		Button runButton = (Button) findViewById(R.id.run_button);
		runButton.setOnClickListener(this);

		Button boxButton = (Button) findViewById(R.id.new_box_button);
		boxButton.setOnClickListener(this);

		Button endButton = (Button) findViewById(R.id.new_end_button);
		endButton.setOnClickListener(this);

	}

	/**
	 * Inflates the menu and adds items                 
	 * <p>
	 * @param Menu. Menu which has items added
	 * @return boolean
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// TODO Add help, save, return

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/**
	 * To ignore orientation change, so UI is horizontal                   
	 * <p>
	 * @return void
	 */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onStart(){
		super.onStart();
	}

	public void updateDisplay(){

		RelativeLayout container = (RelativeLayout) findViewById(R.id.actionFrame);
		RelativeLayout.LayoutParams actionParameters = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		actionParameters.addRule(RelativeLayout.ALIGN_BASELINE, R.id.theLine);

		int currentPosition = 0;

		List<Instruction> list = game.getInstructionList();

		for(Instruction inst: list)
		{
			if (inst instanceof Box)
			{
				Box instruction = (Box) inst;
				Button button = new Button(this);
				button.setBackgroundColor(buttonColours[0]);

				if (instruction.getType())
				{
					actionParameters.addRule(RelativeLayout.ALIGN_BASELINE, R.id.theLine);
				}

				else
				{
					actionParameters.addRule(RelativeLayout.BELOW, R.id.theLine);
				}

				if (0==currentPosition)
				{
					actionParameters.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
					currentPosition = button.getId();
				}

				else
				{
					actionParameters.addRule(RelativeLayout.LEFT_OF, currentPosition);
				}
				
				container.addView(button);
			}
		}
	}

	/**
	 * Iterates through register buttons, getting their data from Game class.                   
	 * <p>
	 * @return void
	 */
	public void setRegisters(){

		for (int i = 0; i<MAXREGISTERS; i++){

			int resID = i;
			Button b = (Button) findViewById(resID);
			String data = game.getRegData(i) + "";
			b.setText(data);
		}
	}

	public int getMaxReg(){
		return MAXREGISTERS;
	}

	/**
	 * Takes click on register button and increments register                     
	 * <p>
	 * @param View. The button which has been clicked      
	 * @return void
	 */
	@Override
	public void onClick(View v) {

		int resid = v.getId();
		
		Log.d("Hey", "You clicked anyyyyy instruction");

		if (resid>=0 && resid<10)
		{
			Log.d("Hey", "You clicked a reg");
			game.incrementReg(resid);
		}

		else if (resid == R.id.new_arrow_button || resid == R.id.new_box_button || resid == R.id.new_end_button)
		{
			Log.d("Hey", "You clicked an instruction");
			game.newInstruction(resid);
			Log.d("Hey", "I've returned from the instructions");
			this.updateDisplay();
		}

		else if(resid == R.id.run_button)
		{
			game.runGame();
		}

		//else if it's a instruction, do xyz.
	}

	/**
	 * Takes long click on register button and zeros register                      
	 * <p>
	 * @param View. Button which has been clicked      
	 * @return boolean
	 */
	@Override
	public boolean onLongClick(View v) {

		int resid = v.getId();
		game.zeroReg(resid);
		return true;
	}
}