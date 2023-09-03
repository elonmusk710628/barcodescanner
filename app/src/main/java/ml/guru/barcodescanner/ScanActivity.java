package ml.guru.barcodescanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.client.android.DecodeFormatManager;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ScanActivity extends AppCompatActivity implements SurfaceHolder.Callback{
    Context context;
    ImageView imageView;
    private Spinner spinner;
    private TextView textView_barcode_result;
    private EditText editTextValue;

    Preference pref = null;

    private static final int FILE_PICKER_REQUEST_CODE = 1;
    String strScanResult = "";

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MultiFormatReader multiFormatReader;
    Camera camera;

    int nRotation;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        IntentIntegrator scan;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        context = this;

        surfaceView = findViewById(R.id.surfaceView);
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);
        nRotation = 0;

        /*
        // Check and request camera permission if not granted
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST_CODE);
        } else {
            //initializeQRCodeReader();
        }

         */

        textView_barcode_result = findViewById(R.id.textView_barcode_result);

        pref = new Preference(this);

        pref.setStringArray("scan_results", null);

        imageView = findViewById(R.id.image_logo);
        String imagePath = pref.getImageFilePath();

        if (!imagePath.isEmpty())
        {
            Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate: imagePath=" + imagePath);
            Uri fileUri = Uri.parse(imagePath);
            imageView.setImageURI(fileUri);
        }

        TextView textView_Customer = findViewById(R.id.textView_customer);
        textView_Customer.setText(pref.getCustomer());

        /*
        listView_scans.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click event
                strCustomer = (String) parent.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(), "Select: " + strCustomer, Toast.LENGTH_SHORT).show();
            }
        });
        */

        ImageButton button_back = findViewById(R.id.imageButton_back);
        button_back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:imageButton_back: setOnClickListener");

                String[] scans = pref.getStringArray("scan_results");
                //scans = 1;
                if (scans != null && scans.length > 0)
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Notice");
                    builder.setMessage("Any Codes not submitted will be deleted");
                    builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            camera.stopPreview();
                            Intent intent = new Intent(view.getContext(), MainActivity.class);
                            view.getContext().startActivity(intent);
                            pref.setStringArray("scan_results", null);
                            finish();
                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle Cancel button click
                        }
                    });

                    builder.show();
                }
                else {
                    Intent intent = new Intent(view.getContext(), MainActivity.class);
                    view.getContext().startActivity(intent);
                    finish();
                }
            }
        });

        LinearLayout linear_qty = findViewById(R.id.linear_qty);
        if (pref.getQtyFlag())
        {
            linear_qty.setVisibility(View.VISIBLE);
        }
        else
        {
            linear_qty.setVisibility(View.INVISIBLE);
        }

        editTextValue = findViewById(R.id.editTextValue);

        ImageButton btnMinus = (ImageButton) findViewById(R.id.btnMinus);
        btnMinus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnMinus: setOnClickListener");

                if (textView_barcode_result.getText().toString().equals(""))
                {
                    Toast.makeText(context, "Scan barcode", Toast.LENGTH_SHORT).show();
                    return;
                }

                int value = Integer.parseInt(String.valueOf(editTextValue.getText()));
                value -= 1;

                if (value < 0)
                    value = 0;

                editTextValue.setText(Integer.toString(value));
            }
        });

        ImageButton btnClockwise = (ImageButton) findViewById(R.id.btnClockwise);
        btnClockwise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnClockwise: setOnClickListener");
                nRotation = (nRotation - 90) % 360;
                startCameraPreview();
            }
        });

        ImageButton btnAntiClockwise = (ImageButton) findViewById(R.id.btnAntiClockwise);
        btnAntiClockwise.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnAntiClockwise: setOnClickListener");
                nRotation = (nRotation + 90) % 360;
                startCameraPreview();
            }
        });

        ImageButton button_submit = findViewById(R.id.imageButton_submit);
        button_submit.setOnClickListener(new View.OnClickListener() {
       public void onClick(View view) {
           Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:imageButton_submit: setOnClickListener");

           String[] scans = pref.getStringArray("scan_results");

           if (scans == null || scans.length == 0)
           {
               Toast.makeText(context, "No scans", Toast.LENGTH_SHORT).show();
               return;
           }

           pref.setScanResultActivityCallFlag(true);
           Intent intent = new Intent(view.getContext(), ScanResultActivity.class);
           intent.putExtra("callingActivity", ScanActivity.class.getSimpleName());
           startActivity(intent);
       }
   });

        ImageButton btnPlus = (ImageButton) findViewById(R.id.btnPlus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnPlus: setOnClickListener");

                if (textView_barcode_result.getText().toString().equals(""))
                {
                    Toast.makeText(context, "Scan barcode", Toast.LENGTH_SHORT).show();
                    return;
                }

                int value = Integer.parseInt(editTextValue.getText().toString());
                value += 1;

                editTextValue.setText(Integer.toString(value));
            }
        });

        ImageButton buttonScan_Camera = (ImageButton) findViewById(R.id.btnScan_Camera);
        buttonScan_Camera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:buttonScan_Camera: setOnClickListener");
                textView_barcode_result.setText("");

                startCameraPreview();
            }
        });

        /*
        Button buttonScan_File = (Button) findViewById(R.id.btnScan_File);
        buttonScan_File.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:buttonScan_File: setOnClickListener");

                textView_barcode_result.setText("");
                // Create the intent to open the file picker
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");  // Set the MIME type of files you want to allow

                // Start the file picker activity
                startActivityForResult(intent, FILE_PICKER_REQUEST_CODE);
            }
        });
         */

        ImageButton btnInsert = (ImageButton) findViewById(R.id.btnInsert);
        btnInsert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnInsert: setOnClickListener");
                    String str = textView_barcode_result.getText().toString();

                    if (str.isEmpty())
                    {
                        Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnInsert: setOnClickListener: textView_barcode_result empty");
                        Toast.makeText(context, "First, scan barcode", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (pref.getQtyFlag())
                    {
                        String strValue = editTextValue.getText().toString();
                        if (strValue.equals("0"))
                        {
                            Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnInsert: setOnClickListener: textView_barcode_result empty");
                            Toast.makeText(context, "select qty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        str = str + "," + editTextValue.getText().toString();
                    }

                    String[] scanArray = pref.getStringArray("scan_results");
                    List<String> stringList = null;
                    if (scanArray != null)
                    {
                        stringList = new ArrayList<>(Arrays.asList(scanArray));
                    }
                    else
                    {
                        stringList = new ArrayList<>();
                    }

                    stringList.add(str);

                    scanArray = stringList.toArray(new String[stringList.size()]);

                    pref.setStringArray("scan_results", scanArray);

                    textView_barcode_result.setText("");
                    editTextValue.setText("0");
                    startCameraPreview();
                }
                catch (Exception e)
                {
                    Log.d("scanbarcodeqrcode", "ScanActivity:OnCreate:btnInsert: setOnClickListener: e=" + e.toString());
                }
            }
        });
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("scanbarcodeqrcode", "ScanActivity:onActivityResult: start");

        if (requestCode == FILE_PICKER_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri fileUri = data.getData();
                scanImageFile(fileUri);
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        // Start the camera preview when the surface is created
        initializeQRCodeReader();
        startCameraPreview();
    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {
        // No action needed
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {
        // Release resources when the surface is destroyed
        surfaceHolder.removeCallback(this);
        if (camera != null)
        {
            camera.stopPreview();
        }
    }

    private void initializeQRCodeReader() {
        multiFormatReader = new MultiFormatReader();

        EnumMap<DecodeHintType, Object> hints = new EnumMap<>(DecodeHintType.class);
        EnumSet<BarcodeFormat> decodeFormats = EnumSet.of(BarcodeFormat.QR_CODE, BarcodeFormat.CODABAR, BarcodeFormat.CODE_39, BarcodeFormat.CODE_93, BarcodeFormat.CODE_128, BarcodeFormat.EAN_8, BarcodeFormat.EAN_13, BarcodeFormat.ITF, BarcodeFormat.UPC_A, BarcodeFormat.UPC_E);
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.TRY_HARDER, true);
        hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");
        multiFormatReader.setHints(hints);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (surfaceHolder.getSurface().isValid()) {
            surfaceHolder.getSurface().release();
            camera.release();
            camera = null;
            // Stop any ongoing rendering or video playback
            // Release any resources associated with the SurfaceView
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (surfaceHolder.getSurface().isValid()) {
            surfaceHolder.getSurface().release();
            camera.release();
            camera = null;
            // Other cleanup tasks for the SurfaceView
        }
    }

    private void startCameraPreview() {
        try {
            if (camera != null)
            {
                camera.release();
                camera = null;
            }

            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
            camera.setParameters(parameters);

            /*
            Display display = getWindowManager().getDefaultDisplay();
            Point screenSize = new Point();
            display.getSize(screenSize);
            int screenWidth = screenSize.x;
            int screenHeight = screenSize.y;

            // Adjust the SurfaceView size to match the screen size
            ViewGroup.LayoutParams layoutParams = surfaceView.getLayoutParams();
            layoutParams.width = screenWidth;
            layoutParams.height = screenHeight;
            surfaceView.setLayoutParams(layoutParams);
            */

            camera.setPreviewDisplay(surfaceHolder);
            camera.setPreviewCallback(new Camera.PreviewCallback() {
                public void onPreviewFrame(byte[] data, Camera camera) {
                    PlanarYUVLuminanceSource source = CameraConfigurationManager.buildLuminanceSource(data, camera, context, nRotation);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

                    try {
                        Result result = multiFormatReader.decodeWithState(bitmap);
                        if (result != null) {
                            // QR code successfully scanned, handle the result
                            strScanResult = result.getText();
                            if (strScanResult.length() < 9)
                            {
                                Log.d("scanbarcodeqrcode", "ScanActivity:startCameraPreview:onPreviewFrame:   strScanResult is short:" + strScanResult);
                                strScanResult = "";
                                return;
                            }

                            textView_barcode_result.setText(strScanResult);
                            camera.stopPreview();
                            Toast.makeText(getApplicationContext(), strScanResult, Toast.LENGTH_SHORT).show();
                        }
                    } catch (NotFoundException e) {
                        // QR code not found in the current frame
                    } finally {
                        multiFormatReader.reset();
                    }
                }
            });

            camera.startPreview();
        } catch (IOException e) {
            Log.e("scanbarcodeqrcode", "Failed to start camera preview: " + e.getMessage());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        // Your custom logic here
        startCameraPreview();
    }

    public void rotation() {
        Log.d("scanbarcodeqrcode", "ScanActivity:surfaceChanged: start");
        if (surfaceHolder.getSurface() == null) {
            Log.d("scanbarcodeqrcode", "ScanActivity:surfaceChanged: surfaceHolder.getSurface() = null");
            return;
        }

        try {
            camera.stopPreview();
        } catch (Exception e) {
            Log.d("scanbarcodeqrcode", "ScanActivity:surfaceChanged: camera.stopPreview e =" + e.toString());
            e.printStackTrace();
        }

        // Configure camera parameters
        Camera.Parameters parameters = camera.getParameters();
        //configureCameraParameters(parameters);

        // Set the display orientation
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = ((Activity) surfaceView.getContext()).getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        degrees += nRotation;

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate for the mirror effect
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);

        try {
            camera.startPreview();
        } catch (Exception e) {
            Log.d("scanbarcodeqrcode", "ScanActivity:surfaceChanged: camera.startPreview e =" + e.toString());
            e.printStackTrace();
        }
    }

    // Method to scan an image file
    private void scanImageFile(Uri imageUri) {
        RGBLuminanceSource source = null;

        if (imageUri.toString().indexOf("jpg") != -1) {
            try {
                FileInputStream inputStream = (FileInputStream) this.getContentResolver().openInputStream(imageUri);

                // Get the file descriptor from the input stream
                FileDescriptor fileDescriptor = inputStream.getFD();

                // Decode the file descriptor into a Bitmap
                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor);

                // Convert the JPEG Bitmap to a BinaryBitmap
                source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), getYUVLuminanceArray(bitmap));
            } catch (Exception e) {
                Log.d("scanbarcodeqrcode", "ScanActivity:scanImageFile: error 1: " + e.toString());
                Toast.makeText(this, "Scanning error", Toast.LENGTH_SHORT).show();
            }
        } else {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));

                int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
                bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
                source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), pixels);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.d("scanbarcodeqrcode", "ScanActivity:scanImageFile: Image not found");
                Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show();
            }
        }

        if (source != null)
        {
            BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

            MultiFormatReader reader = new MultiFormatReader();
            try {
                com.google.zxing.Result result = reader.decode(binaryBitmap);
                strScanResult = result.getText();
                Log.d("scanbarcodeqrcode", "ScanActivity:scanImageFile: Scanned data: " + strScanResult);
                Toast.makeText(this, "Scanned data: " + strScanResult, Toast.LENGTH_SHORT).show();
                textView_barcode_result.setText(strScanResult);

            } catch (ReaderException e) {
                e.printStackTrace();
                Log.d("scanbarcodeqrcode", "ScanActivity:scanImageFile: Scanning failed");
                Toast.makeText(this, "Scanning failed", Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            Log.d("scanbarcodeqrcode", "ScanActivity:scanImageFile: no source");
            Toast.makeText(this, "no source", Toast.LENGTH_SHORT).show();
        }
    }

    private static int[] getYUVLuminanceArray(Bitmap bitmap) {
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        int[] luminanceArray = new int[bitmap.getWidth() * bitmap.getHeight()];

        for (int i = 0; i < pixels.length; i++) {
            int pixel = pixels[i];
            int red = (pixel >> 16) & 0xff;
            int green = (pixel >> 8) & 0xff;
            int blue = pixel & 0xff;

            // Calculate luminance using formula: Y = 0.299R + 0.587G + 0.114B
            byte luminance = (byte) ((0.299 * red + 0.587 * green + 0.114 * blue) + 0.5);

            luminanceArray[i] = luminance;
        }

        return luminanceArray;
    }

}