package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v8.renderscript.RenderScript;
import android.util.Log;
import android.view.*;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.views.DurationSelectorView;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Create Pause Screen Fragment, this is the default view of the app
 */
public class CreatePauseFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, View.OnFocusChangeListener, DurationSelectorView.OnDurationChangedListener {

    private static final String TAG = CreatePauseFragment.class.getSimpleName();
    private static int RESULT_LOAD_IMAGE = 1;

    private SavedPauseDataSource datasource;

    @Inject protected Bus mBus;
    @Inject protected SharedPreferences mPrefs;

    @InjectView(R.id.backgroundImage) ImageView pauseBackgroundImage;
    @InjectView(R.id.messageSelectorBtn) ImageButton messageSelectorBtn;
    @InjectView(R.id.durationSelectorBtn) ImageButton durationSelectorBtn;
    @InjectView(R.id.cameraSelectorBtn) ImageButton cameraSelectorBtn;
    @InjectView(R.id.gallerySelectorBtn) ImageButton gallerySelectorBtn;
    @InjectView(R.id.pauseMessageText) EditText pauseMessageText;
    @InjectView(R.id.pauseDurationText) TextView pauseDurationField;
    @InjectView(R.id.durationContainer) RelativeLayout pauseDurationContainer;
    @InjectView(R.id.startPauseBtn) ImageButton startPauseBtn;
    @InjectView(R.id.durationSelectorView) DurationSelectorView mDurationSelectorView;

    private Bitmap mSelectedImage;
    private String mSelectedImagePath;
    private FrameLayout mCreatePauseLayout;
    private Boolean isExistingPause = false;
    private Long mPauseEndTimeInMillis = 0L;

    private PauseBounceBackMessage mCurrentPauseBouncebackMessage;

