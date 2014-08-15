package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.core.CameraPreview;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This fragment is responsible for displaying the camera view
 */
public class CameraFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = CameraFragment.class.getSimpleName();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    private static int RESULT_LOAD_IMAGE = 1;

    private int mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK; // default to rear facing camera

    @Inject protected Bus mBus;
    @InjectView(R.id.camera_preview) FrameLayout previewLayout;
    @InjectView(R.id.button_capture) ImageButton mCaptureBtn;
    @InjectView(R.id.switchCameraBtn) ImageButton mSwitchCameraBtn;
    @InjectView(R.id.backBtn) ImageButton mBackBtn;

    private Camera mCamera;
    private CameraPreview mPreview;
    private SurfaceHolder mHolder;
    private PauseBounceBackMessage mCurrentPauseBouncebackMessage;
    private boolean inPreview = false;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.camera_fragment, null);

        // Inject Butterknife views
        Views.inject(this, view);

        // Retrieve active bounce back message
        Intent intent = getActivity().getIntent();
        mCurrentPauseBouncebackMessage = intent.getParcelableExtra(Constants.Message.MESSAGE_PARCEL);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(getActivity(), mCamera);
        mHolder = mPreview.getHolder();
        previewLayout.addView(mPreview);

        init();

        return view;
    }

    public void init() {
        // Only display switch camera button if device supports multiple cameras
        if(Camera.getNumberOfCameras() == 1){
            mSwitchCameraBtn.setVisibility(View.INVISIBLE);
        }
        else {
            mSwitchCameraBtn.setOnClickListener(this);
        }

        mCaptureBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);

        if(mCamera != null) {
            startPreview();
        }
        else{
            mCamera = Camera.open(mCurrentCameraId);

            mPreview = new CameraPreview(getActivity(), mCamera);
            previewLayout.addView(mPreview);
            startPreview();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
        releaseCamera();
    }

    @Override
    public void onDestroy(){
        super.onPause();
        mBus.unregister(this);
        releaseCamera();
    }


    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        Injector.inject(this);
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
            Log.e(TAG, e.getMessage().toString());
        }
        return c; // returns null if camera is unavailable
    }

    private void startPreview() {
        if(mCamera != null){
            mCamera.startPreview();
            inPreview = true;
        }
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.release();        // release the camera for other applications
            mCamera = null;
            mPreview.getHolder().removeCallback(mPreview);
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_capture:
                mCamera.takePicture(null, null, new Camera.PictureCallback() {

                    @Override
                    public void onPictureTaken(byte[] data, Camera camera) {

                        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                        if (pictureFile == null) {
                            Log.d(TAG, "Error creating media file, check storage permissions: ");
                            return;
                        }

                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);

                        Bitmap adjustedBitmap;
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();

                        if(mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
                            adjustedBitmap = rotateBitmap(tempBitmap, 90, false);
                        }
                        else{
                            adjustedBitmap = rotateBitmap(tempBitmap, 90, true);
                        }

                        adjustedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        adjustedBitmap.recycle();

                        try {
                            // write byte array to file
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(byteArray);
                            fos.close();

                            // notify gallery that a new picture has been added
                            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));

                            // add file path to active bounce back message
                            mCurrentPauseBouncebackMessage.setPathToOriginal(pictureFile.getPath());
                            showPreviewScreen();
                        } catch (FileNotFoundException e) {
                            Log.d(TAG, "File not found: " + e.getMessage());
                        } catch (IOException e) {
                            Log.d(TAG, "Error accessing file: " + e.getMessage());
                        }
                    }

                });
                break;
            case R.id.switchCameraBtn:
                switchCamera();
                break;
            case R.id.backBtn:
                getActivity().finish();
                break;
        }
    }

    /**
     * This function toggles the front and back facing camera.  In order to change cameras we need to first stop current
     * preview, release camera, and rebuild preview with the other camera
     */
    public void switchCamera() {
        if(inPreview){
            mCamera.stopPreview();
            inPreview = false;
        }

        mCamera.release();

        //swap the id of the camera to be used
        if(mCurrentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
            mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        }
        else {
            mCurrentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        mCamera = Camera.open(mCurrentCameraId);
        mPreview = new CameraPreview(getActivity(), mCamera);
        // clear previous camera view
        previewLayout.removeAllViews();
        previewLayout.addView(mPreview);
        startPreview();
    }

    private void showPreviewScreen() {
        Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
        previewIntent.putExtra(Constants.Message.MESSAGE_PARCEL, (android.os.Parcelable) mCurrentPauseBouncebackMessage);
        startActivity(previewIntent);
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle, boolean isFrontCamera) {
        Matrix matrix = new Matrix();

        // Front facing camera image will be mirrored, we need to apply an additional transformation
        if(isFrontCamera){
            float[] mirrorY = { -1, 0, 0, 0, 1, 0, 0, 0, 1};
            Matrix matrixMirrorY = new Matrix();
            matrixMirrorY.setValues(mirrorY);
            matrix.postConcat(matrixMirrorY);
        }

        matrix.postRotate(angle);
        Bitmap result = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
        // rotating done, original not needed => recycle()
        source.recycle();
        return result;
    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Pause");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("Pause", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK
                && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            if (!picturePath.equals("")) {
                mCurrentPauseBouncebackMessage.setPathToOriginal(picturePath);
                // go to preview
                showPreviewScreen();
            }
        }

    }
}
