package ml.guru.barcodescanner;
// Make sure to run this code on a background thread (not on the main/UI thread)

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailSender {
    public static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    public static EmailSender getInstance() {
        return InstanceHolder.instance;
    }

    private EmailSender() {
    }

    private static class InstanceHolder {
        private static EmailSender instance = new EmailSender();
    }

    class Message {
        private String to = "";
        private String subject = "";
        private String content = "";
        private String file = "";
        private String filename = "";

        public Message(String to, String subject, String content, String file, String filename) {
            this.to = to;
            this.subject = subject;
            this.content = content;
            this.file = file;
            this.filename = filename;
        }
    }

    class MailTask extends AsyncTask<Void, Void, Boolean> {

        Message message;
        public MailTask(Message message) {
            this.message = message;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.d("scanbarcodeqrcode", "EmailSender: doInBackground: start");
            OkHttpClient client = new OkHttpClient();

            try {
                // Replace YOUR_SERVER_URL with the actual URL of your server API
                //String url = "http://192.168.8.188/index.php?action=send";
                String url = "https://test.factorgpu.com/app/smtp.php?action=send";

                // Create JSON request body
                JSONObject json = new JSONObject();
                json.put("to", message.to);
                json.put("subject", message.subject);
                json.put("content", message.content);
                json.put("file", message.file);
                json.put("filename", message.filename);
                String requestBody = json.toString();

                RequestBody body = RequestBody.create(requestBody, JSON);
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();

                Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    Log.d("scanbarcodeqrcode", "EmailSender: doInBackground: ok " + responseBody);
                    return Boolean.TRUE;
                    // Handle the server response here
                    // You can show a success message to the user, etc.
                } else {
                    // Handle error response here
                    Log.d("scanbarcodeqrcode", "EmailSender: doInBackground: error");
                }
            } catch (Exception e) {
                Log.d("scanbarcodeqrcode", "EmailSender: doInBackground: error=" + e.toString());
                e.printStackTrace();
            }
            return Boolean.FALSE;
        }
    }

    public void sendEmail(String to, String subject, String content, String file, String filename) {
        Log.d("scanbarcodeqrcode", "EmailSender: sendEmail: start");
        Message message = new Message(to, subject, content, file, filename);
        EmailSender.MailTask mailTask = new EmailSender.MailTask(message);
        mailTask.execute();
    }

}
