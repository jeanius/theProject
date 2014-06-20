package com.jeanpower.reggieproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;

public class DrawArrow extends View {
    Paint paint = new Paint();
    View start;
    View end;    

    public DrawArrow(Context context, View startView, View endView) {
        super(context);   
        paint.setStrokeWidth(5);
        paint.setStyle(Paint.Style.STROKE); 
        start = startView;
        end = endView;
    }

    public void onDraw(Canvas canvas) {
    	
    	 	paint.setColor(Color.BLACK);     
            canvas.drawLine(start.getX() + (start.getWidth()/2), start.getY()+ (start.getHeight()/2), end.getX() + (end.getWidth()/2), end.getY() + (end.getHeight()/2), paint);
    }
}