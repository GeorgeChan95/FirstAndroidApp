package com.george.chapter07_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ContactAddActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_contact_name;
    private EditText et_contact_phone;
    private EditText et_contact_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_add);
        et_contact_name = findViewById(R.id.et_contact_name);
        et_contact_phone = findViewById(R.id.et_contact_phone);
        et_contact_email = findViewById(R.id.et_contact_email);

        findViewById(R.id.btn_add_contact).setOnClickListener(this);
        findViewById(R.id.btn_read_contact).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

    }
}