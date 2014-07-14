package com.pauselabs.pause.ui;

import android.app.Activity;
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
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.view.*;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.InjectView;
import butterknife.Views;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.events.PauseSessionChangedEvent;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.squareup.otto.Bus;

import javax.inject.Inject;

/**
 * Create Pause Screen Fragment, this is the default view of the app
 */
public class CreatePauseFragment extends Fragment {

    private static int RESULT_LOAD_IMAGE = 1;

    private SavedPauseDataSource datasource;

    @Inject
    protected Bus mBus;

    @Inject
    protected SharedPreferences mPrefs;

    @InjectView(R.id.backgroundImage)
    ImageView pauseBackgroundImage;

    @InjectView(R.id.blurredBackgroundImage)
    ImageView pauseBlurredBackgroundImage;

    @InjectView(R.id.addImageBtn)
    ImageButton addImageBtn;

    @InjectView(R.id.pauseMessageText)
    TextView pauseMessageText;

    @InjectView(R.id.pauseLocationText)
    TextView pauseMessageLocation;

    private Bitmap mSelectedImage;
    private String mSelectedImagePath;
    private FrameLayout mCreatePauseLayout;
    private Boolean isExistingPause = false;


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

        addImageBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        return view;

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
        options.inSampleSize = 8;
        Bitmap blurTemplate = BitmapFactory.decodeFile(pathToImage, options);

        options.inSampleSize = 2;
        Bitmap original = BitmapFactory.decodeFile(pathToImage, options);


        //define this only once if blurring multiple times
        RenderScript rs = RenderScript.create(getActivity());

        //this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
        final Allocation input = Allocation.createFromBitmap(rs, blurTemplate); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(rs, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(2f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        //Drawable drawable = getResources().getDrawable(R.drawable.gradient_pause_image);

        BitmapDrawable blurredDrawable = new BitmapDrawable(getResources(), blurTemplate);
        BitmapDrawable originalDrawable = new BitmapDrawable(getResources(), original);

        mSelectedImage = original;
        mSelectedImagePath = pathToImage;

        pauseBackgroundImage.setImageDrawable(originalDrawable);
        pauseBlurredBackgroundImage.setImageDrawable(blurredDrawable);
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

        pauseBlurredBackgroundImage.setImageDrawable(layerDrawable);


//        Bitmap b = Bitmap.createBitmap(theView.getWidth(), theView.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas c = new Canvas(b);
//        theView.draw(c);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_actions, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_pause:
                PauseBounceBackMessage savedPause = createPauseMessage();
                if(!isExistingPause){
                    // save Pause to db if it is new
                    savedPause = datasource.createSavedPause(savedPause);
                }

                // set active Pause database ID in sharedPrefs
                mPrefs.edit().putLong(Constants.Pause.ACTIVE_PAUSE_DATABASE_ID_PREFS, savedPause.getId()).commit();

                mBus.post(new PauseSessionChangedEvent(Constants.Pause.PAUSE_SESSION_STATE_ACTIVE));

                return true;
            case R.id.settingsMenuItem:
                Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
                startActivity(settingsIntent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void savedPauseMessageSelected(long savedMessageId){
        PauseBounceBackMessage savedPause = datasource.getSavedPauseById(savedMessageId);
        isExistingPause = true;

        if(savedPause.getMessage() != null && !savedPause.getMessage().equals("")){
            pauseMessageText.setText(savedPause.getMessage());
        }

        if(savedPause.getPathToImage() != null && !savedPause.getPathToImage().equals("")){
            Bitmap original = BitmapFactory.decodeFile(savedPause.getPathToImage());
            BitmapDrawable savedDrawable = new BitmapDrawable(getResources(), original);
            pauseBackgroundImage.setImageDrawable(savedDrawable);
        }


    }

    // TODO update this to work with saved messages
    public PauseBounceBackMessage createPauseMessage() {
        String title = "Pause Message";
        String message = "Can't talk now, I'm wired in";
        message = pauseMessageText.getText().toString();

        PauseBounceBackMessage bounceBack = new PauseBounceBackMessage(title, message);

        if(mSelectedImage != null){
            bounceBack.setImage(mSelectedImage);
        }

        if(mSelectedImagePath != null) {
            bounceBack.setPathToImage(mSelectedImagePath);
        }

        return bounceBack;
    }


}
