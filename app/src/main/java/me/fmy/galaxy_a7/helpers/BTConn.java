package me.fmy.galaxy_a7.helpers;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Femmy on 8/2/2016.
 */
public class BTConn {

    private static final String TAG = "GALAXY_A7_BTCONN_CLASS";

    private BluetoothAdapter mBTAdapter;

    private BluetoothDevice mBTDevice;

    private BluetoothSocket mBTSocket;

    private static final UUID mUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private InputStream mmInStream = null;

    private OutputStream mmOutStream = null;

    private String mConnectedDeviceAddress = null;

    public BTConn() throws IOException {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter();
        getDeviceAddress();
        mBTDevice = mBTAdapter.getRemoteDevice(mConnectedDeviceAddress);
        mBTSocket = mBTDevice.createInsecureRfcommSocketToServiceRecord(mUUID);
        mBTAdapter.cancelDiscovery();
        mBTSocket.connect();
        mmInStream = mBTSocket.getInputStream();
        mmOutStream = mBTSocket.getOutputStream();
    }

    private synchronized void getDeviceAddress(){
        Set<BluetoothDevice> pairedDevices = mBTAdapter.getBondedDevices();
        for(BluetoothDevice bt : pairedDevices){
            //BluetoothDevice device = bt;
            if(bt.getName().equals(DBConst.BT_DEVICE_NAME) ){
                Log.d(TAG,"CONNECTION DEVICE IS "+bt.getName());
                mConnectedDeviceAddress = bt.getAddress();
                break;
            }
        }
    }

    public boolean checkConn(){
        String msg = DBConst.BT_MSG_CHECK;
        byte[] send = msg.getBytes();
        try {
            mmOutStream.write(send);
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
            return false;
        }
    }

    public void selectSong(int song_id){
        String msg;
        switch (song_id){
            case 1:
                msg = DBConst.BT_MSG_SONG_1;
                break;
            case 2:
                msg = DBConst.BT_MSG_SONG_2;
                break;
            case 3:
                msg = DBConst.BT_MSG_SONG_3;
                break;
            case 4:
                msg = DBConst.BT_MSG_SONG_4;
                break;
            case 5:
                msg = DBConst.BT_MSG_SONG_5;
                break;
            default:
                msg = DBConst.BT_MSG_SONG_1;
        }
        byte[] send = msg.getBytes();
        try {
            mmOutStream.write(send);
            Log.d(TAG, "Sending commmand select song success");
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }

    public void closeSocket(){
        try {
            mBTSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Close Socket failed", e);
        }
    }

    public void stopSong(){
        String msg = DBConst.BT_MSG_SONG_STOP;
        byte[] send = msg.getBytes();
        try {
            mmOutStream.write(send);
            Log.d(TAG, "Sending commmand stop song success");
        } catch (IOException e) {
            Log.e(TAG, "Exception during write", e);
        }
    }
}
