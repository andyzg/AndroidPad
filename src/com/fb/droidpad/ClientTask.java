package com.fb.droidpad;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

class ClientTask extends AsyncTask<BluetoothAdapter, Void, BluetoothSocket> {

	private String TAG = "Asynctask";
	private String MAC;
	private UUID uuid;
	private ServerSocketListener listen;
	
	public ClientTask(UUID uuid, String MAC, ServerSocketListener listen) {
		this.MAC = MAC;
		this.uuid = uuid;
		this.listen = listen;
	}
	
	@Override
	protected BluetoothSocket doInBackground(BluetoothAdapter... bt) {
		BluetoothSocket mSocket = null;
		Log.d(TAG, "Starting doinbackground");
		try {
			// BluetoothDevice device = bt[0].getBondedDevices();
			//mSocket = device.createRfcommSocketToServiceRecord(uuid);
			Log.d(TAG, "Received serversocket from UUID " + uuid + ", now accepting");
			mSocket.connect();
			
			Log.d(TAG, "Received socket, closing server");
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("AsyncTask", "Unable to get bluetooth socket");
		}
		
		return mSocket;
	}
	
     protected void onPostExecute(BluetoothSocket socket) {
    	 Log.d(TAG, "Done execution");
         listen.onServerSocketComplete(socket);
     }
}