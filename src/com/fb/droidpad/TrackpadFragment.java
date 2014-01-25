package com.fb.droidpad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class TrackpadFragment extends Fragment implements ServerSocketListener {

	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	private PrintWriter mPrintWriter = null;
	private BluetoothSocket mSocket = null;
	
	private Button mButton1;
	
	private static final String TAG = "Trackpad Fragment";
	private String MAC = "";
	private BluetoothAdapter mBluetoothAdapter = null;
	private static final String NAME = "Droidpad";
	
	private UUID mUUID = UUID.fromString("370a07a0-8557-11e3-9d72-0002a5d5c51b");
	
	private static final int REQUEST_ENABLE_BT = 2;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.fragment_trackpad, container,
                false);
		
		mButton1 = (Button) v.findViewById(R.id.send);
		mButton1.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				sendMessage();
			}
		});
		return v;
	}
	
	@Override
	public void onStart() {
		super.onStart();
		
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If BT is not on, request that it be enabled.
	        // setupCommand() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
		}
		// otherwise set up the command service
		else {
			Log.d(TAG, "Starting Asynctask");
			new ServerTask(mUUID, NAME, this).execute(mBluetoothAdapter);
			// new ClientTask(mUUID, MAC, this).execute(mBluetoothAdapter);
		}
	}

	@Override
	public void onServerSocketComplete(BluetoothSocket socket) {
		Log.d(TAG, "Completed AsyncTask");
		mSocket = socket;
		try {
			mInputStream = socket.getInputStream();
			mOutputStream = socket.getOutputStream();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		mPrintWriter = new PrintWriter(mOutputStream);
	}
	
	public void sendMessage() {
		if (mPrintWriter != null) {
			Log.d(TAG, "Sending hello world!");
			mPrintWriter.println("Hello world");
			mPrintWriter.flush();
		}
	}
}