package com.example.zingakart.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zingakart.APIClient.Api_Client;
import com.example.zingakart.APIClient.User_Service;
import com.example.zingakart.R;
import com.example.zingakart.Utils.Config;
import com.example.zingakart.app.AppController;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity {

    EditText et_first_name, et_last_name, et_mail, et_password, et_username;
    Button btn_submit;
    TextView tv_signin;
    String total = "";
    User_Service userService;
    ProgressBar progressBar;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    String regId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_APP);
                    SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
                    regId = pref.getString("regId", null);
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    String message = intent.getStringExtra("message");
                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                }
            }
        };
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        regId = pref.getString("regId", null);
        setContentView(R.layout.activity_sign_up);

        progressBar = findViewById(R.id.pbHeaderProgress);

        et_first_name = findViewById(R.id.edit_first_name);
        et_last_name = findViewById(R.id.edit_last_name);
        et_username = findViewById(R.id.edit_username);
        et_mail = findViewById(R.id.edit_mail);
        et_password = findViewById(R.id.edit_pass);
        tv_signin = findViewById(R.id.tv_signin);

        et_first_name.getText().toString();
        et_last_name.getText().toString();
        et_username.getText().toString();
        et_password.getText().toString();
        et_mail.getText().toString();



        total =  getIntent().getStringExtra("total_price");

        btn_submit = findViewById(R.id.nextbutton);

        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isValidEmail(et_mail.getText().toString())) {
                    et_mail.setError("invalid email");
                }
                if (!isValidPassword(et_password.getText().toString())) {
                    et_password.setError("password should be 6 character");
                } else {
                    register();
                }

            }
        });

        tv_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                intent.putExtra("total_price",total);
                startActivity(intent);
            }
        });
    }


    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    private boolean isValidPassword(String pass) {
        return pass != null && pass.length() > 5;
    }


    private void register() {
        progressBar.setVisibility(View.VISIBLE);
        JsonObject js = new JsonObject();
        js.addProperty("consumer_key","ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc");
        js.addProperty("consumer_secret", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8");
        js.addProperty("email", et_mail.getText().toString());
        js.addProperty("password", et_password.getText().toString());
        js.addProperty("username", et_username.getText().toString());

        userService = Api_Client.getClient().create(User_Service.class);
        Call<JsonObject> call = userService.getUser("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc ","cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8",et_mail.getText().toString(),et_password.getText().toString(),et_username.getText().toString());
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if(response.isSuccessful())
                    {
                        progressBar.setVisibility(View.GONE);
                        response.body().toString();
                        Toast.makeText(SignUpActivity.this, "Registered Successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                        intent.putExtra("total_price",total);
                        startActivity(intent);
                    }
                    else
                    {
                        Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Toast.makeText(SignUpActivity.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
    }
}