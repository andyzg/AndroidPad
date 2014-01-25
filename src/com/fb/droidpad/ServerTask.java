package com.fb.droidpad;

import java.io.IOException;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.AsyncTask;
import android.util.Log;

class ServerTask extends AsyncTask<BluetoothAdapter, Void, BluetoothSocket> {

	private String name;
	private UUID uuid;
	private ServerSocketListener listen;
	
	public ServerTask(UUID uuid, String name, ServerSocketListener listen) {
		this.name = name;
		this.uuid = uuid;
		this.listen = listen;
	}
	
	@Override
	protected BluetoothSocket doInBackground(BluetoothAdapter... bt) {
		BluetoothSocket mSocket = null;
		
		try {
			BluetoothServerSocket server = bt[0].listenUsingInsecureRfcommWithServiceRecord(name, uuid);
			mSocket = server.accept();
		} catch (IOException e) {
			e.printStackTrace();
			Log.d("AsyncTask", "Unable to get bluetooth socket");
		}
		
		return mSocket;
	}
	
     protected void onPostExecute(BluetoothSocket socket) {
         listen.onServerSocketComplete(socket);
     }
}