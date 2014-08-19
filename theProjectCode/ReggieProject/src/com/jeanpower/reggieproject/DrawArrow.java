package com.jeanpower.reggieproject;


/**
 * Arrow drawing helper class
 * <p>
 * Class creates and returns bitmap Arrow to use on ImageButton on screen.
 * Arrow differs depending on if loop/branch, the predecessor and "to" instruction, 
 * and in length. Length is determined by number of increment/decrement instructions it spans.
 * 
 * <p>
 * @author Jean Power 2014
 * 
 */

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.RectF;

public class DrawArrow {
	Paint paint = new Paint();
	Path pathBefore = new Path(); //Before head (>)
	Path pathAfter = new Path(); //After head (>)
	int beforeColour;
	int afterColour;
	int numberButtons;
	Arrow arrow;
	boolean loop;
	float arrowWidth;
	float arrowHeight;
	
	/**
	 * Constructor.
	 * 
	 * @param Arrow - to be drawn, int - width of ImageButton, height - height of ImageButton
	 */
	public DrawArrow(Arrow a, int width, int height){

		paint.setStrokeWidth(6);
		paint.setStyle(Paint.Style.STROKE); 
		arrow = a;
		numberButtons = arrow.getSpaces(); //Get span required
		loop = arrow.getType();
		arrowWidth = width;
		arrowHeight = height;
	}
	
	
	/**
	 * Method sets colours of before, and after the head of arrow (>)
	 * <p>
	 * @param int - Start Colour, int - End Colour
	 * @return void
	 */
	public void setColours(int startC, int endC){
		beforeColour = startC;
		afterColour = endC;
	}

	
	/**
	 * Method draws path of Arrow, returns Bitmap
	 * <p>
	 * Colours are register colours, associated with predecessor and "to" instructions.
	 * <p>
	 * @param void
	 * @return Bitmap - arrow path
	 */
	
	public Bitmap getImage(){

		Bitmap bitmap = Bitmap.createBitmap(((int) arrowWidth * numberButtons), (int) arrowHeight, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		if (loop){
			
			if (numberButtons <= 1){ //If arrow span of 1 box, need V
				
				pathBefore.moveTo(arrowWidth/4, 0);
				pathBefore.lineTo(arrowWidth/2, arrowHeight);
				pathBefore.lineTo(arrowWidth - arrowWidth/4, 0);
			}

			else { //Need |---<---|
				pathBefore.moveTo(arrowWidth - arrowWidth/3, arrowHeight);
				pathBefore.lineTo(arrowWidth - arrowWidth/3, arrowHeight/2);
				pathBefore.lineTo((arrowWidth * numberButtons)/2, arrowHeight/2);
				
				pathAfter.moveTo((arrowWidth * numberButtons)/2 + arrowWidth/4, arrowHeight);
				pathAfter.lineTo((arrowWidth * numberButtons)/2, arrowHeight/2);
				pathAfter.lineTo((arrowWidth * numberButtons)/2 + arrowWidth/4, 0);
				pathAfter.lineTo((arrowWidth * numberButtons)/2, arrowHeight/2);
				pathAfter.lineTo((arrowWidth * numberButtons) - arrowWidth/3, arrowHeight/2);
				pathAfter.lineTo((arrowWidth * numberButtons) - arrowWidth/3, arrowHeight);		
			}
			
			paint.setColor(afterColour);
			canvas.drawPath(pathBefore, paint);
			
			if (numberButtons > 1){
				paint.setColor(beforeColour);
				canvas.drawPath(pathAfter, paint);
			}
		}

		else { //If branch
			if (numberButtons <= 1){  //If arrow span of 1 box, need /\
				pathBefore.moveTo(arrowWidth/4, arrowHeight);
				pathBefore.lineTo(arrowWidth/2, 0);
				pathBefore.lineTo(arrowWidth - arrowWidth/4, arrowHeight);
			}
			
			else { //Need O--->---|
				RectF rec = new RectF(arrowWidth/3, 0, arrowWidth - arrowWidth/3, arrowHeight);
				
				pathBefore.moveTo(arrowWidth - arrowWidth/3, arrowHeight/2);
				pathBefore.addOval(rec, Path.Direction.CW);
				pathBefore.lineTo((arrowWidth * numberButtons)/2 + arrowWidth/4, arrowHeight/2);
				
				pathAfter.moveTo((arrowWidth * numberButtons)/2, arrowHeight);
				pathAfter.lineTo((arrowWidth * numberButtons)/2 + arrowWidth/4, arrowHeight/2);
				pathAfter.lineTo((arrowWidth * numberButtons)/2, 0);
				pathAfter.lineTo((arrowWidth * numberButtons)/2 + arrowWidth/4, arrowHeight/2);
				
				pathAfter.lineTo((arrowWidth * numberButtons) - arrowWidth/3, arrowHeight/2);
				pathAfter.lineTo((arrowWidth * numberButtons) - arrowWidth/3, 0);
			}
			
			paint.setColor(beforeColour);
			canvas.drawPath(pathBefore, paint);
			
			if (numberButtons > 1){
				paint.setColor(afterColour);
				canvas.drawPath(pathAfter, paint);
			}
		}
		return bitmap;
	}
}