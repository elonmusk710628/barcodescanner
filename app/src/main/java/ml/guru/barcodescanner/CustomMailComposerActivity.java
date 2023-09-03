package ml.guru.barcodescanner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import com.google.android.material.textfield.TextInputEditText;

public class CustomMailComposerActivity extends AppCompatActivity {

    Preference pref = null;
    String strBody = "";
    String filename = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_mail_composer);

        pref = new Preference(this);

        filename = "product.txt";
        if (pref.getFiletypeFlag())
        {
            filename = "product.csv";
        }

        // Assuming you have already inflated your layout and obtained the reference to the TextInputEditText
        TextInputEditText editTextRecipient = findViewById(R.id.editTextRecipient);
        // Set the desired text
        editTextRecipient.setText(pref.getEmailAddress());

        // Assuming you have already inflated your layout and obtained the reference to the TextInputEditText
        TextInputEditText editTextSubject = findViewById(R.id.editTextSubject);
        // Set the desired text
        editTextSubject.setText(pref.getCustomer());

        TextView textView_attatch_name = findViewById(R.id.textView_attatch_name);
        textView_attatch_name.setText(filename);

        ImageButton button_submit = findViewById(R.id.send);
        button_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    String filePath = getFilesDir().getPath() + "/scans/" + filename;

                    File file = new File(getFilesDir().getPath() + "/scans/", filename);
                    if (!file.exists())
                    {
                        Toast.makeText(getApplicationContext(), "no exist product file.", Toast.LENGTH_SHORT).show();
                        return;
                    }


                    String strFileContent = "";
                    //strFileContent = readFromFile(getApplicationContext(), filePath);
                    strFileContent = readFileFromExternalStorage(filename);

                    long scans = pref.getScans();
                    scans += pref.getStringArray("scan_results").length;
                    pref.setScans(scans);

                    EmailSender mailManager = EmailSender.getInstance();
                    mailManager.sendEmail(pref.getEmailAddress(), pref.getUserCode() + ": Scans", strBody, strFileContent, filename);

                    /*
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder.setTitle("Notice");
                    builder.setMessage("Sent email successfully.");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                    builder.show();
                     */

                    Toast.makeText(getApplicationContext(), "Sent email successfully.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                catch (Exception e)
                {
                    Log.d("scanbarcodeqrcode", "CustomMailComposerActivity:OnCreate:send: setOnClickListener: e=" + e.toString());
                }
            }
        });


        ImageButton button_back = findViewById(R.id.back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "CustomMailComposerActivity:OnCreate:back: setOnClickListener");
                finish();
            }
        });
    }
    private String readFromFile(Context context, String file) {

        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();
            }
        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }
    public String readFileFromExternalStorage(String fileName) {
        // Get the external storage directory

        // Create a file object with the given file name
        File file = new File(getFilesDir().getPath() + "/scans/", fileName);

        StringBuilder data = new StringBuilder();

        try {
            // Create a BufferedReader to read from the file
            BufferedReader reader = new BufferedReader(new FileReader(file));

            // Read data from the file
            String line;
            while ((line = reader.readLine()) != null) {
                data.append(line).append("\n");
            }

            // Close the BufferedReader
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle file read error
        }

        return data.toString();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Your code here

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String dateTimeString = dateFormat.format(calendar.getTime());

        strBody = "Please find attached your scans from " + pref.getCustomer() + " Submitted on " + dateTimeString;

        // Assuming you have already inflated your layout and obtained the reference to the TextInputEditText
        TextInputEditText messageInputLayout = findViewById(R.id.editTextMessage);
        // Set the desired text
        messageInputLayout.setText(strBody);
    }

}