package com.example.zingakart.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zingakart.APIClient.Api_Client;
import com.example.zingakart.APIClient.User_Service;
import com.example.zingakart.Model.LoginUserModel;
import com.example.zingakart.Model.OrderDetails;
import com.example.zingakart.Model.Orderadd;
import com.example.zingakart.R;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BillingDetailsActivity extends AppCompatActivity {

    private static final String TAG = "this" ;
    Button btn_payment, btnSubmitAddress;
    String total = "";
    String first_name = "";
    String last_name = "";
    String address = "";
    String city = "";
    String state = "";
    String phone = "";
    String postal_code = "";
    String email = "";
    String country = "";
    TextView tv_total, tv_grand_total;
    ImageView ic_back;
    EditText etDefaultUserName, etDefaultAddress, etDefaultCity, etDefaultState, etDefaultZipcode, etDefaultCountry, etDefaultNumber,
            etUserName, etShipingAddress, etShipingCity, etShipingState, etZipcode, etShipingLandmark, etCountry, etShipingNumber;
    CheckBox cbSameAddress;
    String formatted_total = "";
    User_Service userService;
    private ProgressDialog mProgress;
    String json = " {\n" +
            "  \"payment_method\": \"bacs\",\n" +
            "  \"payment_method_title\": \"Direct Bank Transfer\",\n" +
            "  \"set_paid\": true,\n" +
            "  \"billing\": {\n" +
            "    \"first_name\": \"Pritam\",\n" +
            "    \"last_name\": \"Doe\",\n" +
            "    \"address_1\": \"969 Market\",\n" +
            "    \"address_2\": \"\",\n" +
            "    \"city\": \"San Francisco\",\n" +
            "    \"state\": \"CA\",\n" +
            "    \"postcode\": \"94103\",\n" +
            "    \"country\": \"US\",\n" +
            "    \"email\": \"john.doe@example.com\",\n" +
            "    \"phone\": \"(555) 555-5555\"\n" +
            "  },\n" +
            "  \"shipping\": {\n" +
            "    \"first_name\": \"John\",\n" +
            "    \"last_name\": \"Doe\",\n" +
            "    \"address_1\": \"969 Market\",\n" +
            "    \"address_2\": \"\",\n" +
            "    \"city\": \"San Francisco\",\n" +
            "    \"state\": \"CA\",\n" +
            "    \"postcode\": \"94103\",\n" +
            "    \"country\": \"US\"\n" +
            "  },\n" +
            "  \"line_items\": [\n" +
            "    {\n" +
            "      \"product_id\": 93,\n" +
            "      \"quantity\": 2\n" +
            "    },\n" +
            "    {\n" +
            "      \"product_id\": 22,\n" +
            "      \"variation_id\": 23,\n" +
            "      \"quantity\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"shipping_lines\": [\n" +
            "    {\n" +
            "      \"method_id\": \"flat_rate\",\n" +
            "      \"method_title\": \"Flat Rate\",\n" +
            "      \"total\": \"10.00\"\n" +
            "    }\n" +
            "  ]\n" +
            " }";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_details);
        btn_payment = findViewById(R.id.btnCheckOut);
        ic_back = findViewById(R.id.ic_back);
        tv_total = findViewById(R.id.tv_total);
        tv_grand_total = findViewById(R.id.tv_grand_total);
        etDefaultUserName = findViewById(R.id.etDefaultUserName);
        etDefaultAddress = findViewById(R.id.etDefaultAddress);
        etDefaultCity = findViewById(R.id.etDefaultCity);
        etDefaultState = findViewById(R.id.etDefaultState);
        etDefaultZipcode = findViewById(R.id.etDefaultZipcode);
        etDefaultCountry = findViewById(R.id.etDefaultCountry);
        etDefaultNumber = findViewById(R.id.etDefaultNumber);
        etUserName = findViewById(R.id.etUserName);
        etShipingAddress = findViewById(R.id.etShipingAddress);
        etShipingCity = findViewById(R.id.etShipingCity);
        etShipingState = findViewById(R.id.etShipingState);
        etZipcode = findViewById(R.id.etZipcode);
        etShipingLandmark = findViewById(R.id.etShipingLandmark);
        etCountry = findViewById(R.id.etCountry);
        etShipingNumber = findViewById(R.id.etShipingNumber);
        btnSubmitAddress = findViewById(R.id.btnSubmitAddress);
        cbSameAddress = findViewById(R.id.cbSameAddress);


        Intent intent = getIntent();
        if (getIntent().getExtras() != null) {
            //first_name = intent.getStringExtra("first_name");
            //last_name = intent.getStringExtra("last_name");
//            address = intent.getStringExtra("address");
//            city = intent.getStringExtra("city");
//            state = intent.getStringExtra("state");
//            phone = intent.getStringExtra("phone");
//            postal_code = intent.getStringExtra("postcode");
            email = intent.getStringExtra("email");
//            country = intent.getStringExtra("country");
            total = intent.getStringExtra("total_price");
            //etDefaultUserName.setText(first_name + " " + last_name);
            etDefaultAddress.setText(address);
            etDefaultCity.setText(city);
            etDefaultState.setText(state);
            etDefaultState.setText(state);
            etDefaultZipcode.setText(postal_code);
            etDefaultNumber.setText(phone);

        } else {
            etDefaultUserName.setText("");
            etDefaultAddress.setText("");
            etDefaultCity.setText("");
            etDefaultState.setText("");
            etDefaultZipcode.setText("");
            etDefaultCountry.setText("");
            etDefaultNumber.setText("");
        }


        btn_payment.setText("Continue to pay" + " " +"\u20B9"+total);

        cbSameAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    etUserName.setText(etDefaultUserName.getText().toString());
                    etShipingAddress.setText(etDefaultAddress.getText().toString());
                    etShipingCity.setText(etDefaultCity.getText().toString());
                    etShipingState.setText(etDefaultState.getText().toString());
                    etZipcode.setText(etDefaultZipcode.getText().toString());
                    etCountry.setText(etDefaultCountry.getText().toString());
                    etShipingNumber.setText(etDefaultNumber.getText().toString());
                } else {
                    etUserName.setText("");
                    etShipingLandmark.setText("");
                    etShipingAddress.setText("");
                    etShipingNumber.setText("");
                    etZipcode.setText("");
                    etCountry.setText("");
                    etShipingCity.setText("");
                    etShipingState.setText("");
                }

            }
        });

        btnSubmitAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etDefaultUserName.length() == 0) {
                    etDefaultUserName.setError("Please enter your Name");
                    etDefaultUserName.requestFocus();
                } else if (etDefaultAddress.length() == 0) {
                    etDefaultAddress.setError("Please enter your billing Address");
                    etDefaultAddress.requestFocus();
                } else if (etDefaultCity.length() == 0) {
                    etDefaultCity.setError("Please enter your billing Address");
                    etDefaultCity.requestFocus();
                } else if (etDefaultState.length() == 0) {
                    etDefaultState.setError("Please enter your State");
                    etDefaultState.requestFocus();
                } else if (etDefaultZipcode.length() == 0) {
                    etDefaultZipcode.setError("Please enter your pincode");
                    etDefaultZipcode.requestFocus();
                } else if (etDefaultCountry.length() == 0) {
                    etDefaultCountry.setError("Please enter your country");
                    etDefaultCountry.requestFocus();
                } else if (etDefaultNumber.length() == 0) {
                    etDefaultNumber.setError("Please enter your mobile number");
                    etDefaultNumber.requestFocus();
                } else {
                    generateOrder();
                    //Toast.makeText(BillingDetailsActivity.this, "Address Details saved", Toast.LENGTH_SHORT).show();
                }
            }
        });


        ic_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        btn_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etDefaultUserName.length() == 0) {
                    etDefaultUserName.setError("Please enter your Name");
                    etDefaultUserName.requestFocus();
                } else if (etDefaultAddress.length() == 0) {
                    etDefaultAddress.setError("Please enter your billing Address");
                    etDefaultAddress.requestFocus();
                } else if (etDefaultCity.length() == 0) {
                    etDefaultCity.setError("Please enter your billing Address");
                    etDefaultCity.requestFocus();
                } else if (etDefaultState.length() == 0) {
                    etDefaultState.setError("Please enter your State");
                    etDefaultState.requestFocus();
                } else if (etDefaultZipcode.length() == 0) {
                    etDefaultZipcode.setError("Please enter your pincode");
                    etDefaultZipcode.requestFocus();
                } else if (etDefaultCountry.length() == 0) {
                    etDefaultCountry.setError("Please enter your country");
                    etDefaultCountry.requestFocus();
                } else if (etDefaultNumber.length() == 0) {
                    etDefaultNumber.setError("Please enter your mobile number");
                    etDefaultNumber.requestFocus();
                } else {
                    Intent intent = new Intent(BillingDetailsActivity.this, PaymentActivity.class);
                    intent.putExtra("total_price", total);
                    intent.putExtra("email", email);
                    startActivity(intent);
                }
            }
        });
    }

    private void generateOrder() {
        mProgress = new ProgressDialog(BillingDetailsActivity.this);
        mProgress.setMessage("Please wait..");
        mProgress.setCancelable(false);
        mProgress.show();



        userService = Api_Client.getClient().create(User_Service.class);
        Call<Orderadd> call = userService.createOrder("ck_64b548f4f1eb03d4fbb8fc63c8a0c4da05a14bbc","cs_6cdd9def7eb17a0020de915e4df7314db04ea2e8",json);
        call.enqueue(new Callback<Orderadd>() {
            @Override
            public void onResponse(Call<Orderadd> call, Response<Orderadd> response) {
                //hiding progress dialog
                mProgress.dismiss();
                if(response.isSuccessful()){
                    response.body().toString();
                    Toast.makeText(BillingDetailsActivity.this, "Response" + response.body().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Orderadd> call, Throwable t) {
                mProgress.dismiss();
                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}