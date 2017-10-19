package me.fmy.galaxy_a7;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import me.fmy.galaxy_a7.helpers.DBConst;
import me.fmy.galaxy_a7.helpers.DBHelper;
import me.fmy.galaxy_a7.models.User;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etxtName;

    EditText etxtEmail;

    EditText etxtIG;

    Button btnReg;

    DBHelper DBHelper;

    SharedPreferences pref;

    SharedPreferences.Editor prefEditor;

    TextView txtLinkTNC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        _initElement();
    }

    private void _initElement(){
        etxtName    = (EditText) findViewById(R.id.etxt_name);
        etxtEmail   = (EditText) findViewById(R.id.etxt_email);
        etxtIG      = (EditText) findViewById(R.id.etxt_instagram);
        btnReg      = (Button) findViewById(R.id.btn_register);
        txtLinkTNC  = (TextView) findViewById(R.id.txt_link_tnc);
        txtLinkTNC.setPaintFlags(txtLinkTNC.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtLinkTNC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,TncActivity.class);
                startActivity(intent);
            }
        });

        DBHelper    = new DBHelper(this);
        pref        = getApplicationContext().getSharedPreferences(DBConst.SPREF_NAME, 0);
        prefEditor  = pref.edit();
        btnReg.setOnClickListener(this);
    }

    protected void _clearEditext(){
        etxtName.setText("");
        etxtEmail.setText("");
        etxtIG.setText("");
    }

    @Override
    public void onClick(View v) {
        String name = etxtName.getText().toString();
        String email = etxtEmail.getText().toString();
        String ig = etxtIG.getText().toString();
        if(_validationForm(name,email,ig)){
            _storingData(name,email,ig);

            Intent intent = new Intent(MainActivity.this,SonglistActivity.class);
            startActivity(intent);
            finish();
        }
    }

    protected void _storingData(String name, String email, String instagram){
        User user       = new User();
        user.name       = name;
        user.email      = email;
        user.instagram  = instagram;
        int user_id = (int) (long) DBHelper.addUser(user);

        prefEditor.putInt(DBConst.SPREF_KEY_ID,user_id);
        prefEditor.putString(DBConst.SPREF_KEY_NAME,user.name);
        prefEditor.putString(DBConst.SPREF_KEY_EMAIL,user.email);
        prefEditor.putString(DBConst.SPREF_KEY_IG,user.instagram);
        prefEditor.commit();

        _clearEditext();
        _logUser();
    }


    private boolean isEmpty(String etText) {
        if (etText.trim().length() > 0)
            return false;

        return true;
    }

    private boolean _validationForm(String name, String email, String ig){
        if( !isEmpty(name) && !isEmpty(email) &&  !isEmpty(ig)){
            return true;
        } else {
            if(isEmpty(name)){
                Toast.makeText(getBaseContext(), getResources().getString(R.string.register_invalid_name),Toast.LENGTH_LONG).show();
            } else if(isEmpty(email)) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.register_invalid_email),Toast.LENGTH_LONG).show();
            } else if(isEmpty(ig)) {
                Toast.makeText(getBaseContext(), getResources().getString(R.string.register_invalid_ig),Toast.LENGTH_LONG).show();
            }
            return false;
        }
    }

    protected void _logUser(){
        List<User> Users = DBHelper.getAllUsers();
        for(int i=0; i < Users.size(); i++) {
            User user = Users.get(i);
            String id = Integer.toString(user.id);
            String name = user.name;
            String email = user.email;
            String instagram = user.instagram;
            String struser = id+"---"+name+"---"+email+"---"+instagram;
            Log.i("Log user Row",struser);
        }
    }

}
