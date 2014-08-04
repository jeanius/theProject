package com.jeanpower.reggieproject;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.github.amlcurran.showcaseview.ShowcaseView;

public class Tutorial implements View.OnClickListener{
	
	MainActivity main;
	Dialog dialog;

	public Tutorial(MainActivity ma) {
		
		main = ma;

		dialog = new Dialog(main);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.tutorial);
		
		// set the custom dialog components - text, image and button
		TextView text = (TextView) dialog.findViewById(R.id.welcomeText);
		text.setText("Hi there! I'm Reggie, the register machine! I'm here to help you understand how a computer works - would you like to complete the tutorial?");

		Button yesButton = (Button) dialog.findViewById(R.id.dialogYes);
		Button noButton = (Button) dialog.findViewById(R.id.dialogNo);
		yesButton.setOnClickListener(this);
		noButton.setOnClickListener(this);

		dialog.show();


		// setContentView(R.layout.tutorial);

		/*ViewTarget target = new ViewTarget(R.id.bin_clear_button, this);

		new ShowcaseView.Builder(this, true)
		.setTarget(target)
		.setContentTitle("ShowcaseView")
		.setContentText("This is highlighting the Home button")
		.hideOnTouchOutside()
		.build();*/

	}

	@Override
	public void onClick(View button) {

		int resid = button.getId();

		switch (resid){

		case R.id.dialogNo:
			dialog.dismiss();

		case R.id.dialogYes:
			dialog.dismiss();
		}
	}
}
