package com.yoji.callorsendsms;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.SmsManager;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumberEdtTxt;
    private EditText messageTxtEdtTxt;
    private Button callBtn;
    private Button sendSMSBtn;
    private final int CALL_PHONE_PERMISSION = 11;
    private final int SEND_SMS_PERMISSION = 12;

    TextWatcher tw = new PhoneNumberFormattingTextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            super.onTextChanged(s, start, before, count);
            String phoneNumber = phoneNumberEdtTxt.getText().toString().trim();
            String messageTxt = messageTxtEdtTxt.getText().toString().trim();

            callBtn.setEnabled(!phoneNumber.isEmpty());
            sendSMSBtn.setEnabled(!phoneNumber.isEmpty() && !messageTxt.isEmpty());
        }
    };

    private final View.OnClickListener callBtnOnClickListener = v -> call();

    private final View.OnClickListener sendSMSBtnOnclickListener = v -> sendSMS();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
    }

    private void initViews() {
        phoneNumberEdtTxt = findViewById(R.id.phoneNumberEdtTxtId);
        callBtn = findViewById(R.id.callBtnId);
        sendSMSBtn = findViewById(R.id.sendSMSId);
        messageTxtEdtTxt = findViewById(R.id.messageTxtEdtTxtId);

        phoneNumberEdtTxt.addTextChangedListener(tw);
        messageTxtEdtTxt.addTextChangedListener(tw);
        callBtn.setOnClickListener(callBtnOnClickListener);
        sendSMSBtn.setOnClickListener(sendSMSBtnOnclickListener);
    }

    private void call() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION);
        } else {
            String phoneNumber = phoneNumberEdtTxt.getText().toString().trim();
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
    }

    private void sendSMS() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION);
        } else {
            String messageTxt = messageTxtEdtTxt.getText().toString().trim();
            String phoneNumber = phoneNumberEdtTxt.getText().toString().trim();
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, messageTxt, null, null);
            messageTxtEdtTxt.setText("");
            Toast.makeText(this, "Message send", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case CALL_PHONE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    call();
                }
                break;
            case SEND_SMS_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    sendSMS();
                }
                break;
        }
    }
}