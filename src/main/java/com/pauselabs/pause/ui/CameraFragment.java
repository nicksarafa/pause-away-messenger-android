package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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

    @Inject protected Bus mBus;
    @InjectView(R.id.camera_preview) FrameLayout previewLayout;
    @InjectView(R.id.button_capture) ImageButton mCaptureBtn;
    @InjectView(R.id.existingPictureBtn) ImageButton mExistingPicturBtn;
    @InjectView(R.id.backBtn) ImageButton mBackBtn;

    private Camera mCamera;
    private CameraPreview mPreview;
    private PauseBounceBackMessage mCurrentPauseBouncebackMessage;

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
        previewLayout.addView(mPreview);

        mCaptureBtn.setOnClickListener(this);
        mExistingPicturBtn.setOnClickListener(this);
        mBackBtn.setOnClickListener(this);

        return view;
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
            mCamera.startPreview();
        }
        else{
            mCamera = Camera.open();

            mPreview = new CameraPreview(getActivity(), mCamera);
            previewLayout.addView(mPreview);
            mCamera.startPreview();

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

    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
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
                        final BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap tempBitmap = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                        //Bitmap mutableBitmap = tempBitmap.copy(Bitmap.Config.ARGB_8888, true);

                        Bitmap rotatedBitmap = rotateBitmap(tempBitmap, 90);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        if (pictureFile == null) {
                            Log.d(TAG, "Error creating media file, check storage permissions: ");
                            return;
                        }

                        rotatedBitmap.recycle();

                        try {
                            FileOutputStream fos = new FileOutputStream(pictureFile);
                            fos.write(byteArray);
                            fos.close();

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
            case R.id.existingPictureBtn:
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
                break;
            case R.id.backBtn:
                getActivity().finish();
                break;
        }
    }

    private void showPreviewScreen() {
        Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
        previewIntent.putExtra(Constants.Message.MESSAGE_PARCEL, (android.os.Parcelable) mCurrentPauseBouncebackMessage);
        startActivity(previewIntent);
    }

    private static Bitmap rotateBitmap(Bitmap source, float angle) {
        // Out of memory error on Samsung Galaxy S4
        Matrix matrix = new Matrix();
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
