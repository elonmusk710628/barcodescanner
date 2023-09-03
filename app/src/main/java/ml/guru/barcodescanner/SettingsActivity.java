package ml.guru.barcodescanner;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;


public class SettingsActivity extends AppCompatActivity {
    ImageView imageView;
    private Spinner spinner;

    Switch switchWidget_qty;
    Switch switchWidget_filetype;
    EditText editText_email;
    CustomArrayAdapter<String> adapter;

    private static final int FILE_PICKER_REQUEST_CODE = 1;

    Preference pref = null;

    public SettingsActivity() {
    }

    private String[] arrayAdapterToArray(ArrayAdapter<String> arrayAdapter)
    {
        String[] stringArray = new String[arrayAdapter.getCount()];
        for (int i = 0; i < arrayAdapter.getCount(); i++) {
            stringArray[i] = arrayAdapter.getItem(i);
            //stringArray[i] = arrayAdapter.getItem(arrayAdapter.getCount() - i -1);
            Log.d("scanbarcodeqrcode", "SettingActivity:arrayAdapterToArray: " + Integer.toString(i) + " " + arrayAdapter.getItem(i));

        }

        return stringArray;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        pref = new Preference(this);

        imageView = findViewById(R.id.image_logo);
        String imagePath = pref.getImageFilePath();
        if (!imagePath.isEmpty())
        {
            Uri fileUri = Uri.parse(imagePath);
            imageView.setImageURI(fileUri);
        }

        spinner = findViewById(R.id.spinner1);
        ImageButton addButton = findViewById(R.id.add_button);
        ImageButton deleteButton = findViewById(R.id.delete_button);

        //String[] customs = pref.getCustomers();
        String[] customs = pref.getStringArray("customers");
        // Create an empty ArrayAdapter

        ArrayList<String> itemList = new ArrayList<>();

        adapter = new CustomArrayAdapter<>(this, R.layout.custom_spinner_item, itemList, Color.BLUE);

        if (customs != null)
        {
            //adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, customs);

            adapter.addAll(customs);
        }

        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.custom_spinner_item);

        spinner.setAdapter(adapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addItemToSpinner();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItem();
            }
        });

        switchWidget_qty = findViewById(R.id.switch_qty);
        switchWidget_qty.setChecked(pref.getQtyFlag());

        // Set an OnCheckedChangeListener to listen for switch state changes
        switchWidget_qty.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pref.setQtyFlag(isChecked);

                // Handle switch state change
                if (isChecked) {
                    // Switch is in the "on" state
                    // Perform necessary actions

                } else {
                    // Switch is in the "off" state
                    // Perform necessary actions
                }
            }
        });

        editText_email = findViewById(R.id.edittext_email);
        editText_email.setText(pref.getEmailAddress());

        editText_email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Called before the text is changed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Called when the text is being changed
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Called after the text has been changed
                String newText = s.toString();
                // Perform any desired actions with the new text
                pref.setEmailAddress(newText);
            }
        });

        switchWidget_filetype = findViewById(R.id.switch_filetype);
        switchWidget_filetype.setChecked(pref.getFiletypeFlag());

        // Set an OnCheckedChangeListener to listen for switch state changes
        switchWidget_filetype.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                pref.setFiletypeFlag(isChecked);

                // Handle switch state change
                if (isChecked) {
                    // Switch is in the "on" state
                    // Perform necessary actions

                } else {
                    // Switch is in the "off" state
                    // Perform necessary actions
                }
            }
        });

        Button button = findViewById(R.id.button_upload);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "OnCreate:button_upload: setOnClickListener");

                // Create the intent to open the file picker
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*"); // Set the MIME type of files you want to allow

                // Start the file picker activity
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
            }
        });

        ImageButton button_back = findViewById(R.id.imageButton_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "OnCreate:imageButton_back: setOnClickListener");
                finish();
            }
        });

        ImageButton button_submit = findViewById(R.id.imageButton_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "OnCreate:imageButton_submit: setOnClickListener");

                if (pref.getEmailAddress().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Set email address", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (pref.getStringArray("customers") == null || pref.getStringArray("customers").length == 0)
                {
                    Toast.makeText(getApplicationContext(), "Add customer", Toast.LENGTH_SHORT).show();
                    return;
                }

                /*
                Intent intent = new Intent(view.getContext(), ScanActivity.class);
                view.getContext().startActivity(intent);
                 */
                finish();
            }
        });
    }

    private void addItemToSpinner() {
        EditText newItemEditText = new EditText(this);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Enter Customer Name and Press ADD button.")
                .setView(newItemEditText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newItem = newItemEditText.getText().toString().trim();
                        if (!newItem.isEmpty()) {
                            adapter.add(newItem);
                            adapter.notifyDataSetChanged();
                            pref.setStringArray("customers", arrayAdapterToArray(adapter));
                        }
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
        //newItemEditText.requestFocus();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                imageView.setImageURI(fileUri);

                pref.setImageFilePath(fileUri.toString());
            }
        }
    }

    private void deleteSelectedItem() {
        int selectedPosition = spinner.getSelectedItemPosition();
        if (selectedPosition != AdapterView.INVALID_POSITION) {
            adapter.remove(adapter.getItem(selectedPosition));
            adapter.notifyDataSetChanged();

            pref.setStringArray("customers", arrayAdapterToArray(adapter));
            //pref.setCustomers(arrayAdapterToArray(adapter));
        }
    }
}