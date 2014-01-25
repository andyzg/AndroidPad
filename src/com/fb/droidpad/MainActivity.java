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
    
    // MotionEvent Actions
    private static final int ACTION_DOWN = 0;
    private static final int ACTION_MOVE = 1;
    private static final int ACTION_UP = 2;
    
	// Gesture IDS
	private static final int NO_GESTURE = -1;
	private static final int SINGLE_TAP = 0;
	private static final int DOUBLE_TAP = 1;
	private static final int LONG_PRESS = 2;
	private static final int LONG_PRESS_DRAG = 3;
	private static final int SCROLL = 4;
	private static final int RIGHT_CLICK = 5;
	private static final int LEFT_TAB_SWITCH = 6;
	private static final int RIGHT_TAB_SWITCH = 7;
	private static final int CUSTOM_GESTURE = 8;
	
	private float tabSwitchX = 0;
	private float tabSwitchCurrentX = 0;
	
	private boolean longPressing = false;
	private boolean twoFingers = false;
	private boolean threeFingers = false;
	// Easter egg event, opens up facebook
	private boolean fourFingers = false;
	
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
		mMenuFragment = new MenuFragment(this);
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
			
			// If lifting one of 4 fingers, run custom gesture
			if (fourFingers) {
				Log.d(TAG, "Custom gesture");
				sendToClient(event, CUSTOM_GESTURE);
				fourFingers = false;
				threeFingers = false;
				twoFingers = false;
			}
			// If lifting from 3 fingers, swap tabs
			else if (threeFingers) {
				
				tabSwitchCurrentX = event.getX();
				Log.d(TAG, "Final X : " + tabSwitchCurrentX);
				
/*				for (int i=0; i<event.getPointerCount(); i++) {
					tabSwitchCurrentX += event.getX(i)/event.getPointerCount();
				}*/

				float difference = tabSwitchCurrentX - tabSwitchX;
				
				Log.d(TAG, "Difference from " + tabSwitchCurrentX + " and " + tabSwitchX + " is " + difference);
				// Moved at least 100 pixels
				if (difference > 50 || difference < 50) {
					// If to the right
					if (difference > 0) {
						Log.d(TAG, "To the right swap");
						sendToClient(event, RIGHT_TAB_SWITCH);
					}
					// If to the left
					else {
						Log.d(TAG, "To the left swap");
						sendToClient(event, LEFT_TAB_SWITCH);
					}
					twoFingers = false;
				}
				
				// Null everything else
				threeFingers = false;
				tabSwitchX = 0;
				tabSwitchCurrentX=0;
			}
			// If there was one more than 1 finger on the screen and now lifted
			else if (twoFingers) {
				// Check if time is less than RIGHT_CLICK_TIME 
				if (event.getEventTime() - event.getDownTime() < RIGHT_CLICK_TIME) {
					Log.d(TAG, "RIGHT CLICKING : " + (event.getEventTime() - event.getDownTime()));
					sendToClient(event, RIGHT_CLICK);
				}
				twoFingers = false;
			}
			
		} 
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE &&
				event.getPointerCount() == 3) {
			// Log.d(TAG, "Tab swipe");
		}
		else if (event.getActionMasked() == MotionEvent.ACTION_MOVE && 
				event.getPointerCount() == 2){
			sendToClient(event, SCROLL);
		}
		else {
			// Switch caused an unexpected error, using if instead
			if (event.getPointerCount() == 2) {
				twoFingers = true;
			}
			else if (event.getPointerCount() == 3 && 
					event.getActionMasked() != MotionEvent.ACTION_POINTER_UP) {
				tabSwitchX = event.getX();
				Log.d(TAG, "Initial X" + tabSwitchX);
/*				for (int i=0; i<event.getPointerCount(); i++) {
					tabSwitchX += event.getX(i)/event.getPointerCount();
				}*/
				threeFingers = true;
				twoFingers = false;
			}
			else if (event.getPointerCount() == 4) {
				fourFingers = true;
				threeFingers = false;
				twoFingers = false;
			}
			detector.onTouchEvent(event);
		}
		
		return true;
	}
	
	private int getEventType(MotionEvent event) {
		switch(event.getActionMasked()) {
			case MotionEvent.ACTION_DOWN:
				return ACTION_DOWN;
			case MotionEvent.ACTION_MOVE:
				return ACTION_MOVE;
			case MotionEvent.ACTION_UP:
				return ACTION_UP;
			default:
				return NO_GESTURE;	
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
