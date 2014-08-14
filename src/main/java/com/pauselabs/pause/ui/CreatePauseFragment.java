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
import com.squareup.otto.Bus;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Create Pause Screen Fragment, this is the default view of the app
 */
public class CreatePauseFragment extends Fragment implements View.OnClickListener, View.OnTouchListener, SeekBar.OnSeekBarChangeListener, View.OnFocusChangeListener{

    private static int RESULT_LOAD_IMAGE = 1;

    private SavedPauseDataSource datasource;

    @Inject protected Bus mBus;
    @Inject protected SharedPreferences mPrefs;

    @InjectView(R.id.backgroundImage) ImageView pauseBackgroundImage;
    @InjectView(R.id.addImageBtn) ImageButton addImageBtn;
    @InjectView(R.id.pauseMessageText) EditText pauseMessageText;
    @InjectView(R.id.pauseLocationText) EditText pauseMessageLocation;
    @InjectView(R.id.pauseDurationText) TextView pauseDurationField;
    @InjectView(R.id.durationSelectorContainer) LinearLayout pauseDurationSelectorContainer;
    @InjectView(R.id.hourSeekLabel) TextView hourSeekBarLabel;
    @InjectView(R.id.minuteSeekLabel) TextView minuteSeekBarLabel;
    @InjectView(R.id.hourSelectorBar) SeekBar pauseHourSelectorSeekBar;
    @InjectView(R.id.minutesSelectorBar) SeekBar pauseMinutesSelectorSeekBar;
    @InjectView(R.id.startPauseBtn) ImageButton startPauseBtn;
    @InjectView(R.id.addFiveMinBtn) Button addFiveMinBtn;
    @InjectView(R.id.addFifteenMinBtn) Button addFifteenMinBtn;
    @InjectView(R.id.addHourBtn) Button addHourBtn;
    @InjectView(R.id.indefiniteBtn) Button indefiniteBtn;

    private Bitmap mSelectedImage;
    private String mSelectedImagePath;
    private Calendar mPauseEndTime = Calendar.getInstance();
    private FrameLayout mCreatePauseLayout;
    private Boolean isExistingPause = false;
    private Long mPauseEndTimeInMillis = 0L;

    private PauseBounceBackMessage mCurrentPauseBouncebackMessage;

    private int hours = 0;
    private int minutes = 0;


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
        addImageBtn.setOnClickListener(this);
        startPauseBtn.setOnClickListener(this);
        pauseDurationField.setOnClickListener(this);

        addFiveMinBtn.setOnClickListener(this);
        addFifteenMinBtn.setOnClickListener(this);
        addHourBtn.setOnClickListener(this);
        indefiniteBtn.setOnClickListener(this);

        pauseBackgroundImage.setOnTouchListener(this);

        mPauseEndTime.setTime(new Date());
        pauseHourSelectorSeekBar.setOnSeekBarChangeListener(this);
        pauseMinutesSelectorSeekBar.setOnSeekBarChangeListener(this);

        pauseMessageText.setOnFocusChangeListener(this);
        pauseMessageLocation.setOnFocusChangeListener(this);

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
            if (!(picturePath.equals(""))) {
                setBackgroundImage(picturePath);
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

        mCurrentPauseBouncebackMessage = savedPause;


    }

    // TODO update this to work with saved messages
    public PauseBounceBackMessage createPauseMessage() {
        String title = "Pause Message";
        String message = pauseMessageText.getText().toString();

        PauseBounceBackMessage bounceBack = new PauseBounceBackMessage(title, message);

        if(!pauseMessageLocation.getText().toString().equals("")){
            bounceBack.setLocation(pauseMessageLocation.getText().toString());
        }

        if(mSelectedImage != null){
            bounceBack.setImage(mSelectedImage);
        }

        if(mSelectedImagePath != null) {
            bounceBack.setPathToImage(mSelectedImagePath);
        }

        return bounceBack;
    }

    public PauseBounceBackMessage buildBounceBackFromInputFields() {

        if(!pauseMessageText.getText().toString().equals("")){
            mCurrentPauseBouncebackMessage.setTitle(pauseMessageText.getText().toString());
            mCurrentPauseBouncebackMessage.setMessage(pauseMessageText.getText().toString());
        }
        if(!pauseMessageLocation.getText().toString().equals("")){
            mCurrentPauseBouncebackMessage.setLocation(pauseMessageLocation.getText().toString());
        }
        if(mPauseEndTimeInMillis != 0L){
            mCurrentPauseBouncebackMessage.setEndTime(mPauseEndTime.getTimeInMillis());
        }
        else{
            mCurrentPauseBouncebackMessage.setEndTime(0L); // indefinite
        }

        return mCurrentPauseBouncebackMessage;
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.addImageBtn:
                if(pauseMessageText.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.cameraMessageRequired), Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent cameraIntent = new Intent(getActivity(), CameraActivity.class);
                    cameraIntent.putExtra(Constants.Message.MESSAGE_PARCEL, (android.os.Parcelable) buildBounceBackFromInputFields());
                    startActivity(cameraIntent);
                }
                break;
            case R.id.startPauseBtn:
                dismissDurationSelector();

