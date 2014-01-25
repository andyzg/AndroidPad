package com.fb.droidpad;

import org.json.JSONException;

public interface ActionListener {
	public void mouseAction(float eventX, float eventY, int motionEvent) throws JSONException;
	public void multiTapAction(float eventX[], float eventY[], int motionEvent[]) throws JSONException;
}
