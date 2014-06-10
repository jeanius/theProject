package com.jeanpower.reggieproject;

import android.os.Bundle;
import android.app.Activity;
import android.app.Fragment;
import android.content.res.Configuration;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

public class MainActivity extends Activity {
	
	private Game game;
	final int MAXREGISTERS = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
       
       setContentView(R.layout.activity_main); 
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
    
    public void onStart(){
    	
    	game = new Game(this);
   }
    
    public void setRegisters(){
        
        for (int i = 0; i<MAXREGISTERS; i++){
        	
        	String regName = ("register" + i);
        
        	int resID = getResources().getIdentifier(regName, "id", getPackageName()); //Get register button unique resource ID
        
        	Button but = ((Button) findViewById(resID));
        	
        	Log.d("here", game.getRegData(0) + "");
        	String data = game.getRegData(i) + "";
        	but.setText(data);
        }
    }
    
    public int getMaxReg(){
    	return MAXREGISTERS;
    }
}
