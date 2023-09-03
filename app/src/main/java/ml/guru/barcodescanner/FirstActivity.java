package ml.guru.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

//https://test.factorgpu.com/app/smtp.php

public class FirstActivity extends AppCompatActivity {

    Preference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = new Preference(this);

        if (pref.getAcceptFlag())
        {
            if (pref.getActivateFlag())
            {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else
            {
                Intent intent = new Intent(this, ActivateActivity.class);
                startActivity(intent);
            }

            finish();
        }

        setContentView(R.layout.activity_first);

        ImageButton button_Accept = findViewById(R.id.btnScan_Accept);
        button_Accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "MainActivity: OnCreate: btnScan_Accept");
                pref.setAcceptFlag(true);

                Intent intent = new Intent(view.getContext(), ActivateActivity.class);
                view.getContext().startActivity(intent);
                finish();
            }
        });

        ImageButton button_Decline = findViewById(R.id.btnScan_Decline);
        button_Decline.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "MainActivity: OnCreate: btnScan_Decline");
                finish();
            }
        });
    }
}