                if(pauseMessageText.getText().toString().equals("")){
                    Toast.makeText(getActivity(), getString(R.string.messageRequired), Toast.LENGTH_SHORT).show();
                }
                else {
                    //PauseBounceBackMessage savedPause = buildBounceBackFromInputFields();
                    mCurrentPauseBouncebackMessage = buildBounceBackFromInputFields();

                    // save BounceBack to database
                    mCurrentPauseBouncebackMessage =  datasource.createSavedPause(mCurrentPauseBouncebackMessage);

                    // set active Pause database ID in sharedPrefs
                    mPrefs.edit().putLong(Constants.Pause.ACTIVE_PAUSE_DATABASE_ID_PREFS, mCurrentPauseBouncebackMessage.getId()).commit();

                    PauseApplication.startPauseService();

                    // show scoreboard activity
                    Intent scoreboardIntent = new Intent(getActivity(), ScoreboardActivity.class);
                    startActivity(scoreboardIntent);
                }


                break;
            case R.id.pauseDurationText:
                InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pauseMessageText.getWindowToken(), 0);
                imm.hideSoftInputFromWindow(pauseMessageLocation.getWindowToken(), 0);

                pauseDurationSelectorContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.addFiveMinBtn:
                mPauseEndTime.add(Calendar.MINUTE, 5);
                pauseMinutesSelectorSeekBar.setProgress(pauseMinutesSelectorSeekBar.getProgress() + 5);
                minuteSeekBarLabel.setText(pauseMinutesSelectorSeekBar.getProgress() + " MINUTES");
                updateEndTimeView();
                break;
            case R.id.addFifteenMinBtn:
                mPauseEndTime.add(Calendar.MINUTE, 15);
                pauseMinutesSelectorSeekBar.setProgress(pauseMinutesSelectorSeekBar.getProgress() + 15);
                minuteSeekBarLabel.setText(pauseMinutesSelectorSeekBar.getProgress() + " MINUTES");
                updateEndTimeView();
                break;
            case R.id.addHourBtn:
                mPauseEndTime.add(Calendar.MINUTE, 60);
                pauseHourSelectorSeekBar.setProgress(pauseHourSelectorSeekBar.getProgress() + 1);
                hourSeekBarLabel.setText(pauseHourSelectorSeekBar.getProgress() + " HOURS");
                updateEndTimeView();
                break;
            case R.id.indefiniteBtn:
                setDurationIndefinte();
                break;
            default:
                dismissDurationSelector();
        }
    }

    public void dismissDurationSelector(){
        pauseDurationSelectorContainer.setVisibility(View.GONE);
    }

    public void updateEndTimeView() {
        SimpleDateFormat df = new SimpleDateFormat("h:mm a");

        Date endTime = mPauseEndTime.getTime();
        mPauseEndTimeInMillis = mPauseEndTime.getTimeInMillis();
        pauseDurationField.setText("until " + df.format(endTime).toString());
    }

    private void setDurationIndefinte() {
        // reset seekbar
        pauseHourSelectorSeekBar.setProgress(0);
        hourSeekBarLabel.setText(pauseHourSelectorSeekBar.getProgress() + " HOURS");
        pauseMinutesSelectorSeekBar.setProgress(0);
        minuteSeekBarLabel.setText(pauseMinutesSelectorSeekBar.getProgress() + " MINUTES");
        // reset time
        mPauseEndTime.setTime(new Date());
        // update text
        pauseDurationField.setText("until stopped");
        // set duration indefinite on Pause
        mPauseEndTimeInMillis = 0L;

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        dismissDurationSelector();
        return true;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {


    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch(seekBar.getId()){
            case R.id.hourSelectorBar:
                if(hours >= seekBar.getProgress()){
                    // decrease time
                    mPauseEndTime.add(Calendar.HOUR_OF_DAY, -1 * (hours - seekBar.getProgress()));
                }
                else{
                    // increase time
                    mPauseEndTime.add(Calendar.HOUR_OF_DAY, seekBar.getProgress() - hours);
                }
                hours = seekBar.getProgress();
                hourSeekBarLabel.setText(hours + " HOURS");
                break;
            case R.id.minutesSelectorBar:
                if(minutes >= seekBar.getProgress()){
                    mPauseEndTime.add(Calendar.MINUTE, -1 * (minutes - seekBar.getProgress()));
                }
                else{
                    mPauseEndTime.add(Calendar.MINUTE, seekBar.getProgress() - minutes);
                }
                minutes = seekBar.getProgress();
                minuteSeekBarLabel.setText(minutes + " MINUTES");
                break;
        }
        updateEndTimeView();
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        dismissDurationSelector();
    }
}
