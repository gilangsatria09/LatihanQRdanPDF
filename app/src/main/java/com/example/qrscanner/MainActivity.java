package com.example.qrscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button scanner = (Button)findViewById(R.id.qrcode);
        scanner.setOnClickListener(this);

        Button pdf = (Button)findViewById(R.id.pdf);
        pdf.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.qrcode:
                Intent i = new Intent(MainActivity.this,QRCodeActivity.class);
                startActivity(i);
                break;
            case R.id.pdf:
                Intent a = new Intent(MainActivity.this,PDFActivity.class);
                startActivity(a);
                break;
        }
    }
}
