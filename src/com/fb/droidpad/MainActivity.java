package com.fb.droidpad;

import org.json.JSONException;

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
import android.view.Window;
import android.widget.FrameLayout;

public class MainActivity extends Activity implements OnTouchListener {


	private static final String VIEW_LOG_TAG = "test";
	private static final String TAG = "Main Activity";
	
	private Path path = new Path();
	private TrackpadFragment mTrackpadFragment;
	private MenuFragment mMenuFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main_frame);
	
		FrameLayout layout = (FrameLayout) findViewById(R.id.content_frame);
		layout.setOnTouchListener(this);
		
		mTrackpadFragment = new TrackpadFragment();
		mMenuFragment = new MenuFragment();
		switchToFragment(mTrackpadFragment, true);
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
	public boolean onTouch(View arg0, MotionEvent event) {
		float eventX = event.getX();
		float eventY = event.getY();
		
		Log.d(VIEW_LOG_TAG, eventX + "x- location " + eventY + "y-location");
		path.reset();
		path.addCircle(eventX,eventY,50,Path.Direction.CW);
		JSONAction action;
		// Send the info to be processed into JSON and 
		// sent to the PC
		try {
			// To modify the motion event
			action = new JSONAction(new float[]{eventX}, 
					new float[]{eventY}, 
					new int[]{getEventType(event)});
			Log.d(TAG, action.getJSON().toString());
			mTrackpadFragment.sendAction(action.getJSON());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//process finger movement... stream the data?
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
}
