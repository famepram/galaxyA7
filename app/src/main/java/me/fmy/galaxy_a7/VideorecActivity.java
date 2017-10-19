package me.fmy.galaxy_a7;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import me.fmy.galaxy_a7.helpers.BTConn;
import me.fmy.galaxy_a7.helpers.DBConst;
import me.fmy.galaxy_a7.helpers.SFCameraPreview;

public class VideorecActivity extends AppCompatActivity {

    private final String TAG = "GALAXY_A7_VIDEOREC_ACTIVITY";

    private CountDownTimer mCT = null;
    private TextView mTextCT;

    private static final int STATE_STANDBY = 0;
    private static final int STATE_COUNTING = 1;
    private static final int STATE_FINISHED = 2;
    private int mState;
    private RelativeLayout wrapCDPrerecord;

    SharedPreferences pref, rpref;
    SharedPreferences.Editor prefEditor;
    int song_id;



    private Camera mCamera;
    private SFCameraPreview mPreview;
    private MediaRecorder mediaRecorder;
    private TextView txtCDRecording;
    private Context myContext;
    private LinearLayout cameraPreview;
    private boolean cameraFront = false;
    private  boolean recording = false;
    private CountDownTimer RecCountDownThread;

    String pathOutputFile;

    private BTConn mBTConn = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videorec);

        try {
            mBTConn = new BTConn();
        } catch (IOException e) {
            e.printStackTrace();
        }

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mState = STATE_STANDBY;

        rpref       = getSharedPreferences(DBConst.SPREF_NAME, MODE_PRIVATE);
        song_id     = rpref.getInt(DBConst.SPREF_KEY_SONG,0);
        pref        = getApplicationContext().getSharedPreferences(DBConst.SPREF_NAME, 0);
        prefEditor  = pref.edit();


        initPreCountDown();
        initializeCamCording();
        readyRecording();
        //debugSp();
    }

