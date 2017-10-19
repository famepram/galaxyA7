package me.fmy.galaxy_a7;

import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.UUID;

import me.fmy.galaxy_a7.helpers.BTService;
import me.fmy.galaxy_a7.helpers.BToothThread;
import me.fmy.galaxy_a7.helpers.DBConst;

public class SongsActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnA;

    Button btnB;

    Button btnC;

    Button btnD;

    Button btnE;

    ProgressDialog ProgressDialogConn;

    BluetoothDevice mmDeviceArd = null;

    BluetoothSocket mSocket;

    private BluetoothSocket _sockFallback = null;

    UUID mUUID;

    BToothThread mBThread;

    String DevServerName = "ASUS_Z00AD";
    String DevClientName = "GalaxyA7 ";

    private BluetoothAdapter BA;
    private Set<BluetoothDevice> pairedDevices;
    ListView lv;

    private static final String TAG = "BluetoothGalaxyA7";

    private StringBuffer mOutStringBuffer;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;

    private BluetoothAdapter mBluetoothAdapter = null;

    private BTService mBTService = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songs);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(SongsActivity.this, "Bluetooth is not available", Toast.LENGTH_LONG).show();
            SongsActivity.this.finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mBTService == null) {
            _getDevice();
            setupBTConn();
        }

        //_initBluetooth();
    }

    private void setupBTConn() {
        mBTService = new BTService(SongsActivity.this, mHandler);
        mOutStringBuffer = new StringBuffer("");
        connectDevice(false);

    }

    private final Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {

        }
    };


    private void connectDevice(boolean secure) {
        BluetoothDevice device = mmDeviceArd;
        // Attempt to connect to the device
        mBTService.connect(device, secure);
    }

    protected void _launchPDConn(){
        ProgressDialogConn = ProgressDialog.show(SongsActivity.this, "Please wait ...", "Checking Bluetooth Connection ...", true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    _initBluetooth();

                } catch (Exception e) {

                }
                ProgressDialogConn.dismiss();
            }
        }).start();
    }

    protected void _initBluetooth(){
        mmDeviceArd = null;
        BA = BluetoothAdapter.getDefaultAdapter();
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(),"Turned on",Toast.LENGTH_LONG).show();
        } else {

            _getDevice();
        }
    }

    protected void _getDevice(){
        Set<BluetoothDevice> pairedDevices  = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {

                if(device.getName().equals("ASUS_Z00AD")) {
                    Log.d(TAG,"--------------------------------------------------"+device.getName());
                    mmDeviceArd = device;
                    ParcelUuid[] uuids = device.getUuids();
                    mUUID = uuids[0].getUuid();
                    break;
                } else if(device.getName().equals("GalaxyA7")){
                    Log.d(TAG,"--------------------------------------------------"+device.getName());
                    mmDeviceArd = device;
                    ParcelUuid[] uuids = device.getUuids();
                    mUUID = uuids[0].getUuid();
                    break;
                }
            }
//            BA.cancelDiscovery();
//
//            if(mmDeviceArd != null) {
//
//
//                try {
//                    mSocket = mmDeviceArd.createRfcommSocketToServiceRecord(mUUID);
//
//                    Log.i("GALAXY A7","SOCKET CREATED");
//                } catch (IOException e) {
//                    Log.i("GALAXY A7","SOCKET NOT CREATED");
//                    e.printStackTrace();
//                }
//
//                try {
//                    mSocket.connect();
//                    Log.e("GALAXY A7","SOCKET DO CONNECTED");
//                } catch (IOException e) {
//                    Log.e("GALAXY A7","SOCKET NOT CONNECTED " +e.getMessage());
//                    Class<?> clazz = mSocket.getRemoteDevice().getClass();
//                    Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
//                    try {
//                        Method m = clazz.getMethod("createRfcommSocket", paramTypes);
//                        Object[] params = new Object[]{Integer.valueOf(1)};
//                        _sockFallback = (BluetoothSocket) m.invoke(mSocket.getRemoteDevice(), params);
//                        _sockFallback.connect();
//                        Log.i("GALAXY A7","SOCKET FALL BACK DO CONNECTED");
//                        mSocket = _sockFallback;
//                    }catch (Exception e2) {
//                        Log.e("GALAXY A7","SOCKET FALLBACK NOT CONNECTED - "+e2.getMessage());
//                    }
//
//                    e.printStackTrace();
//                }
//
//                _initButtonGroup();
//            }
        }
    }

    protected void _initButtonGroup(){
        btnA = (Button) findViewById(R.id.btn_song_a);
        btnA.setOnClickListener(this);
        btnB = (Button) findViewById(R.id.btn_song_b);
        btnB.setOnClickListener(this);
        btnC = (Button) findViewById(R.id.btn_song_c);
        btnC.setOnClickListener(this);
        btnD = (Button) findViewById(R.id.btn_song_d);
        btnD.setOnClickListener(this);
        btnE = (Button) findViewById(R.id.btn_song_e);
        btnE.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int chosen_song;
        switch(v.getId()){
            case R.id.btn_song_a:
                chosen_song = 1;
                break;
            case R.id.btn_song_b:
                chosen_song = 2;
                break;
            case R.id.btn_song_c:
                chosen_song = 3;
                break;
            case R.id.btn_song_d:
                chosen_song = 4;
                break;
            case R.id.btn_song_e:
                chosen_song = 5;
                break;
            default:
                chosen_song = 0;
            break;
        }
        Log.i("GALAXYA7",Integer.toString(chosen_song));

        ByteArrayOutputStream output = new ByteArrayOutputStream(4);
        output.write(chosen_song);
        OutputStream outputStream = null;
        try {
            outputStream = mSocket.getOutputStream();
            outputStream.write(output.toByteArray());
            Log.e("GALAXY A7","SOCKET WRITE SUCCEED");
        } catch (IOException e3) {
            Log.e("GALAXY A7","SOCKET WRITE FAILED - "+e3.getMessage());
            e3.printStackTrace();
        }
    }


}
