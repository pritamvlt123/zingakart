package com.example.zingakart.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zingakart.APIClient.Api_Client;
import com.example.zingakart.APIClient.User_Service;
import com.example.zingakart.Helper.DatabaseHandler;
import com.example.zingakart.Helper.SessionManager;
import com.example.zingakart.Model.LoginUserModel;
import com.example.zingakart.Model.UserRegistration;
import com.example.zingakart.R;
import com.example.zingakart.Utils.Config;
import com.example.zingakart.Utils.ConnectivityReceiver;
import com.example.zingakart.app.AppController;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, ConnectivityReceiver.ConnectivityReceiverListener {


    TextInputEditText edit_mail, edit_pass;
    private final ConnectivityReceiver connectivityReceiver = new ConnectivityReceiver();
    Button btn_submit;
    TextView tv_signup;
    ImageView profile_img;
    ConstraintLayout login;
    User_Service userService;
    String total = "";
    String first_name = "";
    String last_name = "";
    String postal_code = "";
    String address = "";
    String city = "";
    String postcode = "";
    String country = "";
    String mail = "";
    String state = "";
    String phone = "";
    String password, uid, regId;
    ProgressBar progressBar;
    private SessionManager sessionManager;
    private DatabaseHandler databaseHandler;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private ProgressDialog mProgress;


    @Override
    public void onBackPressed() {
        sessionManager.skip();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        total = getIntent().getStringExtra("total_price");

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
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
        databaseHandler = new DatabaseHandler(getApplicationContext());
        sessionManager = new SessionManager(getApplicationContext());
        if (sessionManager.isLoggedIn()) {
            Intent intent = new Intent(getApplicationContext(), BillingDetailsActivity.class);
            intent.putExtra("first_name", first_name);
            intent.putExtra("last_name", last_name);
            intent.putExtra("email", mail);
            intent.putExtra("total_price", total);
            startActivity(intent);
            finish();
        }
        setContentView(R.layout.activity_login);
        progressBar = findViewById(R.id.pbHeaderProgress);
        edit_mail = findViewById(R.id.edit_mail);
        edit_pass = findViewById(R.id.edit_pass);
        login = findViewById(R.id.login);
        btn_submit = findViewById(R.id.nextbutton);
        tv_signup = findViewById(R.id.tv_signup);
        edit_mail.getText().toString();
        edit_pass.getText().toString();

        checkconnection();

    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onNetworkConnectionChange(boolean isConnected) {
        showSnack(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver, new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(connectivityReceiver, intentFilter);
        AppController.getInstance().setConnectivityListener(this);
    }


    private void checkconnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    private void showSnack(boolean isConnected) {
        if (isConnected) {
            tv_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Slide slide = new Slide();
                    slide.setSlideEdge(Gravity.START);
                    TransitionManager.beginDelayedTransition(login, slide);
                    Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                    intent.putExtra("total_price", total);
                    startActivity(intent);
                }
            });

            btn_submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //loginUser();
                    String email = edit_mail.getText().toString();
                    String password = edit_pass.getText().toString();
                    if (!isValidEmail(email)) {
                        edit_mail.setError("invalid email");
                        dismissProgressDialog();
                    }
                    if (!isValidPassword(password)) {
                        edit_pass.setError("password should be 6 character");
                        dismissProgressDialog();
                    }
                    if (isValidEmail(email) && isValidPassword(password)) {
                        String type = "login";
                        checkStoredInDataBaseOrNot(uid, email, password, type);
                    }
                }
            });

        } else {
            Snackbar snackbar = Snackbar
                    .make(login, "No internet connection!", Snackbar.LENGTH_LONG)
                    .setActionTextColor(Color.RED)
                    .setDuration(10000)
                    .setAction("UNDO", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            checkConnection();
                        }
                    });

            View sbView = snackbar.getView();
            TextView textView = sbView.findViewById(R.id.snackbar_text);
            textView.setTextColor(Color.YELLOW);
            snackbar.show();

        }
    }


    private void checkStoredInDataBaseOrNot(String userId, final String email, final String password, final String type) {
        mProgress = new ProgressDialog(this);
        mProgress.setTitle("Logging in");
        mProgress.setMessage("Please wait...");
        mProgress.setCancelable(false);
        mProgress.setIndeterminate(true);
        mProgress.show();
        if (AppController.isConnectingToInternet(getApplicationContext())) {
            Map<String, String> params = new HashMap<>();
            if (type.equals("login")) {
                params.put("password", password);
                params.put("email", email);
                params.put("id", userId);
            }
            JsonObject js = new JsonObject();
            js.addProperty("consumer_key", "ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc");
            js.addProperty("consumer_secret", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8");
            js.addProperty("email", edit_mail.getText().toString());
            js.addProperty("password", edit_pass.getText().toString());

            userService = Api_Client.getClient().create(User_Service.class);
            Call<List<LoginUserModel>> call = userService.loginUser("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc", "cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8", edit_mail.getText().toString(), edit_pass.getText().toString());
            call.enqueue(new Callback<List<LoginUserModel>>() {
                @Override
                public void onResponse(Call<List<LoginUserModel>> call, Response<List<LoginUserModel>> response) {
                    if (response.body() == null) {
                        response.body().toString();
                        mProgress.dismiss();
                        Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    } else{
                        LoginUserModel loginUserModel = new LoginUserModel();
                        String first_name = loginUserModel.getFirstName();
                        String last_name = loginUserModel.getLastName();
                        String mail = loginUserModel.getEmail();
                        uid = String.valueOf(loginUserModel.getId());
//                        String address = loginUserModel.getBilling().getAddress1();
//                        String city = loginUserModel.getBilling().getCity();
//                        String state = loginUserModel.getBilling().getState();
//                        String phone = loginUserModel.getBilling().getPhone();
//                        String postal_code = loginUserModel.getBilling().getPostcode();
//                        String country = loginUserModel.getBilling().getCountry();
                        Toast.makeText(LoginActivity.this, "Logged in", Toast.LENGTH_SHORT).show();
                        Slide slide = new Slide();
                        slide.setSlideEdge(Gravity.START);
                        TransitionManager.beginDelayedTransition(login, slide);
                        Intent intent = new Intent(LoginActivity.this, BillingDetailsActivity.class);
                        intent.putExtra("first_name", first_name);
                        intent.putExtra("last_name", last_name);
//                        intent.putExtra("address_1", address);
//                        intent.putExtra("city", city);
//                        intent.putExtra("state", state);
//                        intent.putExtra("phone", phone);
//                        intent.putExtra("country", country);
//                        intent.putExtra("postcode", postal_code);
                        intent.putExtra("email", mail);
                        intent.putExtra("total_price", total);
                        startActivity(intent);
                    }
                    if (type.equalsIgnoreCase("login")) {
                        sessionManager.setLoggedIn(true);
                        databaseHandler.deleteUsers();
                        databaseHandler.addUser(uid, mail, first_name, last_name);
                        Slide slide = new Slide();
                        slide.setSlideEdge(Gravity.START);
                        TransitionManager.beginDelayedTransition(login, slide);
                        Intent intent = new Intent(LoginActivity.this, BillingDetailsActivity.class);
                        intent.putExtra("total_price", total);
                        startActivity(intent);
                    }
                    dismissProgressDialog();
                    finish();
                }

                @Override
                public void onFailure(Call<List<LoginUserModel>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                    databaseHandler.deleteUsers();
                    sessionManager.logoutUser();
                    dismissProgressDialog();
                }
            });
        }
    }

    private boolean isValidPassword(String password) {
        return password != null && password.length() > 5;

    }

    private boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();

    }

    private void dismissProgressDialog() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }

    }

    // Method to manually check connection status
    private void checkConnection() {
        boolean isConnected = ConnectivityReceiver.isConnected();
        showSnack(isConnected);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}