package com.droidpad.android.touch.single;

import android.app.Activity;
import android.os.Bundle;

public class SingleTouchActivity extends Activity {

	@Override
	public void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
			setContentView(new singleTouchEventView(this,null));
		}
	}

