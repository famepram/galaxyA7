package me.fmy.galaxy_a7;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;

import me.fmy.galaxy_a7.helpers.BTConn;

public class SplashActivity extends Activity {

    private BTConn mBTConn = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            mBTConn = new BTConn();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(2000);

                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    if(mBTConn != null){
                        if(mBTConn.checkConn()){
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                                }
                            });
                            mBTConn.closeSocket();
                            mBTConn = null;
                            Intent intent = new Intent(SplashActivity.this,MainActivity.class);
                            startActivity(intent);
                            SplashActivity.this.finish();
                        } else {
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    Toast.makeText(getApplicationContext(),"Device connection failed",Toast.LENGTH_SHORT).show();
                                }
                            });
                            //Toast.makeText(getApplicationContext(),"Device connection failed",Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(getApplicationContext(),"Device connection failed",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                }
            }
        };
        timerThread.start();

    }
}
