package com.bijendra.myqrcode.qrcodereadwrite;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void gotoQrCodeOperation(View view)
    {
         if(view.getTag().toString().equalsIgnoreCase("0"))
             startActivity(new Intent(this,GenerateQRCodeActivity.class));
        if(view.getTag().toString().equalsIgnoreCase("1"))
            startActivity(new Intent(this,ReadQRCodeActivity.class));
    }
}