//    private void debugSp(){
//        SharedPreferences prefs = getSharedPreferences(DBConst.SPREF_NAME, MODE_PRIVATE);
//        Log.d(TAG,"SP LOG"+Integer.toString(prefs.getInt(DBConst.SPREF_KEY_ID,0)));
//        Log.d(TAG,"SP LOG"+prefs.getString(DBConst.SPREF_KEY_NAME,""));
//        Log.d(TAG,"SP LOG"+prefs.getString(DBConst.SPREF_KEY_EMAIL,""));
//        Log.d(TAG,"SP LOG"+prefs.getString(DBConst.SPREF_KEY_IG,""));
//        Log.d(TAG,"SP LOG"+Integer.toString(prefs.getInt(DBConst.SPREF_KEY_SONG,0)));
//        Log.d(TAG,"SP LOG"+prefs.getString(DBConst.SPREF_KEY_VIDEO,""));
//    }

    private void initPreCountDown(){
        mTextCT = (TextView) findViewById(R.id.txt_countdown);
        wrapCDPrerecord = (RelativeLayout) findViewById(R.id.wrap_count_down_prerecord);
        mCT = new CountDownTimer(10000, 1000) {
            long sec;
            @Override
            public void onTick(long millisUntilFinished) {
                sec = millisUntilFinished / 1000;
                mTextCT.setText(String.valueOf(sec));
            }

            @Override
            public void onFinish() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    wrapCDPrerecord.setVisibility(View.GONE);
                    }
                });
                mState = STATE_FINISHED;
                startRecording();
            }
        };


    }

    private void readyRecording(){
        if(mState == STATE_STANDBY){
            Thread timerThread = new Thread(){
                public void run(){
                    try{
                        sleep(2000);
                    }catch(InterruptedException e){
                        e.printStackTrace();
                    }finally{
                        mCT.start();
                        mState = STATE_COUNTING;
                    }
                }
            };
            timerThread.start();
        }
    }

    private void initializeCamCording(){
        myContext = getApplicationContext();
        cameraPreview = (LinearLayout) findViewById(R.id.camera_preview);
        mPreview = new SFCameraPreview(myContext, mCamera);
        cameraPreview.addView(mPreview);
        txtCDRecording = (TextView) findViewById(R.id.txt_countdown_record);
        RecCountDownThread = new CountDownTimer(DBConst.VID_DURATION, 1000) {
            long sec;

            @Override
            public void onTick(long millisUntilFinished) {
                sec = millisUntilFinished / 1000;
                txtCDRecording.setText(String.valueOf(sec));
                Log.d("COUNTDOWN",String.valueOf(sec));
            }

            @Override
            public void onFinish() {
                stopRecording();
            }
        };

    }

    @Override
    public void onResume(){
        super.onResume();
        if (!hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            // if the front facing camera does not exist
            mCamera = Camera.open(findBackFacingCamera());
            //mCamera.setDisplayOrientation(90);
            mPreview.refreshCamera(mCamera);
        }
    }

    private int findBackFacingCamera() {
        int cameraId = -1;
        // Search for the back facing camera
        // get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        // for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                cameraFront = false;
                break;
            }
        }
        return cameraId;
    }

    private void stopRecording(){
        if (recording) {
            // stop recording and release camera
            mBTConn.stopSong();
            mBTConn.closeSocket();
            mBTConn = null;
            txtCDRecording.setText("Done...");
            mediaRecorder.stop(); // stop the recording
            releaseMediaRecorder(); // release the MediaRecorder object
            Toast.makeText(myContext, "Video captured!", Toast.LENGTH_LONG).show();
            recording = false;
            finishRecording();
        }
    }

    private  void startRecording(){
        if(!recording){
            if (!prepareMediaRecorder()) {
                Toast.makeText(getApplicationContext(), "Fail in prepareMediaRecorder()!\n - Ended -", Toast.LENGTH_LONG).show();
                finish();
            }
            // work on UiThread for better performance
            runOnUiThread(new Runnable() {
                public void run() {
                    try {
                        recording = true;
                        mediaRecorder.start();
                        RecCountDownThread.start();
                        mBTConn.selectSong(song_id);

                    } catch (final Exception ex) {
                        //Log.e("RECORDED FAILED",ex.getMessage());
                    }
                }
            });
        }
    }

    private void releaseMediaRecorder() {
        if (mediaRecorder != null) {
            mediaRecorder.reset(); // clear recorder configuration
            mediaRecorder.release(); // release the recorder object
            mediaRecorder = null;
            mCamera.lock(); // lock camera for later use
        }
    }

    private boolean prepareMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mCamera.unlock();
        mediaRecorder.setCamera(mCamera);

        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);


        mediaRecorder.setProfile(CamcorderProfile.get(CamcorderProfile.QUALITY_480P));
        String filename = generatingVideoName();
        pathOutputFile = DBConst.VID_STORAGE_PATH + filename;
        Log.d("PATH", pathOutputFile);
        storingData(filename);
        mediaRecorder.setOutputFile(pathOutputFile);
        mediaRecorder.setMaxDuration(DBConst.VID_MAX_DURATION); // Set max duration 60 sec.
        mediaRecorder.setMaxFileSize(DBConst.VID_MAX_SIZE); // Set max file size 50M
        mediaRecorder.setOrientationHint(90);

        prefEditor.putString(DBConst.SPREF_KEY_VIDEO,filename);
        prefEditor.commit();
        try {
            mediaRecorder.prepare();
        } catch (IllegalStateException e) {
            releaseMediaRecorder();
            return false;
        } catch (IOException e) {
            releaseMediaRecorder();
            return false;
        }
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        // when on Pause, release camera in order to be used from other
        // applications
        releaseCamera();
    }

    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }

    private void storingData(String VideoName){
        prefEditor.putString(DBConst.SPREF_KEY_VIDEO,VideoName);
    }

    public String generatingVideoName(){
        String loc = "/sdcard/GalaxyExp";
        Log.d("LOKASI DIR",loc);
        File f = new File(loc);
        if(!f.isDirectory()){
            f.mkdir();
        }

        Long tsLong = System.currentTimeMillis()/1000;
        String ts = tsLong.toString();
        int userID = pref.getInt(DBConst.SPREF_KEY_ID,0);
        String VideoName = DBConst.VID_NAME_PREFIX + userID +"_"+ ts + DBConst.VID_EXT;
        return VideoName;
    }

    private boolean hasCamera(Context context) {
        // check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }

    private void finishRecording(){
        Intent intent = new Intent(VideorecActivity.this,FinishrecActivity.class);
        intent.putExtra(DBConst.VID_UPLOAD_SOURCE_KEY, pathOutputFile);
        startActivity(intent);
        finish();

    }

}
