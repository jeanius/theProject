package com.jeanpower.reggieproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

public class DrawArrow extends View {
	Paint paint = new Paint();
	View start;
	View end;  
	RelativeLayout rl;

	public DrawArrow(Context context, View startView, View endView, RelativeLayout container) {

		super(context);   
		paint.setStrokeWidth(5);
		paint.setStyle(Paint.Style.STROKE); 
		start = startView;
		end = endView; 
		rl = container;
	}

	public void onDraw(Canvas canvas) {

		paint.setColor(Color.BLACK);     
		canvas.drawLine(start.getX() + (start.getWidth()/2), start.getY()+ (start.getHeight()/2), end.getX() + (end.getWidth()/2), end.getY() + (end.getHeight()/2), paint);   
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(rl.getWidth(), rl.getHeight());  
	}
}