    public CreatePauseFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        datasource = new SavedPauseDataSource(getActivity());
        datasource.open();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.create_pause_fragment, null);
        mCreatePauseLayout = (FrameLayout) view;

        // Inject Butterknife views
        Views.inject(this, view);

        //initialize views
        init();

        return view;
    }

    private void init() {
        // set listeners
        messageSelectorBtn.setOnClickListener(this);
        durationSelectorBtn.setOnClickListener(this);
        cameraSelectorBtn.setOnClickListener(this);
        gallerySelectorBtn.setOnClickListener(this);
        startPauseBtn.setOnClickListener(this);
        pauseDurationField.setOnClickListener(this);

//        startPauseBtn.setEnabled(false);

        mDurationSelectorView.setOnDurationChangedListener(this);

        pauseBackgroundImage.setOnTouchListener(this);

        pauseMessageText.setOnFocusChangeListener(this);
//        pauseMessageText.setFocusableInTouchMode(true);
//        pauseMessageText.requestFocus();

        mCurrentPauseBouncebackMessage = new PauseBounceBackMessage();

        Intent intent = getActivity().getIntent();
        Long editId = intent.getLongExtra(Constants.Pause.EDIT_PAUSE_MESSAGE_ID_EXTRA, -1L);
        if(editId != null & editId > 0L){
            savedPauseMessageSelected(editId);
        }

    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);

        Injector.inject(this);
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


    public void setBackgroundImage(String pathToImage){
        isExistingPause = false;
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap original = BitmapFactory.decodeFile(pathToImage, options);

        //define this only once if blurring multiple times
        RenderScript rs = RenderScript.create(getActivity());

        //this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
//        final Allocation input = Allocation.createFromBitmap(rs, blurTemplate); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
//        final Allocation output = Allocation.createTyped(rs, input.getType());
//        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
//        script.setRadius(2f);
//        script.setInput(input);
//        script.forEach(output);
//        output.copyTo(blurTemplate);

        //Drawable drawable = getResources().getDrawable(R.drawable.gradient_pause_image);

        BitmapDrawable originalDrawable = new BitmapDrawable(getResources(), original);

        mSelectedImage = original;
        mSelectedImagePath = pathToImage;

        pauseBackgroundImage.setImageDrawable(originalDrawable);
    }

    public void createPauseImageFromView() {
        Bitmap bitmap = Bitmap.createBitmap(pauseBackgroundImage.getWidth(), pauseBackgroundImage.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bitmap);

        Paint textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(50);

        int xPos = (c.getWidth() / 2);
        int yPos = (int) ((c.getHeight() / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)) ;

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap original = BitmapFactory.decodeFile(mSelectedImagePath, options);
        BitmapDrawable originalDrawable = new BitmapDrawable(getResources(), original);

        originalDrawable.draw(c);

        c.drawText(pauseMessageText.getText().toString(), xPos, yPos, textPaint);

        LayerDrawable layerDrawable = new LayerDrawable(
                new Drawable[]{originalDrawable, new BitmapDrawable(bitmap)});

        pauseBackgroundImage.setImageDrawable(layerDrawable);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflater.inflate(R.menu.main_activity_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


//    @Override
//    public boolean onOptionsItemSelected(final MenuItem item) {
//
//        switch (item.getItemId()) {
//            case R.id.settingsMenuItem:
//                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
//                startActivity(settingsIntent);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }


    public void savedPauseMessageSelected(long savedMessageId){

        // if there is an existing image, lets recycle the old bitmap before loading a new image
        if(mSelectedImage != null){
            mSelectedImage.recycle();
        }

        PauseBounceBackMessage savedPause = datasource.getSavedPauseById(savedMessageId);
        isExistingPause = true;

        if(savedPause.getMessage() != null && !savedPause.getMessage().equals("")){
            pauseMessageText.setText(savedPause.getMessage());
        }

        if(savedPause.getPathToOriginal() != null && !savedPause.getPathToOriginal().equals("")){
            Bitmap original = BitmapFactory.decodeFile(savedPause.getPathToOriginal());
            BitmapDrawable savedDrawable = new BitmapDrawable(getResources(), original);
            pauseBackgroundImage.setImageDrawable(savedDrawable);
            mSelectedImage = original;
            mSelectedImagePath = savedPause.getPathToImage();
        }
        else{
            // remove previously shown image if its only a saved SMS
            if(mSelectedImage != null){
                mSelectedImage.recycle();
            }
            mSelectedImage = null;
            mSelectedImagePath = "";
            pauseBackgroundImage.setImageDrawable(null);
        }

        mCurrentPauseBouncebackMessage = savedPause;

    }


    public PauseBounceBackMessage buildBounceBackFromInputFields() {

        mPauseEndTimeInMillis = mDurationSelectorView.getDurationEndTimeInMillis();

        if(!pauseMessageText.getText().toString().equals("")){
            mCurrentPauseBouncebackMessage.setTitle(pauseMessageText.getText().toString());
            mCurrentPauseBouncebackMessage.setMessage(pauseMessageText.getText().toString());
        }

        if(mPauseEndTimeInMillis > 0L && !mDurationSelectorView.isIndefinite()){
            mCurrentPauseBouncebackMessage.setEndTime(mPauseEndTimeInMillis);
        }
        else{
            mCurrentPauseBouncebackMessage.setEndTime(0L); // indefinite
        }

        return mCurrentPauseBouncebackMessage;
    }

    private void updateDurationFieldText() {
        if(mDurationSelectorView.isIndefinite()){
            pauseDurationField.setText(getString(R.string.pauseMessageDurationIndefinite));
        }
        else {
            pauseDurationField.setText(mDurationSelectorView.getDurationEndTimeText());
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.durationSelectorBtn:
                if(pauseMessageText.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.durationMessageRequired), Toast.LENGTH_SHORT).show();
                }
                else{
                    durationSelectorBtn.setImageResource(R.drawable.ic_timer_selector_alt);
                    pauseDurationField.requestFocus();
                    pauseDurationContainer.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(pauseMessageText.getWindowToken(), 0);
                    mDurationSelectorView.displayDurationSelector();

                    //pauseDurationSelectorContainer.setVisibility(View.VISIBLE);
                }
                break;

            case R.id.cameraSelectorBtn:
                if(pauseMessageText.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.cameraMessageRequired), Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent cameraIntent = new Intent(getActivity(), CameraActivity.class);
                    cameraIntent.putExtra(Constants.Message.MESSAGE_PARCEL, (android.os.Parcelable) buildBounceBackFromInputFields());
                    startActivity(cameraIntent);
                }
                break;

            case R.id.gallerySelectorBtn:
                if(pauseMessageText.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.cameraMessageRequired), Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(i, RESULT_LOAD_IMAGE);
                }
                break;

            case R.id.startPauseBtn:
                dismissDurationSelector();

                if(pauseMessageText.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.messageRequired), Toast.LENGTH_SHORT).show();
                }
                else {
                    mCurrentPauseBouncebackMessage = buildBounceBackFromInputFields();

                    // if previously saved, don't resave
                    if(mCurrentPauseBouncebackMessage.getId() == -1) {   // id of -1 is a new unsaved bounce back
                        // save BounceBack to database
                        mCurrentPauseBouncebackMessage =  datasource.createSavedPause(mCurrentPauseBouncebackMessage);
                    }

                    // set active Pause database ID in sharedPrefs
                    mPrefs.edit().putLong(Constants.Pause.ACTIVE_PAUSE_DATABASE_ID_PREFS, mCurrentPauseBouncebackMessage.getId()).commit();

                    PauseApplication.startPauseService();

                    // show scoreboard activity
                    Intent scoreboardIntent = new Intent(getActivity(), ScoreboardActivity.class);
                    startActivity(scoreboardIntent);
                }


                break;
            case R.id.pauseDurationText:
//                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
//                        Context.INPUT_METHOD_SERVICE);
//                imm.hideSoftInputFromWindow(pauseMessageText.getWindowToken(), 0);
//                //imm.hideSoftInputFromWindow(pauseMessageLocation.getWindowToken(), 0);
//
//                pauseDurationSelectorContainer.setVisibility(View.VISIBLE);
                break;
            default:
                dismissDurationSelector();
        }
    }

    private void showPreviewScreen() {
        Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
        previewIntent.putExtra(Constants.Message.MESSAGE_PARCEL, (android.os.Parcelable) buildBounceBackFromInputFields());
        startActivity(previewIntent);
    }



    public void dismissDurationSelector(){
        mDurationSelectorView.dismissDurationSelector();
        durationSelectorBtn.setImageResource(R.drawable.ic_timer_selector);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        dismissDurationSelector();
        return true;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        messageSelectorBtn.setImageResource(R.drawable.ic_pencil_selector);
        if(v.getId() == R.id.pauseMessageText && hasFocus){
            messageSelectorBtn.setImageResource(R.drawable.ic_pencil_selector_alt);
            dismissDurationSelector();
        }
    }

    @Override
    public void onDurationChanged() {
        Log.d(TAG, "onDurationChanged");
        updateDurationFieldText();
    }
}
