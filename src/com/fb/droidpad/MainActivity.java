package com.fb.droidpad;

import org.json.JSONException;

import com.fb.droidpad.MenuFragment.OnMenuClickListener;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener, OnMenuClickListener {

	
	
	// Debugging purposes
	private static final String VIEW_LOG_TAG = "test";
	private static final String TAG = "Main Activity";
    private static final String DEBUG_TAG = "Gestures"; 
	
	// Gesture IDS
	private static final int NO_GESTURE = -1;
	private static final int SINGLE_TAP = 0;
	private static final int DOUBLE_TAP = 1;
	private static final int LONG_PRESS = 2;
	private static final int DOUBLE_TAP_MOVE = 3;
	private static final int PINCH = 4;
	
	private int DOUBLE_TOUCH_CURRENT_X;
	private int DOUBLE_TOUCH_CURRENT_Y;

	private int DOUBLE_TOUCH_X;
	private int DOUBLE_TOUCH_Y;
	
	private boolean DOUBLE_TAPPED = false;
	
	// Fragment storing
	private TrackpadFragment mTrackpadFragment;
	private MenuFragment mMenuFragment;
	private SettingsFragment mSettingsFragment;
	private AboutFragment mAboutFragment;
	
	private GestureDetectorCompat detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_frame);
		
		mTrackpadFragment = new TrackpadFragment();
		mMenuFragment = new MenuFragment();
		switchToFragment(mMenuFragment, true);

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

	public void sendToClient(MotionEvent event, int gestureId) {
		float eventX = event.getX();
		float eventY = event.getY();
		
		Log.d(VIEW_LOG_TAG, eventX + "x- location " + eventY + "y-location");
		
		JSONAction action;
		
		try {
			// To modify the motion event
			action = new JSONAction(new float[]{eventX}, 
					new float[]{eventY}, 
					new int[]{getEventType(event)});
			if (gestureId != -1) {
				action.setGesture(gestureId);
			}
			
			Log.d(TAG, action.getJSON().toString());
			mTrackpadFragment.sendAction(action.getJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// Runs method depending on type of gesture
		this.detector.onTouchEvent(event);
		
		
		return true;
	}
	
	private int getEventType(MotionEvent event) {
		switch(event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				return 0;
			case MotionEvent.ACTION_MOVE:
				return 1;
			case MotionEvent.ACTION_UP:
				return 2;
			default:
				return -1;	
		}
	}
	

	@Override
	public boolean onDoubleTap(MotionEvent event) {
        Log.d(DEBUG_TAG, "onDoubleTap: " + event.toString());
        sendToClient(event, DOUBLE_TAP);
		return true;
	}

	@Override
	public boolean onDown(MotionEvent event) {
	    Log.d(DEBUG_TAG,"onDown: "); 
	    sendToClient(event, NO_GESTURE);
		return true;
	}

	@Override
	public boolean onFling(MotionEvent event, MotionEvent flingEvent, float arg2,
			float arg3) {
		Log.d(DEBUG_TAG, "onFling: ");
		sendToClient(flingEvent, NO_GESTURE);
		return true;
	}

	/**
	 * Event for long press
	 */
	@Override
	public void onLongPress(MotionEvent event) {
	    Log.d(DEBUG_TAG, "onLongPress: "); 
		sendToClient(event, LONG_PRESS);
		
		switch (event.getActionMasked()) {
		case MotionEvent.ACTION_DOWN:
		case MotionEvent.ACTION_UP:
		}
		
	    return;
	}

	/**
	 *  First event is the DOWN event, second event are the movement events
	 */
	@Override
	public boolean onScroll(MotionEvent event, MotionEvent moveEvent, float arg2,
			float arg3) {
		Log.d(DEBUG_TAG, "onScroll: ");
		if (!DOUBLE_TAPPED) {
			sendToClient(moveEvent, NO_GESTURE);
		}
		else {
			sendToClient(moveEvent, DOUBLE_TAP_MOVE);
		}
		return true;
	}

	/**
	 * Single tap event
	 */
	@Override
	public boolean onSingleTapUp(MotionEvent event) {
        // Log.d(DEBUG_TAG, "onSingleTap: ");
        // sendToClient(event, SINGLE_TAP);
		return true;
	}
	
	@Override
	public boolean onSingleTapConfirmed(MotionEvent event) {
		Log.d(DEBUG_TAG, "Single tap: ");
		sendToClient(event, SINGLE_TAP);
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent event) {
	    Log.d(DEBUG_TAG, "onShowPress: ");
	    //sendToClient(event, LONG_PRESS);
	    return;
	}
	
	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		// Log.d(DEBUG_TAG, "onDoubleTap: ");
		// sendToClient(event, DOUBLE_TAP);
		switch (event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				DOUBLE_TAPPED = true;
			case MotionEvent.ACTION_UP:
				DOUBLE_TAPPED = false;
		}
		return true;
	}

	@Override
	public void switchToTrackpad() {
		switchToFragment(mTrackpadFragment, true);
	}

	@Override
	public void switchToSettings() {
		switchToFragment(mSettingsFragment, true;);
	}

	@Override
	public void switchToAbout() {
		switchToFragment(mAboutFragment, true);
	}
}
