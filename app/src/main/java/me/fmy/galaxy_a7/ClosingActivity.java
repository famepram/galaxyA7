package me.fmy.galaxy_a7;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ClosingActivity extends AppCompatActivity {

    Button btn_back_start;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closing);

        btn_back_start = (Button)findViewById(R.id.btn_back_start);
        btn_back_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ClosingActivity.this,SplashActivity.class);
                //intent.putExtra(DBConst.VID_UPLOAD_SOURCE_KEY, pathOutputFile);
                startActivity(intent);
                finish();
            }
        });

    }
}
