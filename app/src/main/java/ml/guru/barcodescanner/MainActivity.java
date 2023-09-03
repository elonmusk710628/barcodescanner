package ml.guru.barcodescanner;

import static android.se.omapi.Session.*;
import static nl.qbusict.cupboard.CupboardFactory.cupboard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Properties;


public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Preference pref = new Preference(this);

        ImageView imageView = findViewById(R.id.image_logo);
        String imagePath = pref.getImageFilePath();
        if (!imagePath.isEmpty())
        {
            Log.d("scanbarcodeqrcode", "MainActivity:OnCreate: imagePath=" + imagePath);
            Uri fileUri = Uri.parse(imagePath);
            imageView.setImageURI(fileUri);
        }

        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        if (day == 1)
        {
            long last_month = pref.getLastMonth();
            if (last_month != month)
            {
                if (!pref.getUserCode().equals(""))
                {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String dateTimeString = dateFormat.format(calendar.getTime());

                    String strBody = "Full Name: " + pref.getUserFullname() + "<br>";
                    strBody += "Company Name: " + pref.getUserCompany() + "<br>";
                    strBody += "Email: " + pref.getUserEmail() + "<br>";
                    strBody += "Code: " + pref.getUserCode() + "<br>";
                    strBody += "Scans: " + String.valueOf(pref.getScans()) + "<br>";
                    strBody += "Data: " + dateTimeString;

                    EmailSender mailManager = EmailSender.getInstance();
                    mailManager.sendEmail("reports@safe-stock.co.uk", pref.getUserCode() + ": Scans", strBody, "" , "");
                    //mailManager.sendEmail("elonmusk710628@gmail.com", pref.getUserCode() + ": Scans", strBody, "" , "");

                    pref.setScans(0);
                    pref.setLastMonth(month);
                }
            }
        }

        ImageButton button = findViewById(R.id.imageButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "MainActivity: OnCreate: click 'settings' imageButton");
                Intent intent = new Intent(view.getContext(), SettingsActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        Button button_start = findViewById(R.id.imageButtonStart);
        button_start.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "MainActivity: OnCreate: click 'settings' imageButton");
                //if (pref.getCustomers() == null || pref.getCustomers().length == 0)
                if (pref.getStringArray("customers") == null || pref.getStringArray("customers").length == 0)
                {
                    Toast.makeText(getApplicationContext(), "Please, add customer in settings.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pref.getEmailAddress().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Set email address in settings.", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(view.getContext(), CustomerActivity.class);
                view.getContext().startActivity(intent);
            }
        });


        // Check and request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Your custom logic here

        Preference pref = new Preference(this);

        ImageView imageView = findViewById(R.id.image_logo);
        String imagePath = pref.getImageFilePath();
        if (!imagePath.isEmpty())
        {
            Log.d("scanbarcodeqrcode", "MainActivity:OnCreate: imagePath=" + imagePath);
            Uri fileUri = Uri.parse(imagePath);
            imageView.setImageURI(fileUri);
        }

    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

}