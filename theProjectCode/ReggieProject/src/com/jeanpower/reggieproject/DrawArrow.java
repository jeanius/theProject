
package com.jeanpower.reggieproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.Log;
import android.view.View;
import android.graphics.Shader;

public class DrawArrow {
	Paint paint = new Paint();
	Path path = new Path();
	int startColour;
	int endColour;
	LinearGradient lg;
	int numberButtons;
	Arrow arrow;
	boolean loop;
	View view;
	int arrowWidth;
	int arrowHeight;

	public DrawArrow(Arrow a, int width, int height){

		paint.setStrokeWidth(5);
		paint.setStyle(Paint.Style.STROKE); 
		arrow = a;
		numberButtons = arrow.getSpaces();
		loop = arrow.getType();
		arrowWidth = width;
		arrowHeight = height/2; //Smaller than the instruction buttons.
	}

	public void setColours(int startC, int endC){
		startColour = startC;
		endColour = endC;

		if (loop){

			lg = new LinearGradient( arrowWidth * numberButtons, 0,  0, 0, startColour, endColour, Shader.TileMode.CLAMP);

		}
		else{

			lg = new LinearGradient( arrowWidth * numberButtons, 0, 0, 0, startColour, endColour, Shader.TileMode.CLAMP);
		}
	}


	public Bitmap getImage(){
		//Log.d("This is button number", numberButtons + "");

		Bitmap bitmap = Bitmap.createBitmap(((int) arrowWidth * numberButtons) + numberButtons, (int) arrowHeight, Bitmap.Config.ALPHA_8);
		Canvas canvas = new Canvas(bitmap);

		if (loop){

			path.moveTo(arrowWidth-1, arrowHeight/2);
			path.lineTo(arrowWidth-1, arrowHeight);
			path.lineTo(arrowWidth/2, arrowHeight/2);
			path.lineTo(arrowWidth-1, 0);
			path.lineTo(arrowWidth-1, arrowHeight/2);

			if (numberButtons > 1){

				path.lineTo((arrowWidth*numberButtons)-1, arrowHeight/2);
				path.lineTo((arrowWidth*numberButtons)-1, arrowHeight);
			}

		}

		else {

			if (numberButtons <= 1){
				path.moveTo(0, 0);
				path.lineTo(0, arrowHeight);
				path.lineTo(arrowWidth/2, arrowHeight/2);
				path.lineTo(0, 0);
			}
			
			else {
				path.moveTo(arrowWidth/3, 0);
				path.lineTo(arrowWidth/3, arrowHeight/2);
				path.lineTo((arrowWidth * numberButtons) - arrowWidth, arrowHeight/2);
				path.lineTo((arrowWidth * numberButtons) - arrowWidth, arrowHeight);
				path.lineTo((arrowWidth * numberButtons) - arrowWidth/2, arrowHeight/2);
				path.lineTo((arrowWidth * numberButtons) - arrowWidth, 0);
				path.lineTo((arrowWidth * numberButtons) - arrowWidth, arrowHeight/2);
			}
		}

		PathEffect effect = new CornerPathEffect(3.0f);
		paint.setPathEffect(effect);
		paint.setShader(lg);

		canvas.drawPath(path, paint);

		return bitmap;
	}
}