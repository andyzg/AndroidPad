package com.fb.droidpad;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.GestureDetector;
import android.widget.FrameLayout;
import android.support.v4.view.GestureDetectorCompat;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener {


	private static final String VIEW_LOG_TAG = "test";
	private Path path = new Path();
	private GestureDetectorCompat detector;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_frame);
		//FrameLayout layout = (FrameLayout) findViewById(R.id.content_frame);
		//layout.setOnTouchListener(this);
		detector = new GestureDetectorCompat(this,this);
		detector.setOnDoubleTapListener(this);
		switchToFragment(new TrackpadFragment(), true);
	}

    /**
     * Switch to fragment.
     * @param newFrag The fragment to switch to.
     * @param addToBackStack Whether the transaction should be added to back stack.
     */
    private void switchToFragment(Fragment newFrag, boolean addToBackStack) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.replace(R.id.content_frame, newFrag);
            if (addToBackStack)
                    transaction.addToBackStack(null);
            transaction.commit();
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onTouchEvent(MotionEvent arg0) {
		this.detector.onTouchEvent(arg0);
		float eventX = arg0.getX();
		float eventY = arg0.getY();
		
		switch (arg0.getAction()) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(eventX,  eventY);
			return true;
		case MotionEvent.ACTION_MOVE:
			Log.d(VIEW_LOG_TAG, eventX + "x- location " + eventY + "y-location");
			path.reset();
			path.addCircle(eventX,eventY,50,Path.Direction.CW);
			//process finger movement... stream the data?
			
			break;
		case MotionEvent.ACTION_UP:
			break;
		default:
			return false;
		}
		return super.onTouchEvent(arg0);
	}
	
	

	@Override
	public boolean onDoubleTap(MotionEvent arg0) {
		// TODO Auto-generated method stub
        Log.d(VIEW_LOG_TAG, "onDoubleTap: " + arg0.toString());
		return false;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent arg0) {
		return false;
	}

	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
        Log.d(VIEW_LOG_TAG, "onSingleTap: " + arg0.toString());
		// TODO Auto-generated method stub
		return false;
	}
}
