package com.fb.droidpad;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class JSONAction {
	private JSONObject json;
	
	public JSONAction(float eventX[], float eventY[], int motionEvent[]) throws JSONException {
		json = new JSONObject();
		
		int length = eventX.length;
		JSONArray array = new JSONArray();
		
		for (int i=0; i<length; i++) {
			JSONObject pointer = new JSONObject();
			pointer.put("x", eventX[i]);
			pointer.put("y", eventY[i]);
			pointer.put("motionEvent", motionEvent[i]);
			array.put(pointer);
		}
		json.put("pointers", array);
		this.json = json;
	}
	
	public String getJSON() {
		return this.json.toString();
	}
}
