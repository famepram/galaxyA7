package me.fmy.galaxy_a7;

import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class TestActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "GALAXY_A7_TEST_ACTIVITY";

    private BluetoothAdapter mBluetoothAdapter = null;

    private BluetoothDevice mBTDevice = null;

    private BluetoothSocket mmSocket = null;

    private BluetoothServerSocket mmServerSocket;

    private ConnectThread mConnectThread;

    private ConnectedThread mConnectedThread = null;

    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";

    // Unique UUID for this application
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");

    private  InputStream mmInStream;
    private OutputStream mmOutStream;

    private Button mButtonTestSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initBT();

        mConnectThread = new ConnectThread(mBTDevice);
        mConnectThread.start();
        mButtonTestSend = (Button) findViewById(R.id.btn_test_send);
        mButtonTestSend.setOnClickListener(this);

//        write(msgTest.getBytes());

    }

    private void initBT(){
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        getDevice();
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

    @Override
    public void onClick(View v) {

        ConnectedThread r;

        synchronized (this) {
            if(mConnectedThread == null) return;
            r = mConnectedThread;
        }

        String msgTest = "Test Sending";
        r.write(msgTest.getBytes());
        Log.d("TEST ACTIVITY", "CONNECTED THREAD RUN");
    }


    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        //private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
        public ConnectThread(BluetoothDevice device) {
            BluetoothSocket tmp = null;
            mmDevice = device;
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(MY_UUID_INSECURE);
                Log.d("TEST ACTIVITY", "SOCKET CONNECTED SUCCESS");
            } catch (IOException e) { }
            mmSocket = tmp;
        }
        public void run() {
            mBluetoothAdapter.cancelDiscovery();
            try {
                mmSocket.connect();
            } catch (IOException connectException) {
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
            if(mConnectedThread != null ){
                Log.d("TEST ACTIVITY", "CONNECTED THREAD RUN");
                String msgTest = "Test Sending";
                mConnectedThread.write(msgTest.getBytes());
            }

        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
            Log.i(TAG, "BEGIN mConnectedThread");

        }
        public void run() {
            Log.i(TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];
            int begin = 0;
            int bytes = 0;
            while (true) {
                try {
                    bytes += mmInStream.read(buffer, bytes, buffer.length - bytes);
                    for(int i = begin; i < bytes; i++) {
                        if(buffer[i] == "#".getBytes()[0]) {
                            mHandler.obtainMessage(1, begin, i, buffer).sendToTarget();
                            begin = i + 1;
                            if(i == bytes - 1) {
                                bytes = 0;
                                begin = 0;
                            }
                        }
                    }
                } catch (IOException e) {
                    break;
                }
            }

        }
        public void write(byte[] bytes) {
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG,e.getMessage());
            }
        }
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.d("RECMSG","MSG ----------------------------------- ACCEPT");
            byte[] writeBuf = (byte[]) msg.obj;
            int begin = (int)msg.arg1;
            int end = (int)msg.arg2;

            switch(msg.what) {
                case 1:
                    String writeMessage = new String(writeBuf);
                    writeMessage = writeMessage.substring(begin, end);
                    break;
            }
        }
    };
}
