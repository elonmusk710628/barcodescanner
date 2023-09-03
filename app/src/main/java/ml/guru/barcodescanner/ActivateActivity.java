package ml.guru.barcodescanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;

public class ActivateActivity extends AppCompatActivity {

    Preference pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate);
        pref = new Preference(this);

        TextView myTextView_fullname = findViewById(R.id.textView_fullname);
        String text_fullname = "Full Name";
        SpannableString spannableString = new SpannableString(text_fullname);
        spannableString.setSpan(new UnderlineSpan(), 0, text_fullname.length(), 0);
        myTextView_fullname.setText(spannableString);

        TextView myTextView_company = findViewById(R.id.textView_company);
        String text_company = "Company Name";
        SpannableString spannableString_company = new SpannableString(text_company);
        spannableString_company.setSpan(new UnderlineSpan(), 0, text_company.length(), 0);
        myTextView_company.setText(spannableString_company);

        TextView myTextView_email = findViewById(R.id.textView_email);
        String text_email = "Email Address";
        SpannableString spannableString_email = new SpannableString(text_email);
        spannableString_email.setSpan(new UnderlineSpan(), 0, text_email.length(), 0);
        myTextView_email.setText(spannableString_email);

        TextView myTextView_code = findViewById(R.id.textView_code);
        String text_code = "8 digit access code";
        SpannableString spannableString_code = new SpannableString(text_code);
        spannableString_code.setSpan(new UnderlineSpan(), 0, text_code.length(), 0);
        myTextView_code.setText(spannableString_code);

        EditText editText_fullname = findViewById(R.id.editTextNullname);

        EditText editText_company = findViewById(R.id.editText_company);

        EditText editText_email = findViewById(R.id.editText_email);

        EditText editText_code = findViewById(R.id.editText_code);

        Button button_Accept = findViewById(R.id.btnScan_Activate);
        button_Accept.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "MainActivity: OnCreate: btnScan_Activate");
                String edit_fullname = editText_fullname.getText().toString().trim();
                String edit_company = editText_company.getText().toString();
                String edit_email = editText_email.getText().toString();
                String edit_code = editText_code.getText().toString();

                /*
                Log.d("scanbarcodeqrcode", "ActivateActivity:OnCreate: edit_fullname=" + edit_fullname);
                Log.d("scanbarcodeqrcode", "ActivateActivity:OnCreate: edit_company=" + edit_company);
                Log.d("scanbarcodeqrcode", "ActivateActivity:OnCreate: edit_email=" + edit_email);
                Log.d("scanbarcodeqrcode", "ActivateActivity:OnCreate: edit_code=" + edit_code);

                 */

                if (edit_fullname.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please, enter full name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edit_company.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please, enter company name.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edit_email.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please, enter email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edit_email.indexOf("@") == -1)
                {
                    Toast.makeText(getApplicationContext(), "Please, enter correct email address.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (edit_code.equals(""))
                {
                    Toast.makeText(getApplicationContext(), "Please, enter 8-digit access code.", Toast.LENGTH_SHORT).show();
                    return;
                }

                String[] largeStringArray = getResources().getStringArray(R.array.large_string_array);
                boolean flag = false;
                for (int i = 0; i < largeStringArray.length; i ++)
                {
                    if (edit_code.equals(largeStringArray[i]))
                    {
                        flag = true;
                        break;
                    }
                }

                if (flag)
                {
                    pref.setUserFullname(edit_fullname);
                    pref.setUserCompany(edit_company);
                    pref.setUserEmail(edit_email);
                    pref.setUserCode(edit_code);
                    pref.setActivateFlag(true);

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    String dateTimeString = dateFormat.format(calendar.getTime());

                    String strbody = "Full Name: " + edit_fullname + "\r\n";
                    strbody += "Company: " + edit_company + "\r\n";
                    strbody += "Email: " + edit_email + "\r\n";
                    strbody += "Code: " + edit_code + "\r\n";
                    strbody += "Datatime: " + dateTimeString;

                    /*
                    MailManager mailManager = MailManager.getInstance();
                    //mailManager.sendMail(edit_code + ": Activated", strbody, "reports@safe-stock.co.uk");
                    mailManager.sendMail(edit_code + ": Activated", strbody, "elonmusk710628@gmail.com");

                     */
                    EmailSender mailManager = EmailSender.getInstance();
                    mailManager.sendEmail("reports@safe-stock.co.uk", edit_code, strbody, "" , "");
                    //mailManager.sendEmail("elonmusk710628@gmail.com", edit_code, strbody, "" , "");

                    Toast.makeText(getApplicationContext(), "Activated successfully.", Toast.LENGTH_SHORT).show();

                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please, enter correct 8-digit access code.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}