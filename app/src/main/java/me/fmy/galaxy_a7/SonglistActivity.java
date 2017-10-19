package me.fmy.galaxy_a7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import me.fmy.galaxy_a7.helpers.DBConst;
import me.fmy.galaxy_a7.helpers.DBHelper;

public class SonglistActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = "GALAXY_A7_SONGLISTACTIVITY";

    LinearLayout LLA;

    LinearLayout LLB;

    LinearLayout LLC;

    LinearLayout LLD;

    LinearLayout LLE;

    Button btn_song_1;

    Button btn_song_2;

    Button btn_song_3;

    Button btn_song_4;

    Button btn_song_5;

    DBHelper DBHelper;

    SharedPreferences pref;

    SharedPreferences.Editor prefEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_songlist);
        DBHelper    = new DBHelper(this);
        pref        = getApplicationContext().getSharedPreferences(DBConst.SPREF_NAME, 0);
        prefEditor  = pref.edit();
        _initButtonGroup();
    }

    protected void _initButtonGroup(){
//        LLA = (LinearLayout) findViewById(R.id.wrap_track_a);
//        LLA.setOnClickListener(this);
//        LLB = (LinearLayout) findViewById(R.id.wrap_track_b);
//        LLB.setOnClickListener(this);
//        LLC = (LinearLayout) findViewById(R.id.wrap_track_c);
//        LLC.setOnClickListener(this);
//        LLD = (LinearLayout) findViewById(R.id.wrap_track_d);
//        LLD.setOnClickListener(this);
//        LLE = (LinearLayout) findViewById(R.id.wrap_track_e);
//        LLE.setOnClickListener(this);
        btn_song_1 = (Button) findViewById(R.id.btn_select_song_1);
        btn_song_1.setOnClickListener(this);
        btn_song_2 = (Button) findViewById(R.id.btn_select_song_2);
        btn_song_2.setOnClickListener(this);
        btn_song_3 = (Button) findViewById(R.id.btn_select_song_3);
        btn_song_3.setOnClickListener(this);
        btn_song_4 = (Button) findViewById(R.id.btn_select_song_4);
        btn_song_4.setOnClickListener(this);
        btn_song_5 = (Button) findViewById(R.id.btn_select_song_5);
        btn_song_5.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        int chosen_song;
        switch(v.getId()){
            case R.id.btn_select_song_1:
                chosen_song = 1;
                break;
            case R.id.btn_select_song_2:
                chosen_song = 2;
                break;
            case R.id.btn_select_song_3:
                chosen_song = 3;
                break;
            case R.id.btn_select_song_4:
                chosen_song = 4;
                break;
            case R.id.btn_select_song_5:
                chosen_song = 5;
                break;
            default:
                chosen_song = 0;
                break;
        }
        prefEditor.putInt(DBConst.SPREF_KEY_SONG,chosen_song);
        prefEditor.commit();
        Log.i(TAG,Integer.toString(chosen_song));
        Intent intent = new Intent(SonglistActivity.this,VideorecActivity.class);
        startActivity(intent);
    }
}
