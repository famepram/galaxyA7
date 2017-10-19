package me.fmy.galaxy_a7;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import me.fmy.galaxy_a7.helpers.DBConst;
import me.fmy.galaxy_a7.models.User;

public class FinishrecActivity extends AppCompatActivity {

    private final String TAG = "FMYLOGLO_GALAXY_A7_FINISHREC_ACTIVITY";

    SharedPreferences pref;
    SharedPreferences mPref;
    SharedPreferences.Editor prefEditor;

    ImageButton btnPreview;

    Button btnSave;

    Button btnRetake;

    VideoView mVideoView;

    ProgressDialog uploadDialog;
    String pathOutputFile;

    private TextView txtArtist;
    private TextView txtSongTitle;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finishrec2);

        pref        = PreferenceManager.getDefaultSharedPreferences(this);
        prefEditor  = pref.edit();
        //pathOutputFile = "/sdcard/GalaxyExp/VID_GLXY_EXP_3_1470066371.mp4";


        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                pathOutputFile = null;
            } else {
                pathOutputFile = extras.getString(DBConst.VID_UPLOAD_SOURCE_KEY);
            }
        } else {
            pathOutputFile = (String) savedInstanceState.getSerializable(DBConst.VID_UPLOAD_SOURCE_KEY);
        }
        Log.d(TAG,"Source Video : "+ pathOutputFile);
        initPreview();


        btnSave = (Button) findViewById(R.id.btn_save_video);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    public void run() {
                        uploadDialog = ProgressDialog.show(FinishrecActivity.this,"","Uploading video...",true);
                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        uploadFile(pathOutputFile);
                        //new HttpAsyncTask().execute(DBConst.VID_USER_INSERT_URL);
                    }
                }).start();
            }
        });

        btnRetake = (Button) findViewById(R.id.btn_retake_video);
        btnRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FinishrecActivity.this,VideorecActivity.class);
                startActivity(intent);
            }
        });
    }



    public void initPreview(){
        mVideoView = (VideoView)findViewById(R.id.videoview);
        txtSongTitle = (TextView) findViewById(R.id.txt_songtitle);
        txtArtist = (TextView) findViewById(R.id.txt_artist);

        //Uri uri = Uri.parse(pathOutputFile);
        mVideoView.setVideoPath(pathOutputFile);
        mVideoView.requestFocus();
        btnPreview = (ImageButton) findViewById(R.id.btn_preview_vid);
        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!mVideoView.isPlaying()){
                    btnPreview.setVisibility(View.INVISIBLE);
                    mVideoView.start();
                } else {
                    btnPreview.setVisibility(View.VISIBLE);
                    mVideoView.pause();
                }

            }
        });

        mVideoView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(!mVideoView.isPlaying()){
                    btnPreview.setVisibility(View.INVISIBLE);
                    mVideoView.start();
                } else {
                    btnPreview.setVisibility(View.VISIBLE);
                    mVideoView.pause();
                }
                return false;
            }
        });

        mVideoView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                btnPreview.setVisibility(View.VISIBLE);
                mVideoView.pause();
            }
        });

        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPreview.setVisibility(View.VISIBLE);
            }
        });

        SharedPreferences prefs = getSharedPreferences(DBConst.SPREF_NAME, MODE_PRIVATE);
        int song_id = prefs.getInt(DBConst.SPREF_KEY_SONG,0);
        if(song_id == 1){
            txtSongTitle.setText(DBConst.SONG_TITLE_1);
            txtArtist.setText(DBConst.ARTIST_1);
        } else if(song_id == 2){
            txtSongTitle.setText(DBConst.SONG_TITLE_2);
            txtArtist.setText(DBConst.ARTIST_2);
        } else if(song_id == 3){
            txtSongTitle.setText(DBConst.SONG_TITLE_3);
            txtArtist.setText(DBConst.ARTIST_3);
        } else if(song_id == 4){
            txtSongTitle.setText(DBConst.SONG_TITLE_4);
            txtArtist.setText(DBConst.ARTIST_4);
        } else if(song_id == 5){
            txtSongTitle.setText(DBConst.SONG_TITLE_5);
            txtArtist.setText(DBConst.ARTIST_5);
        }
    }



    public int uploadFile(final String selectedFilePath){
        int serverResponseCode = 0;

        HttpURLConnection connection;
        DataOutputStream dataOutputStream;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";


        int bytesRead,bytesAvailable,bufferSize;
        byte[] buffer;
        int maxBufferSize = 1 * 1024 * 1024;
        File selectedFile = new File(selectedFilePath);


        String[] parts = selectedFilePath.split("/");
        final String fileName = parts[parts.length-1];

        if (!selectedFile.isFile()){
            uploadDialog.dismiss();
            return 0;
        }else{
            try{
                FileInputStream fileInputStream = new FileInputStream(selectedFile);
                URL url = new URL(DBConst.VID_UPLOAD_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);//Allow Inputs
                connection.setDoOutput(true);//Allow Outputs
                connection.setUseCaches(false);//Don't use a cached Copy
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                connection.setRequestProperty("uploaded_file",selectedFilePath);

                //creating new dataoutputstream
                dataOutputStream = new DataOutputStream(connection.getOutputStream());

                //writing bytes to data outputstream
                dataOutputStream.writeBytes(twoHyphens + boundary + lineEnd);
                dataOutputStream.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                        + selectedFilePath + "\"" + lineEnd);

                dataOutputStream.writeBytes(lineEnd);

                //returns no. of bytes present in fileInputStream
                bytesAvailable = fileInputStream.available();
                //selecting the buffer size as minimum of available bytes or 1 MB
                bufferSize = Math.min(bytesAvailable,maxBufferSize);
                //setting the buffer as byte array of size of bufferSize
                buffer = new byte[bufferSize];

                //reads bytes from FileInputStream(from 0th index of buffer to buffersize)
                bytesRead = fileInputStream.read(buffer,0,bufferSize);

                //loop repeats till bytesRead = -1, i.e., no bytes are left to read
                while (bytesRead > 0){
                    //write the bytes read from inputstream
                    dataOutputStream.write(buffer,0,bufferSize);
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable,maxBufferSize);
                    bytesRead = fileInputStream.read(buffer,0,bufferSize);
                }

                dataOutputStream.writeBytes(lineEnd);
                dataOutputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();

                Log.i(TAG, "Server Response is: " + serverResponseMessage + ": " + serverResponseCode);

                //response code of 200 indicates the server status OK
                if(serverResponseCode == 200){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            uploadDialog = ProgressDialog.show(FinishrecActivity.this,"","Inserting User Data...",true);
                            new HttpAsyncTask().execute(DBConst.VID_USER_INSERT_URL);
                        }
                    });
                }
                fileInputStream.close();
                dataOutputStream.flush();
                dataOutputStream.close();



            } catch (FileNotFoundException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FinishrecActivity.this,"File Not Found",Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (MalformedURLException e) {
                e.printStackTrace();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FinishrecActivity.this, "URL error!", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
                Log.d(TAG,"ERROR HERE : " + e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FinishrecActivity.this, "Cannot Read/Write File!", Toast.LENGTH_SHORT).show();
                    }
                });

            }
            uploadDialog.dismiss();
            return serverResponseCode;
        }
    }


    public String post(String url, User user){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(url);
            String json = "";
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate(DBConst.COL_ID, user.id);
            jsonObject.accumulate(DBConst.COL_NAME, user.name);
            jsonObject.accumulate(DBConst.COL_EMAIL, user.email);
            jsonObject.accumulate(DBConst.COL_IG, user.instagram);
            jsonObject.accumulate(DBConst.COL_SONG, user.song);
            jsonObject.accumulate(DBConst.COL_VIDEO, user.video);
            json = jsonObject.toString();

            StringEntity se = new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            HttpResponse httpResponse = httpclient.execute(httpPost);
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }



    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            SharedPreferences prefs = getSharedPreferences(DBConst.SPREF_NAME, MODE_PRIVATE);
            user = new User();
            user.id     = prefs.getInt(DBConst.SPREF_KEY_ID,0);
            user.name   = prefs.getString(DBConst.SPREF_KEY_NAME,"");
            user.email  = prefs.getString(DBConst.SPREF_KEY_EMAIL,"");
            user.instagram  = prefs.getString(DBConst.SPREF_KEY_IG,"");
            user.song     = prefs.getInt(DBConst.SPREF_KEY_SONG,0);
            user.video  = prefs.getString(DBConst.SPREF_KEY_VIDEO,"");

//            user.id         = 1;
//            user.name       = "Testing";
//            user.email      = "testing@email.com";
//            user.instagram  = "testing";
//            user.song       = 1;
//            user.video      = "VID_GLXY_EXP_3_1470066371.mp4";
            return post(urls[0],user);
        }

        @Override
        protected void onPostExecute(String result) {
            uploadDialog.dismiss();
            Toast.makeText(getApplicationContext(), "Data Sent!", Toast.LENGTH_LONG).show();
            finishingAct();

        }
    }

    private void finishingAct(){
        prefEditor.clear();
        Intent intent = new Intent(FinishrecActivity.this,ClosingActivity.class);
        startActivity(intent);
        finish();
    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        Log.d(TAG,result);
        return result;
    }
}
