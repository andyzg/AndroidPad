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

public class TrackpadFragment extends Fragment implements ServerSocketListener {

	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	private PrintWriter mPrintWriter = null;
	private BluetoothSocket mSocket = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	
	private static final String TAG = "Trackpad Fragment";
	
	
	private static final String NAME = "Droidpad";
	private static final String MSG_HEADER = "DROIDPAD 1.0";
	
	private UUID mUUID = UUID.fromString("370a07a0-8557-11e3-9d72-0002a5d5c51b");
	
	private static final int REQUEST_ENABLE_BT = 2;
	
	public TrackpadFragment() {
		// Empty constructor
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		// Obtain bluetooth adapter
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		// If BT is not on, request that it be enabled.
	        // setupCommand() will then be called during onActivityResult
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
			
			ensureDiscoverable();
		}
		// otherwise set up the command service
		else {
			Log.d(TAG, "Starting Asynctask");
			new ServerTask(mUUID, NAME, this).execute(mBluetoothAdapter);
			// new ClientTask(mUUID, MAC, this).execute(mBluetoothAdapter);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Load the layout
		View v = inflater.inflate(R.layout.fragment_trackpad, container,
                false);
		
		return v;
	}
	
	/**
	 * Insert any code if required
	 */
	@Override
	public void onStart() {
		super.onStart();

	}

	/**
	 * After Async task is done, run this to instantiate
	 * the serversocket and input/output streams
	 */
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
	
	/**
	 * Ensure that the user is discoverable if 
	 * the user is connected and needs to be
	 * paired
	 */
    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
	
	/**
	 * Sends JSON to the client PC
	 * @param json
	 */
	public void sendAction(String json) {
		if (mSocket == null) {
			return;
		}
		Log.d(TAG, "Sending JSON");
		mPrintWriter.println(json.toString());
		mPrintWriter.flush();
	}
}