package com.fb.droidpad;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

public class MenuFragment extends Fragment implements OnClickListener {
	
	private Button trackpadButton;
	private Button settingsButton;
	private Button aboutButton;

	private OnMenuClickListener mListener;
	
	public interface OnMenuClickListener {
		public void switchToTrackpad();
		public void switchToSettings();
		public void switchToAbout();
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_menu, container,
                false);
		trackpadButton = (Button) v.findViewById(R.id.trackpad_button);
		settingsButton = (Button) v.findViewById(R.id.settings_button);
		aboutButton = (Button) v.findViewById(R.id.about_button);
		
		trackpadButton.setOnClickListener(this);
		settingsButton.setOnClickListener(this);
		aboutButton.setOnClickListener(this);
		
		return v;
	}
	
	
	@Override
	public void onClick(View v) {
		switch(v.getId()) {
		case R.id.trackpad_button:
			mListener.switchToTrackpad();
			break;
		case R.id.settings_button:
			mListener.switchToSettings();
			break;
		case R.id.about_button:
			mListener.switchToAbout();
			break;
		default:
			Log.d("Menu", "Unable to find ID of button clicked");
			return;
		}
	}
}
