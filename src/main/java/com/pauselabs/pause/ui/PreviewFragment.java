package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.squareup.otto.Bus;

import javax.inject.Inject;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Preview Fragment is responsible for displaying the actual image preview before the pause session is initiated
 */
public class PreviewFragment extends Fragment implements View.OnClickListener{

    private static final String TAG = PreviewFragment.class.getSimpleName();
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private PauseBounceBackMessage mCurrentPauseBouncebackMessage;
    private SavedPauseDataSource datasource;

    @Inject protected Bus mBus;
    @Inject protected SharedPreferences mPrefs;

    @InjectView(R.id.previewImage) RelativeLayout mPreviewLayout;
    @InjectView(R.id.pauseImage) ImageView mPauseImage;
    @InjectView(R.id.pauseMessageText) TextView mPauseMessageText;
    @InjectView(R.id.locationContainer) RelativeLayout mLocationSectionContainer;
    @InjectView(R.id.durationContainer) RelativeLayout mDurationSectionContainer;
    @InjectView(R.id.pauseLocationText) TextView mPauseLocationText;
    @InjectView(R.id.pauseDurationText) TextView mPauseDurationText;
    @InjectView(R.id.acceptBtn) ImageButton mAcceptBtn;
    @InjectView(R.id.cancelBtn) ImageButton mCancelBtn;

    private int mWidth = 0;
    private int mHeight = 0;

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.preview_camera_fragment, null);

        // Inject Butterknife views
        Views.inject(this, view);

        // Retrieve active bounce back message
        Intent intent = getActivity().getIntent();
        mCurrentPauseBouncebackMessage = intent.getParcelableExtra(Constants.Message.MESSAGE_PARCEL);

        init();

        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        datasource = new SavedPauseDataSource(getActivity());
        datasource.open();
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        Injector.inject(this);
    }

    public void init(){
        // load drawable from image path
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap pauseBitmap = BitmapFactory.decodeFile(mCurrentPauseBouncebackMessage.getPathToOriginal(), options);
        BitmapDrawable pauseDrawable = new BitmapDrawable(getResources(), pauseBitmap);
        mPauseImage.setImageDrawable(pauseDrawable);

        mPauseMessageText.setText(mCurrentPauseBouncebackMessage.getMessage());

        // if location set, show location field
        if(mCurrentPauseBouncebackMessage.getLocation() != null &&
                !mCurrentPauseBouncebackMessage.getLocation().equals("")){
            mPauseLocationText.setText(mCurrentPauseBouncebackMessage.getLocation());
            mLocationSectionContainer.setVisibility(View.VISIBLE);
        }

        // if duration set, show duration field
        // TODO

        mAcceptBtn.setOnClickListener(this);
        mCancelBtn.setOnClickListener(this);

        mWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth();
        mHeight = getActivity().getWindowManager().getDefaultDisplay().getHeight();

    }

    @Override
    public void onResume() {
        datasource.open();
        super.onResume();
    }

    @Override
    public void onPause() {
        datasource.close();
        super.onPause();
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.acceptBtn:
                previewImageAccepted();
                break;
            case R.id.cancelBtn:
                // go back to create screen?
                getActivity().finish();
                break;
        }
    }

    private void previewImageAccepted() {
        // Save image to external storage
        String pauseImagePath = saveBounceBackImageToStorage();
        if(!pauseImagePath.equals("")){
            mCurrentPauseBouncebackMessage.setPathToImage(pauseImagePath);
        }

        // if previously saved, don't resave
        if(mCurrentPauseBouncebackMessage.getId() == -1) {   // id of -1 is a new unsaved bounce back
            // save BounceBack to database
            mCurrentPauseBouncebackMessage =  datasource.createSavedPause(mCurrentPauseBouncebackMessage);
        }

        // set active Pause database ID in sharedPrefs
        mPrefs.edit().putLong(Constants.Pause.ACTIVE_PAUSE_DATABASE_ID_PREFS, mCurrentPauseBouncebackMessage.getId()).commit();

        // start Pause Session
        //mBus.post(new PauseSessionChangedEvent(Constants.Pause.PAUSE_SESSION_STATE_ACTIVE));

        PauseApplication.startPauseService();

        // show scoreboard activity
        Intent scoreboardIntent = new Intent(getActivity(), ScoreboardActivity.class);
        startActivity(scoreboardIntent);
    }

    private String saveBounceBackImageToStorage(){
        String pathToImage = "";
        File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
        Bitmap b = getScreenViewBitmap(mPreviewLayout);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            return "";
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            fos.write(byteArray);
            fos.close();

            // notify gallery to update
            getActivity().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(pictureFile)));

            pathToImage = pictureFile.getPath();

        } catch (FileNotFoundException e) {
            Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            Log.d(TAG, "Error accessing file: " + e.getMessage());
        }

        return pathToImage;
    }

    private Bitmap getScreenViewBitmap(View v) {

        mAcceptBtn.setVisibility(View.GONE);
        mCancelBtn.setVisibility(View.GONE);

        v.setDrawingCacheEnabled(true);

        // this is the important line :)
        // Without it the view will have a dimension of 0,0 and the bitmap will be null
        v.layout(0, 0, mWidth, mHeight);

        v.buildDrawingCache(true);
        Bitmap b = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false); // clear drawing cache
        return b;
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
}


