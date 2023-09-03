package ml.guru.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class CustomerActivity extends AppCompatActivity {

    ImageView imageView;
    Preference pref = null;
    private Spinner spinner;
    CustomArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer);

        pref = new Preference(this);
        pref.setCustomer("");

        imageView = findViewById(R.id.image_logo);
        String imagePath = pref.getImageFilePath();

        if (!imagePath.isEmpty())
        {
            Log.d("scanbarcodeqrcode", "CustomerActivity:OnCreate: imagePath=" + imagePath);
            Uri fileUri = Uri.parse(imagePath);
            imageView.setImageURI(fileUri);
        }

        String[] customs =pref.getStringArray("customers");
        spinner = findViewById(R.id.spinner1);

        ArrayList<String> itemList = new ArrayList<>();

        // Create an empty ArrayAdapter
        if (customs != null)
        {
            itemList = new ArrayList<>(Arrays.asList(customs));
        }

        adapter = new CustomArrayAdapter<>(this, R.layout.custom_spinner_item, itemList, Color.BLUE);

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //adapter.setDropDownViewResource(R.layout.custom_spinner_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Handle item selection event
                String strCustomer = (String) parent.getItemAtPosition(position);
                pref.setCustomer(strCustomer);
                Log.d("scanbarcodeqrcode", "CustomerActivity:OnCreate:spinner.setOnItemSelectedListener: setOnClickListener strCustomer=" + strCustomer);
                //Toast.makeText(getApplicationContext(), "Selected item: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Handle case when no item is selected
            }
        });

        ImageButton button_back = findViewById(R.id.imageButton_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "CustomerActivity:OnCreate:imageButton_back: setOnClickListener");
                finish();
            }
        });

        ImageButton button_submit = findViewById(R.id.imageButton_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "CustomerActivity:OnCreate:imageButton_submit: setOnClickListener");

                if (pref.getCustomer().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please, select customer", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(view.getContext(), ScanActivity.class);
                view.getContext().startActivity(intent);
                finish();
            }
        });

        Button button_confirm = findViewById(R.id.btnScan_confirm);
        button_confirm.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "CustomerActivity:OnCreate:btnScan_confirm: setOnClickListener");

                if (pref.getCustomer().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please, select customer", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(view.getContext(), ScanActivity.class);
                view.getContext().startActivity(intent);
                finish();
            }
        });
    }
}