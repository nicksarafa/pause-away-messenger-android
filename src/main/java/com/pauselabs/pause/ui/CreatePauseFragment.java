package com.pauselabs.pause.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.InjectView;
import butterknife.Views;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.pauselabs.R;
import com.pauselabs.pause.Injector;
import com.pauselabs.pause.PauseApplication;
import com.pauselabs.pause.core.Constants;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import com.pauselabs.pause.views.DurationSelectorView;
import com.squareup.otto.Bus;
import javax.inject.Inject;

/** Create Pause Screen Fragment, this is the default view of the app */
public class CreatePauseFragment extends Fragment
    implements View.OnClickListener,
        DurationSelectorView.OnDurationChangedListener,
        View.OnFocusChangeListener,
        TextWatcher {

  private static final String TAG = CreatePauseFragment.class.getSimpleName();
  private static int RESULT_LOAD_IMAGE = 1;

  private SavedPauseDataSource datasource;

  @Inject protected Bus mBus;
  @Inject protected SharedPreferences mPrefs;

  @InjectView(R.id.backgroundImage)
  ImageView pauseBackgroundImage;

  @InjectView(R.id.backgroundLayout)
  RelativeLayout pauseContainerLayout;

  @InjectView(R.id.messageSelectorBtn)
  ImageButton messageSelectorBtn;

  @InjectView(R.id.durationSelectorBtn)
  ImageButton durationSelectorBtn;

  @InjectView(R.id.cameraSelectorBtn)
  ImageButton cameraSelectorBtn;

  @InjectView(R.id.gallerySelectorBtn)
  ImageButton gallerySelectorBtn;

  @InjectView(R.id.pauseMessageText)
  EditText pauseMessageText;

  @InjectView(R.id.pauseDurationText)
  TextView pauseDurationField;

  @InjectView(R.id.durationContainer)
  RelativeLayout pauseDurationContainer;

  @InjectView(R.id.startPauseBtn)
  ImageButton startPauseBtn;

  @InjectView(R.id.durationSelectorView)
  DurationSelectorView mDurationSelectorView;

  private Bitmap mSelectedImage;
  private String mSelectedImagePath;
  private FrameLayout mCreatePauseLayout;
  private Boolean isExistingPause = false;
  private Long mPauseEndTimeInMillis = 0L;
  private Tracker mAnalyticsTracker;
  private InputMethodManager mInputManager;

  private PauseBounceBackMessage mCurrentPauseBouncebackMessage;

  public CreatePauseFragment() {}

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setHasOptionsMenu(true);

    datasource = new SavedPauseDataSource(getActivity());
    datasource.open();

    mAnalyticsTracker = PauseApplication.getTracker(PauseApplication.TrackerName.GLOBAL_TRACKER);
    mAnalyticsTracker.setScreenName("CreatePauseScreenView");

    mInputManager =
        (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
  }

  @Override
  public View onCreateView(
      final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
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
    pauseMessageText.setOnFocusChangeListener(this);
    pauseMessageText.addTextChangedListener(this);
    startPauseBtn.setEnabled(false);

    mDurationSelectorView.setOnDurationChangedListener(this);

    mCurrentPauseBouncebackMessage = new PauseBounceBackMessage();

    Intent intent = getActivity().getIntent();
    Long editId = intent.getLongExtra(Constants.Pause.EDIT_PAUSE_MESSAGE_ID_EXTRA, -1L);
    if (editId != null & editId > 0L) {
      savedPauseMessageSelected(editId);
    }

    mAnalyticsTracker.send(new HitBuilders.AppViewBuilder().build());
  }

  @Override
  public void onAttach(final Activity activity) {
    super.onAttach(activity);

    Injector.inject(this);
  }

  @Override
  public void onResume() {
    super.onResume();
    datasource.open();

    focusPauseMessageTextAndShowKeyboard();
  }

  @Override
  public void onPause() {
    super.onPause();
    datasource.close();
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
      Uri selectedImage = data.getData();
      String[] filePathColumn = {MediaStore.Images.Media.DATA};

      Cursor cursor =
          getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
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

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.main_activity_actions, menu);
    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(final MenuItem item) {
    switch (item.getItemId()) {
      case R.id.action_settings:
        Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
        startActivity(settingsIntent);
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void savedPauseMessageSelected(long savedMessageId) {

    // if there is an existing image, lets recycle the old bitmap before loading a new image
    if (mSelectedImage != null) {
      mSelectedImage.recycle();
    }

    PauseBounceBackMessage savedPause = datasource.getSavedPauseById(savedMessageId);
    isExistingPause = true;

    if (savedPause.getMessage() != null && !savedPause.getMessage().equals("")) {
      pauseMessageText.setText(savedPause.getMessage());
    }

    if (savedPause.getPathToOriginal() != null && !savedPause.getPathToOriginal().equals("")) {
      Bitmap original = BitmapFactory.decodeFile(savedPause.getPathToOriginal());
      BitmapDrawable savedDrawable = new BitmapDrawable(getResources(), original);
      pauseBackgroundImage.setImageDrawable(savedDrawable);
      mSelectedImage = original;
      mSelectedImagePath = savedPause.getPathToImage();
    } else {
      // remove previously shown image if its only a saved SMS
      if (mSelectedImage != null) {
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

    if (!pauseMessageText.getText().toString().equals("")) {
      mCurrentPauseBouncebackMessage.setTitle(pauseMessageText.getText().toString());
      mCurrentPauseBouncebackMessage.setMessage(pauseMessageText.getText().toString());
    }

    if (mPauseEndTimeInMillis > 0L && !mDurationSelectorView.isIndefinite()) {
      mCurrentPauseBouncebackMessage.setEndTime(mPauseEndTimeInMillis);
    } else {
      mCurrentPauseBouncebackMessage.setEndTime(0L); // indefinite
    }

    mAnalyticsTracker.send(
        new HitBuilders.EventBuilder()
            .setCategory(getString(R.string.data))
            .setAction(getString(R.string.start_pause_button_clicked))
            .setLabel(mCurrentPauseBouncebackMessage.toString())
            .build());

    return mCurrentPauseBouncebackMessage;
  }

  private void updateDurationFieldText() {
    if (mDurationSelectorView.isIndefinite()) {
      pauseDurationField.setText(getString(R.string.pauseMessageDurationIndefinite));
    } else {
      pauseDurationField.setText(mDurationSelectorView.getDurationEndTimeText());
    }
  }

  @Override
  public void onClick(View v) {
    switch (v.getId()) {
      case R.id.messageSelectorBtn:
        if (pauseMessageText.isFocused()) {
          unfocusPauseMessageTextAndHideKeyboard();
        } else {
          focusPauseMessageTextAndShowKeyboard();
        }
        break;
      case R.id.durationSelectorBtn:
        if (pauseMessageText.getText().toString().equals("")) {
          Toast.makeText(
                  getActivity(), getString(R.string.durationMessageRequired), Toast.LENGTH_SHORT)
              .show();
        } else {
          if (mDurationSelectorView.isDisplayed()) {
            dismissDurationSelector();
          } else {
            displayDurationSelector();
          }
        }
        break;

      case R.id.cameraSelectorBtn:
        if (pauseMessageText.getText().toString().equals("")) {
          Toast.makeText(
                  getActivity(), getString(R.string.cameraMessageRequired), Toast.LENGTH_SHORT)
              .show();
        } else {
          mAnalyticsTracker.send(
              new HitBuilders.EventBuilder()
                  .setCategory(getString(R.string.ui_action))
                  .setAction(getString(R.string.camera_button_clicked))
                  .setLabel(getString(R.string.camera_button))
                  .build());

          Intent cameraIntent = new Intent(getActivity(), CameraActivity.class);
          cameraIntent.putExtra(
              Constants.Message.MESSAGE_PARCEL,
              (android.os.Parcelable) buildBounceBackFromInputFields());
          startActivity(cameraIntent);
        }
        break;

      case R.id.gallerySelectorBtn:
        if (pauseMessageText.getText().toString().equals("")) {
          Toast.makeText(
                  getActivity(), getString(R.string.cameraMessageRequired), Toast.LENGTH_SHORT)
              .show();
        } else {
          mAnalyticsTracker.send(
              new HitBuilders.EventBuilder()
                  .setCategory(getString(R.string.ui_action))
                  .setAction(getString(R.string.gallery_button_clicked))
                  .setLabel(getString(R.string.gallery_button))
                  .build());

          Intent i =
              new Intent(
                  Intent.ACTION_PICK,
                  android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
          startActivityForResult(i, RESULT_LOAD_IMAGE);
        }
        break;

      case R.id.startPauseBtn:
        if (pauseMessageText.getText().toString().equals("")) {
          Toast.makeText(getActivity(), getString(R.string.messageRequired), Toast.LENGTH_SHORT)
              .show();
        } else {
          mAnalyticsTracker.send(
              new HitBuilders.EventBuilder()
                  .setCategory(getString(R.string.ui_action))
                  .setAction(getString(R.string.start_pause_button_clicked))
                  .setLabel(getString(R.string.start_button))
                  .build());

          mCurrentPauseBouncebackMessage = buildBounceBackFromInputFields();

          // if previously saved, don't resave
          if (mCurrentPauseBouncebackMessage.getId()
              == -1) { // id of -1 is a new unsaved bounce back
            // save BounceBack to database
            mCurrentPauseBouncebackMessage =
                datasource.createSavedPause(mCurrentPauseBouncebackMessage);
          }

          // set active Pause database ID in sharedPrefs
          mPrefs
              .edit()
              .putLong(
                  Constants.Pause.ACTIVE_PAUSE_DATABASE_ID_PREFS,
                  mCurrentPauseBouncebackMessage.getId())
              .commit();

          PauseApplication.startPauseService();

          // show scoreboard activity
          Intent scoreboardIntent = new Intent(getActivity(), ScoreboardActivity.class);
          startActivity(scoreboardIntent);
        }

        break;
      default:
        //dismissDurationSelector();
        break;
    }
  }

  private void showPreviewScreen() {
    Intent previewIntent = new Intent(getActivity(), PreviewActivity.class);
    previewIntent.putExtra(
        Constants.Message.MESSAGE_PARCEL, (android.os.Parcelable) buildBounceBackFromInputFields());
    startActivity(previewIntent);
  }

  public void displayDurationSelector() {
    mAnalyticsTracker.send(
        new HitBuilders.EventBuilder()
            .setCategory(getString(R.string.ui_action))
            .setAction(getString(R.string.timer_button_clicked))
            .setLabel(getString(R.string.timer_button))
            .build());
    if (pauseMessageText.isFocused()) {
      unfocusPauseMessageTextAndHideKeyboard();
    }
    durationSelectorBtn.setImageResource(R.drawable.ic_timer_selector_alt);
    pauseDurationContainer.setVisibility(View.VISIBLE);
    pauseDurationField.requestFocus();
    mDurationSelectorView.displayDurationSelector();
  }

  public void dismissDurationSelector() {
    durationSelectorBtn.setImageResource(R.drawable.ic_timer_selector);
    mDurationSelectorView.dismissDurationSelector();
    pauseDurationField.clearFocus();
    //pauseContainerLayout.requestFocus();

  }

  public void focusPauseMessageTextAndShowKeyboard() {
    if (mDurationSelectorView.isDisplayed()) {
      dismissDurationSelector();
    }
    pauseMessageText.requestFocus();
    mInputManager.showSoftInput(pauseMessageText, InputMethodManager.SHOW_IMPLICIT);
  }

  public void unfocusPauseMessageTextAndHideKeyboard() {
    mInputManager.hideSoftInputFromWindow(pauseMessageText.getWindowToken(), 0);
    pauseMessageText.clearFocus();
    pauseContainerLayout.requestFocus();
  }

  @Override
  public void onDurationChanged() {
    Log.d(TAG, "onDurationChanged");
    updateDurationFieldText();
  }

  @Override
  public void onFocusChange(View view, boolean b) {
    if (view.getId() == R.id.pauseMessageText) {
      if (pauseMessageText.isFocused()) {
        if (mDurationSelectorView.isDisplayed()) {
          dismissDurationSelector();
        }
        messageSelectorBtn.setImageResource(R.drawable.ic_pencil_selector_alt);
      } else {
        messageSelectorBtn.setImageResource(R.drawable.ic_pencil_selector);
      }
    }
  }

  @Override
  public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

  @Override
  public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

  @Override
  public void afterTextChanged(Editable editable) {
    if (!pauseMessageText.getText().toString().equals("")) {
      startPauseBtn.setEnabled(true);
    } else {
      startPauseBtn.setEnabled(false);
    }
  }
}
