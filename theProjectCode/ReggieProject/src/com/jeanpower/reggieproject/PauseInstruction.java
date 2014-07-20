package com.jeanpower.reggieproject;

import android.os.AsyncTask;
import android.util.Log;

public class PauseInstruction extends AsyncTask<Void, Void, Void>{
	
	Game game;
	
	public PauseInstruction(Game g){
		game = g;
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
    protected void onPostExecute(Void result) {
    	game.setFlag();
    }
}
