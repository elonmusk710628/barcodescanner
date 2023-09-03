package ml.guru.barcodescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ScanResultActivity extends AppCompatActivity {

    ListView listView_scans;

    private ArrayAdapter<String> adapter_scans;

    ArrayList<String> itemList_scans = new ArrayList<>();
    int selectedPosition = -1;

    Preference pref = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        pref = new Preference(this);

        /*
        String callingActivityName = getIntent().getStringExtra("callingActivity");

        if (callingActivityName == null)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:Calling ActivityName= null");
            return;
        }

        Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:Calling Activity=" + callingActivityName);

        if (!callingActivityName.equals(ScanActivity.class.getSimpleName()))
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:Calling ActivityName is mistake.");
            return;
        }

         */

        TextView textView_Customer = findViewById(R.id.textView_customer);
        textView_Customer.setText(pref.getCustomer());

        String[] scanArray = pref.getStringArray("scan_results");

        String[] changeArray = changeDataFormat(scanArray);

        if (changeArray != null)
        {
            itemList_scans = new ArrayList<>(Arrays.asList(changeArray));
        }

        adapter_scans = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, itemList_scans);
        listView_scans = findViewById(R.id.listView);
        adapter_scans.setDropDownViewResource(R.layout.custom_spinner_item);
        listView_scans.setAdapter(adapter_scans);
        listView_scans.setSelector(R.drawable.list_item_selector);
        adapter_scans.notifyDataSetChanged();

        listView_scans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Delete the selected item
                selectedPosition = position;
                // Clear the previous selection (if any) when a new item is selected
                listView_scans.clearChoices();
                // Set the current item as selected
                listView_scans.setItemChecked(position, true);
            }
        });

        ImageButton button_back = findViewById(R.id.imageButton_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:imageButton_back: setOnClickListener");
                finish();
            }
        });

        Button button_submit = findViewById(R.id.buttonConfirmSend);
        button_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:buttonConfirmSend: setOnClickListener");
                    if (itemList_scans.size() == 0)
                    {
                        Toast.makeText(getApplicationContext(), "Scan barcode", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String filename = "product.txt";
                    if (pref.getFiletypeFlag())
                    {
                        filename = "product.csv";
                    }

                    String directoryPath = getFilesDir().getPath() + "/scans";
                    File directory = new File(directoryPath);

                    if (!directory.exists() || !directory.isDirectory()) {
                        directory.mkdirs();
                    }

                    File file = new File(directory, filename);

                    if (saveData(file, itemList_scans.toArray(new String[itemList_scans.size()])))
                    {
                        if (! file.exists())
                        {
                            Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:imageButton_submit: setOnClickListener: file no exist.");
                            return;
                        }


                        Intent intent = new Intent(getApplicationContext(), CustomMailComposerActivity.class);
                        startActivity(intent);

                    }

                }
                catch (Exception e)
                {
                    Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:imageButton_submit: setOnClickListener: e=" + e.toString());
                }
            }
        });

        ImageButton button_delete = findViewById(R.id.btnDelete);
        button_delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:imageButton_back: setOnClickListener");

                if (selectedPosition > -1)
                {
                    String[] scanArray = pref.getStringArray("scan_results");

                    String[] newArray = new String[scanArray.length - 1];
                    System.arraycopy(scanArray, 0, newArray, 0, selectedPosition);
                    System.arraycopy(scanArray, selectedPosition + 1, newArray, selectedPosition, scanArray.length - selectedPosition - 1);
                    pref.setStringArray("scan_results", newArray);

                    String[] changeArray = changeDataFormat(newArray);

                    itemList_scans = new ArrayList<>(Arrays.asList(changeArray));
                    adapter_scans.clear();
                    adapter_scans.addAll(itemList_scans);
                    adapter_scans.notifyDataSetChanged();
                    //adapter_scans.notifyAll();

                    selectedPosition = -1;
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "Please item to delete", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /*
    @Override
    protected void onRestart() {

        super.onRestart();

        String callingActivityName = getIntent().getStringExtra("callingActivity");

        if (callingActivityName == null)
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:Calling ActivityName= null");
            return;
        }

        Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:Calling Activity=" + callingActivityName);

        if (!callingActivityName.equals(ScanActivity.class.getSimpleName()))
        {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
            Log.d("scanbarcodeqrcode", "ScanResultActivity:OnCreate:Calling ActivityName is mistake.");
            return;
        }
    }
    */

    private String[] changeDataFormat(String[] arr)
    {
        if (arr == null)
            return null;

        for (int i = 0; i < arr.length; i ++)
        {
            arr[i] = Integer.toString(i + 1) + ". " + arr[i];
        }

        return arr;
    }

    public boolean sendEmailWithAttachment(String recipient, String subject, File file) {
        try {
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String dateTimeString = dateFormat.format(calendar.getTime());

            String body = "Please find attached your scans from " + subject + " Submitted on " + dateTimeString;

            // Get the FileProvider URI
            Uri fileUri = FileProvider.getUriForFile(this, "ml.guru.barcodescanner.fileprovider", file);

            // Create the email intent
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("message/rfc822");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
            intent.putExtra(Intent.EXTRA_SUBJECT, subject);
            intent.putExtra(Intent.EXTRA_TEXT, body);
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            //intent.setData(uri);

            if (intent.resolveActivity(getPackageManager()) != null) {
                pref.setStringArray("scan_results", null);

                startActivity(Intent.createChooser(intent, "Send Email"));
                finish();
                return true;
            }
            {
                Toast.makeText(this, "Fail to save csv file", Toast.LENGTH_SHORT).show();
            }


        }
        catch (Exception e)
        {
            Log.d("scanbarcodeqrcode", "ScanResultActivity:sendEmailWithAttachment: e=" + e.toString());
        }

        return false;
    }

    private void showToastLong(String message, int gravity) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.setGravity(gravity, 0, 0);
        toast.show();
    }

    private boolean saveData(File file, String[] strData)
    {
        try {

            FileWriter writer = new FileWriter(file);

            // Write rows
            for (int i = 0; i < strData.length; i ++)
            {
                writer.append(strData[i] + "\n");
            }

            writer.flush();
            writer.close();

            return true;
        } catch (IOException e) {
            Log.d("scanbarcodeqrcode", "ScanResultActivity:saveData: e=" + e.toString());
            Toast.makeText(this, "Fail to save csv file", Toast.LENGTH_SHORT).show();
        }

        return false;
    }
}