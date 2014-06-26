
package com.jeanpower.reggieproject;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;
import android.view.View;
import android.graphics.Shader;

public class DrawArrow {
	Paint paint = new Paint();
	Path path = new Path();
	View start;
	View end;  
	int startColour;
	int endColour;
	LinearGradient lg;
	int numberButtons;
	
	public DrawArrow(View startView, View endView, int length) {

		paint.setStrokeWidth(5);
		paint.setStyle(Paint.Style.STROKE); 
		start = startView;
		end = endView; 	
		numberButtons = length;
	}

	public void setColours(int startC, int endC){
		startColour = startC;
		endColour = endC;
		lg = new LinearGradient(start.getX() + start.getWidth(), start.getY(), end.getX(), end.getY(), startColour, endColour, Shader.TileMode.MIRROR);
	}

	public Bitmap getImage(){
		
		Log.d("Helllooo", "Hi");
		
		float startX = start.getX();
		float startWidth = start.getWidth();
		float startHeight = start.getHeight();
		float startY = start.getY();
		float endX = end.getX();
		float endY = end.getY();
				
		Bitmap bitmap = Bitmap.createBitmap(((int) startWidth * numberButtons), (int) startHeight/2, Bitmap.Config.ALPHA_8);
		Canvas canvas = new Canvas(bitmap);
		
		path.moveTo((startX + (startWidth/2)), startY + (startHeight));
		path.lineTo((startX + (startWidth/2)), (startY + startHeight/2));
		path.lineTo(endX + startWidth, (endY + startHeight/2));
		
		path.lineTo((endX + startWidth), (endY + startHeight));
		path.lineTo(endX + (startWidth/2), (endY + startHeight/2));
		path.lineTo((endX + startWidth), endY);
		path.lineTo((endX + startWidth), (endY + startHeight/2));
		
		paint.setShader(lg);
		canvas.drawPath(path, paint);

		return bitmap;
		
	}
}