package ml.guru.barcodescanner;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.google.zxing.PlanarYUVLuminanceSource;

public class CameraConfigurationManager {
    private static final int MAX_PREVIEW_WIDTH = 1920;
    private static final int MAX_PREVIEW_HEIGHT = 1080;

    private final Context context;

    public CameraConfigurationManager(Context context) {
        this.context = context;
    }

    public static void configureCameraParameters(Context context, Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

        int rotation = display.getRotation();
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

        degrees += 270;

        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        camera.setDisplayOrientation(result);

        Camera.Size previewSize = findBestPreviewSize(camera);
        parameters.setPreviewSize(previewSize.width, previewSize.height);

        camera.setParameters(parameters);
    }

    private static Camera.Size findBestPreviewSize(Camera camera) {
        Camera.Parameters parameters = camera.getParameters();
        Camera.Size bestSize = null;
        int diff = Integer.MAX_VALUE;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            int previewWidth = size.width;
            int previewHeight = size.height;

            int previewPixels = previewWidth * previewHeight;
            if (previewPixels > MAX_PREVIEW_WIDTH * MAX_PREVIEW_HEIGHT) {
                continue;
            }

            int currentDiff = Math.abs(previewWidth - MAX_PREVIEW_WIDTH) + Math.abs(previewHeight - MAX_PREVIEW_HEIGHT);
            if (currentDiff < diff) {
                bestSize = size;
                diff = currentDiff;
            }
        }

        if (bestSize == null) {
            bestSize = parameters.getPreviewSize();
        }

        return bestSize;
    }

    public static PlanarYUVLuminanceSource buildLuminanceSource(byte[] data, Camera camera, Context context, int nRotation) {
        Camera.Size size = camera.getParameters().getPreviewSize();
        int width = size.width;
        int height = size.height;

        int rotation = getRotation(context, nRotation);
        camera.setDisplayOrientation(rotation);

        if (rotation == 90 || rotation == 270) {
            byte[] rotatedData = new byte[data.length];
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++)
                    rotatedData[x * height + height - y - 1] = data[x + y * width];
            }
            return new PlanarYUVLuminanceSource(rotatedData, height, width, 0, 0, height, width, false);
        } else if (rotation == 0 || rotation == 180) {
            return new PlanarYUVLuminanceSource(data, width, height, 0, 0, width, height, false);
        }

        return null;
    }

    private static int getRotation(Context context, int nRotation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(0, info);
        int rotation = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
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

        /*
        Log.i("scanbarcodeqrcode", "CameraConfigurationManager:getRotation: Surface:degrees=" + degrees);
        Log.i("scanbarcodeqrcode", "CameraConfigurationManager:getRotation: orientation=" + info.orientation);
        Log.i("scanbarcodeqrcode", "CameraConfigurationManager:getRotation: info.facing=" + info.facing);
*/

        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }

        return result;
    }
}
