package me.fmy.galaxy_a7;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.nfc.Tag;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

import me.fmy.galaxy_a7.helpers.BTCommService;

public class SongActivity extends AppCompatActivity {

    private static final String TAG = "GALAXY_A7_SONG_ACTIVITY";

    private BluetoothAdapter mBluetoothAdapter = null;

    private BTCommService mBTCommService = null;

    private static final int REQUEST_ENABLE_BT = 0;

    private static final String DEVICE_SERVER_NAME = "ASUS_Z00AD";

    private static final String DEVICE_CLIENT_NAME = "GalaxyA7 ";

    private BluetoothDevice mBTDevice = null;

    private StringBuffer mOutStringBuffer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_song);

        // Init Adapter & BT Comm Service;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Check If HH Support Bluetooth Or Not
        if(mBluetoothAdapter == null){
            Toast.makeText(getApplicationContext(), "Bluetooth is not available", Toast.LENGTH_LONG).show();
            SongActivity.this.finish();
        }
    }

    @Override
    public void onStart(){
        super.onStart();

        //Check if Bluetooth is ON, if not request enabled
        // if yes statrt setup connection
        if(!mBluetoothAdapter.isEnabled()){
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, REQUEST_ENABLE_BT);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_LONG).show();
        } else if(mBTCommService == null){
            setupComm();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBTCommService != null) {
            mBTCommService.stop();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mBTCommService != null) {

            if(mBTCommService.getState() == BTCommService.STATE_NONE){
                mBTCommService.start();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                if (resultCode == Activity.RESULT_OK) {
                    //
                } else {
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(getApplicationContext(), "Bluetooth not enabled",Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void getDevice(){
        if(mBluetoothAdapter != null){
            Set<BluetoothDevice> pairedDevices  = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals("ASUS_Z00AD")) {
                        Log.d(TAG, "--------------------------------------------------" + device.getName());
                        mBTDevice = device;
                        break;
                    } else if (device.getName().equals("GalaxyA7")) {
                        Log.d(TAG, "--------------------------------------------------" + device.getName());
                        mBTDevice = device;
                        break;
                    }
                }
            } else {
                Log.d(TAG, "Paired Devices Not Found.");
            }
        } else {
            Log.d(TAG, "BT not enabled");
        }
    }

    private void setupComm(){
        if(mBTDevice == null){
            getDevice();
        }
        mBTCommService = new BTCommService(mBluetoothAdapter, getApplicationContext());
        if(mBTCommService.getState() == BTCommService.STATE_NONE){
            connectDevice(false);
        }
        mOutStringBuffer = new StringBuffer("");
    }

    private void connectDevice( boolean secure) {

        // Attempt to connect to the device
        mBTCommService.connect(mBTDevice, secure);
        if(mBTCommService.getState() == BTCommService.STATE_CONNECTED){
            sendMessage("TEst");
            Log.d(TAG,"SENT TEST MSG");
        }
    }

    private void sendMessage(String message) {
        if (mBTCommService.getState() != BTCommService.STATE_CONNECTED) {
            Toast.makeText(getApplicationContext(), "NotConncted", Toast.LENGTH_SHORT).show();
            return;
        }
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mBTCommService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);
        }
    }
}
