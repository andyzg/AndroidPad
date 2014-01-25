package com.droidpad.android.touch.single;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class singleTouchEventView extends View {
	private Paint paint = new Paint();
	private Path path = new Path();
	
	public singleTouchEventView(Context context, AttributeSet attrs) {
		super (context, attrs);
		
		paint.setAntiAlias(true);
		paint.setStrokeWidth(6f);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeJoin(Paint.Join.ROUND);
	}
	
	protected void onDraw(Canvas canvas) {
		canvas.drawPath(path, paint);
	}
	
	public boolean onTouchEvent(MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(eventX,  eventY);
			return true;
		case MotionEvent.ACTION_MOVE:
			path.lineTo(eventX,  eventY);
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			return false;
		}
		invalidate();
		return true;
	}
}

