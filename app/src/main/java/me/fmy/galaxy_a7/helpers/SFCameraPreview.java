package me.fmy.galaxy_a7.helpers;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.io.IOException;

/**
 * Created by Femmy on 7/28/2016.
 */
public class SFCameraPreview extends SurfaceView implements SurfaceHolder.Callback{

    private final String TAG = "GALAXY_A7_SFCameraPreview";
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private boolean isPreviewRunning = false;
    Context mContext;

    private static final int h = 1920;
    private static final int w = 1080;

    public SFCameraPreview(Context context, Camera camera) {

        super(context);
        mContext = context;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // create the surface and start camera preview
            if (mCamera == null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        setCamera(camera);
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        //refreshCamera(mCamera);

        if (isPreviewRunning){
            mCamera.stopPreview();
        }

        Camera.Parameters parameters = mCamera.getParameters();
        Display display = ((WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();

        if(display.getRotation() == Surface.ROTATION_0){
            parameters.setPreviewSize(h, w);
            mCamera.setDisplayOrientation(90);
        }

        if(display.getRotation() == Surface.ROTATION_90){
            parameters.setPreviewSize(w, h);
        }

        if(display.getRotation() == Surface.ROTATION_180){
            parameters.setPreviewSize(h, w);
        }

        if(display.getRotation() == Surface.ROTATION_270){
            parameters.setPreviewSize(w, h);
            mCamera.setDisplayOrientation(180);
        }

        mCamera.setParameters(parameters);
        previewCamera();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // mCamera.release();
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
    }

    public void previewCamera(){
        try{
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            isPreviewRunning = true;
        }
        catch(Exception e)
        {
            Log.d(TAG, "Cannot start preview", e);
        }
    }
}
