package com.fb.droidpad;

import org.json.JSONException;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;

import com.fb.droidpad.MenuFragment.OnMenuClickListener;

public class MainActivity extends Activity implements GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener, OnMenuClickListener {

	
	
	// Debugging purposes
	private static final String VIEW_LOG_TAG = "test";
	private static final String TAG = "Main Activity";
    private static final String DEBUG_TAG = "Gestures"; 
	
    // Time constraint for a right click in ms
    private static final int RIGHT_CLICK_TIME = 200;
    
	// Gesture IDS
	private static final int NO_GESTURE = -1;
	private static final int SINGLE_TAP = 0;
	private static final int DOUBLE_TAP = 1;
	private static final int LONG_PRESS = 2;
	private static final int LONG_PRESS_DRAG = 3;
	private static final int SCROLL = 4;
	private static final int RIGHT_CLICK = 5;
	
	private boolean longPressing = false;
	private boolean twoFingers = false;
	
	// Fragment storing
	private TrackpadFragment mTrackpadFragment;
	private MenuFragment mMenuFragment;
	private SettingsFragment mSettingsFragment;
	private AboutFragment mAboutFragment;
	
	private GestureDetector detector;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_frame);
		
		mTrackpadFragment = new TrackpadFragment();
		mMenuFragment = new MenuFragment();
		mSettingsFragment = new SettingsFragment();
		mAboutFragment = new AboutFragment();

		//FrameLayout layout = (FrameLayout) findViewById(R.id.content_frame);
		//layout.setOnTouchListener(this);
		
		detector = new GestureDetector(this,this);
		detector.setIsLongpressEnabled(false);
		detector.setOnDoubleTapListener(this);
		switchToFragment(mMenuFragment, true);
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

	/**
	 * Send the JSON to the client
	 * @param event
	 * @param gestureId
	 */
	public void sendToClient(MotionEvent event, int gestureId) {

		JSONAction action;
		
		// Create the JSON object to be send
		try {
			final int pointerCount = event.getPointerCount();
			float eventX[] = new float[pointerCount];
			float eventY[] = new float[pointerCount];
			int motionEvent[] = new int[pointerCount];
			
			for (int i=0; i<pointerCount; i++) {
				eventX[i] = event.getX(i);
				eventY[i] = event.getY(i);
				motionEvent[i] = getEventType(event);
			}
			
			// To modify the motion event
			action = new JSONAction(eventX, 
					eventY, 
					motionEvent);

			// Add a gesture if set
			if (gestureId != NO_GESTURE) {
				action.setGesture(gestureId);
			}
			
			// Send the action
			// Log.d(TAG, action.getJSON().toString());
			mTrackpadFragment.sendAction(action.getJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		// Log.d(TAG, "Pointer count : " + event.getPointerCount());
		
		// Lifting your finger
		if (event.getActionMasked() == MotionEvent.ACTION_UP ){
			// No longer long pressing
			longPressing = false;
			sendToClient(event, NO_GESTURE);
			detector.onTouchEvent(event);
			
			// If there was one more than 1 finger on the screen and now lifted
			if (twoFingers) {
				// Check if time is less than RIGHT_CLICK_TIME 
				if (event.getEventTime() - event.getDownTime() < RIGHT_CLICK_TIME) {
					Log.d(TAG, "RIGHT CLICKING : " + (event.getEventTime() - event.getDownTime()));
					sendToClient(event, RIGHT_CLICK);
				}
				twoFingers = false;
			}
		} 
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE
				&& event.getPointerCount() == 2){
			sendToClient(event, SCROLL);
		} else{
			if (event.getPointerCount() == 2) {
				twoFingers = true;
			}
			detector.onTouchEvent(event);
		}
		
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
		if (longPressing) {
			sendToClient(moveEvent, LONG_PRESS_DRAG);
		}
		else {
			sendToClient(moveEvent, NO_GESTURE);
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
	    longPressing = true;
	    return;
	}
	
	@Override
	public boolean onDoubleTapEvent(MotionEvent event) {
		// Log.d(DEBUG_TAG, "onDoubleTap: ");
		/*switch (event.getActionMasked()) {
			case MotionEvent.ACTION_MOVE:
				Log.d(DEBUG_TAG, "Double tap drag");
				//sendToClient(event, DOUBLE_TAP_DRAG);
			case MotionEvent.ACTION_UP:
				// TODO Fix firing multiple times
				Log.d(DEBUG_TAG, "Double tap up");
				//sendToClient(event, DOUBLE_TAP);
		}*/
		return false;
	}

	@Override
	public void switchToTrackpad() {
		switchToFragment(mTrackpadFragment, true);
	}

	@Override
	public void switchToSettings() {
		switchToFragment(mSettingsFragment, true);
	}

	@Override
	public void switchToAbout() {
		switchToFragment(mAboutFragment, true);
	}
}
