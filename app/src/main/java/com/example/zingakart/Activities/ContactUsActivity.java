package com.example.zingakart.Activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.zingakart.R;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class ContactUsActivity extends AppCompatActivity {

    EditText et_name, et_mail, comments;
    Button submit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_us);
        et_name = findViewById(R.id.et_name);
        et_mail = findViewById(R.id.et_mail);
        comments = findViewById(R.id.comments);
        et_name.getText().toString();
        et_mail.getText().toString();
        comments.getText().toString();

        submit = findViewById(R.id.submit);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(et_name.getText().toString().isEmpty() || et_mail.getText().toString().isEmpty() || comments.getText().toString().isEmpty())
                {
                    new AlertDialog.Builder(ContactUsActivity.this)
                            .setTitle("Note")
                            .setMessage("Please fill out the details")
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                   dialog.cancel();
                                }
                            })
                            .show();
                }
                else
                {
                    Toast.makeText(ContactUsActivity.this, "Thanks for submitting your feedback", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
            }
        });

    }
}
