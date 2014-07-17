package com.jeanpower.reggieproject;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageButton;

public class RunInstruction extends AsyncTask<Void, Void, Void>{
	
	Game caller;
	MainActivity window;
	Instruction instruction;
	ImageButton button;
	ImageButton toChange;
	
	public RunInstruction(Instruction i, Game g, MainActivity ma){
		caller = g;
		window = ma;
		instruction = i;
	}

	@Override
	protected void onPreExecute (){
		
		instruction.doWork();
		
		ImageButton button = (ImageButton) window.findViewById(instruction.getId());
		
		if (instruction instanceof Box){
			Box box = (Box) instruction;
			toChange = (ImageButton) window.findViewById (window.getRegisterID(box.getRegister()));
			
			Log.d("Know it's a box", "iT WAS A BOX");
		}
		
		else if (instruction instanceof Arrow){
			
			Arrow arrow = (Arrow) instruction;
			
			toChange = (ImageButton) window.findViewById(arrow.getTo().getId());
			
			Log.d("Know it's a arrow", "iT WAS AN ARROW");
			
		}
		
		if (null != toChange){
			toChange.getBackground().setAlpha(100);
		}
		button.getBackground().setAlpha(100);
	}
	
	@Override
	protected Void doInBackground(Void... arg0) {
		
		try {
		Thread.sleep(1000);

		}
		catch (Exception e){
			Log.d("This was interrupted", "interrupted");
		}
		
			return null;
	}
	
	@Override
	protected void onPostExecute (Void result){
		
		button.getBackground().setAlpha(255);	
		
		if (null != toChange){
			toChange.getBackground().setAlpha(255);
		}
	}

}
