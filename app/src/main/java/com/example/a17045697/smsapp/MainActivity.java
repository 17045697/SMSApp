package com.example.a17045697.smsapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText etTo;
    EditText etContent;
    Button btnSend;
    Button btnSendAll;
    BroadcastReceiver br = new MessageReceiver();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        checkPermission();
        etTo = findViewById(R.id.editTextName);
        etContent = findViewById(R.id.editTextMessage);
        btnSend = findViewById(R.id.buttonSend);
        btnSendAll = findViewById(R.id.buttonSendAll);


        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        this.registerReceiver(br, filter);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String to = etTo.getText().toString();
                String message = etContent.getText().toString();
                String numbers[] = to.split(",");
                SmsManager smsManager = SmsManager.getDefault();
                for(String number : numbers){
                    smsManager.sendTextMessage(number, null, message, null, null);
                }
                Toast.makeText(MainActivity.this,
                        "Message sent", Toast.LENGTH_LONG).show();
                etContent.setText("");
            }

        });

        btnSendAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    String to = etTo.getText().toString();
                    String message = etContent.getText().toString();
                    Uri smsUri = Uri.parse("tel:" + to);
                    Intent intent = new Intent(Intent.ACTION_VIEW, smsUri);
                    intent.putExtra("address", to);
                    intent.putExtra("sms_body", message);
                    intent.setType("vnd.android-dir/mms-sms");//here setType will set the previous data null.
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }

                }else{
                    String to = etTo.getText().toString();
                    String message = etContent.getText().toString();
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse("sms:" + to));
                    intent.putExtra("sms_body", message);
                    startActivity(intent);
                }

            }
        });
    }

    private void checkPermission() {
        int permissionSendSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);
        int permissionRecvSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);
        if (permissionSendSMS != PackageManager.PERMISSION_GRANTED &&
                permissionRecvSMS != PackageManager.PERMISSION_GRANTED) {
            String[] permissionNeeded = new String[]{Manifest.permission.SEND_SMS,
                    Manifest.permission.RECEIVE_SMS};
            ActivityCompat.requestPermissions(this, permissionNeeded, 1);

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(br);
    }
}
