package me.fmy.galaxy_a7;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class TncActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tnc);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TncActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
