package com.jeanpower.reggieproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class Line extends View {
    Paint paint = new Paint();
    View view;    

    public Line(Context context, View v) {
        super(context);   
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE); 
        view = v;
    }

    public void onDraw(Canvas canvas) {
    		Log.d("I'm in the inDraw", "Yes");
    	
    	 	paint.setColor(Color.BLACK);     
            canvas.drawLine(view.getX(), view.getY()+ (view.getHeight()/2), view.getX() + (view.getWidth()), view.getY() + (view.getHeight()/2), paint);
    }

}