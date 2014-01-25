package com.fb.droidpad;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.UUID;

import org.json.JSONException;
import org.json.JSONObject;

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

public class TrackpadFragment extends Fragment implements ServerSocketListener, ActionListener {

	private InputStream mInputStream = null;
	private OutputStream mOutputStream = null;
	private PrintWriter mPrintWriter = null;
	private BluetoothSocket mSocket = null;
	private BluetoothAdapter mBluetoothAdapter = null;
	
	private Button mButton1;
	
	private static final String TAG = "Trackpad Fragment";
	
	
	private static final String NAME = "Droidpad";
	private static final String MSG_HEADER = "DROIDPAD 1.0";
	
	private UUID mUUID = UUID.fromString("370a07a0-8557-11e3-9d72-0002a5d5c51b");
	
	private static final int REQUEST_ENABLE_BT = 2;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
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
			mPrintWriter.print("Hello world");
			mPrintWriter.flush();
		}
		
		/*String action = createAction(x, y);
		mPrintWriter.append(MSG_HEADER);
		mPrintWriter.println(action);
		mPrintWriter.flush();*/
	}
	
    private void ensureDiscoverable() {
        Log.d(TAG, "ensure discoverable");
        
        if (mBluetoothAdapter.getScanMode() !=
            BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }
<<<<<<< HEAD

    /**
     * Single mouse scrolling action
     */
	@Override
	public void mouseAction(float eventX, float eventY, int motionEvent) throws JSONException {
		Log.d(TAG, "Sending mouse action at " + eventX + " and " + eventY);
		JSONObject json = new JSONObject();
		addPointers(json, new float[]{eventX}, new float[]{eventY}, new int[]{motionEvent});
	}

	/**
	 * Multiple inputs received to be turned into a JSON
	 */
	@Override
	public void multiTapAction(float eventX[], float eventY[], int motionEvent[]) throws JSONException {
		Log.d(TAG, "Sending double tap action at " + eventX[0] + " and " + eventY[0]);
		JSONObject json = new JSONObject();
		addPointers(json, eventX, eventY, motionEvent);
		sendAction(json);
	}
	
	/**
	 * Adds the pointers to the JSON to be sent
	 * @param json
	 * @param eventX
	 * @param eventY
	 * @param motionEvent
	 * @throws JSONException
	 */
	public void addPointers(JSONObject json, float eventX[], float eventY[], int motionEvent[]) throws JSONException {
		for (int i=0; i<eventX.length; i++) {
			JSONObject pointer = new JSONObject();
			pointer.put("x", eventX[i]);
			pointer.put("y", eventY[i]);
			pointer.put("motionEvent", motionEvent[i]);
			json.accumulate("pointers", pointer);
		}
	}
	
	/**
	 * Sends JSON to the client PC
	 * @param json
	 */
	public void sendAction(JSONObject json) {
		Log.d(TAG, "Sending JSON");
		mPrintWriter.println(json.toString());
		mPrintWriter.flush();
	}
=======
    
>>>>>>> master
}