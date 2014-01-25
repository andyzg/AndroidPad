package com.fb.droidpad;

import android.bluetooth.BluetoothSocket;


public interface ServerSocketListener {
	public void onServerSocketComplete(BluetoothSocket socket);
}
