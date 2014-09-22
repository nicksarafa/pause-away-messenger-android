package com.pauselabs.pause.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import com.pauselabs.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * This view displays a custom selector view which allows a user to adjust a duration of time by manipulating
 * hour and minute selectors and additional time buttons
 */
public class DurationSelectorView extends LinearLayout implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

    private static final String TAG = DurationSelectorView.class.getSimpleName();

    private OnDurationChangedListener mDurationChangedListener = null;
    private TextView mHourSeekLabel;
    private TextView mMinuteSeekLabel;
    private SeekBar mHourSeekBar;
    private SeekBar mMinuteSeekBar;
    private Button mAddFiveMinutesBtn;
    private Button mAddFifteenMinutesBtn;
    private Button mAddHourBtn;
    private Button mIndefiniteTimeBtn;
    private Context mContext;
    private boolean isIndefinite = true;

    SimpleDateFormat dateFormat = new SimpleDateFormat("h:mm a");
    private Calendar mDurationEndTime;
    private int mHours = 0;
    private int mMinutes = 0;

    public DurationSelectorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        setOrientation(VERTICAL);
        inflate(context, R.layout.duration_selector_view, this);

        initViews();
    }

    public void initViews() {

        mHourSeekLabel = (TextView) findViewById(R.id.hourSeekLabel);
        mMinuteSeekLabel = (TextView) findViewById(R.id.minuteSeekLabel);

        mHourSeekBar = (SeekBar) findViewById(R.id.hourSelectorBar);
        mMinuteSeekBar = (SeekBar) findViewById(R.id.minutesSelectorBar);
        mHourSeekBar.setOnSeekBarChangeListener(this);
        mMinuteSeekBar.setOnSeekBarChangeListener(this);

        mAddFiveMinutesBtn = (Button) findViewById(R.id.addFiveMinBtn);
        mAddFifteenMinutesBtn = (Button) findViewById(R.id.addFifteenMinBtn);
        mAddHourBtn = (Button) findViewById(R.id.addHourBtn);
        mIndefiniteTimeBtn = (Button) findViewById(R.id.indefiniteBtn);

        setOnClickListener(this);
        mAddFiveMinutesBtn.setOnClickListener(this);
        mAddFifteenMinutesBtn.setOnClickListener(this);
        mAddHourBtn.setOnClickListener(this);
        mIndefiniteTimeBtn.setOnClickListener(this);

    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int seekBarStopPos = seekBar.getProgress();
        switch(seekBar.getId()){
            case R.id.hourSelectorBar:
                if(mHours >= seekBarStopPos){
                    // decrease time
                    mHours = mHours - (mHours - seekBar.getProgress());
                }
                else{
                    // increase time
                    mHours = mHours + (seekBar.getProgress() - mHours);
                }
                updateView();
                break;
            case R.id.minutesSelectorBar:
                if(mMinutes >= seekBar.getProgress() * 5){
                    // decrease time
                    mMinutes = mMinutes - (mMinutes - seekBar.getProgress() * 5);
                }
                else{
                    // increase time
                    mMinutes = mMinutes + (seekBar.getProgress() * 5 - mMinutes);
                }
                updateView();
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.addFiveMinBtn:
                mMinutes = mMinutes + 5;
                updateView();
                break;
            case R.id.addFifteenMinBtn:
                mMinutes = mMinutes + 15;
                updateView();
                break;
            case R.id.addHourBtn:
                mHours = mHours + 1;
                updateView();
                break;
            case R.id.indefiniteBtn:
                setDurationToIndefinte();
                break;
            default:
                break;
        }
    }

    private void updateView() {
        if(mMinutes >= 60){
            mMinutes = 0;
            mHours ++;
        }

        if(mMinutes > 0 || mHours > 0) {
            isIndefinite = false;
        }

        mMinuteSeekBar.setProgress(mMinutes / 5);
        mHourSeekBar.setProgress(mHours);
        mMinuteSeekLabel.setText(mMinutes + mContext.getString(R.string.durationSelectorMinutesLabel));
        mHourSeekLabel.setText(mHours + mContext.getString(R.string.durationSelectorHourLabel));

        if (mDurationChangedListener != null) {
            mDurationChangedListener.onDurationChanged();
        }
    }

    private void setDurationToIndefinte() {
        mHours = 0;
        mMinutes = 0;
        isIndefinite = true;
        updateView();

    }

    /**
     * Register a callback to be invoked when the currently selected item changes.
     *
     * @param listener Can be null.
     *                 The current item changed listener to attach to this view.
     */
    public void setOnDurationChangedListener(OnDurationChangedListener listener) {
        mDurationChangedListener = listener;
    }

    public int getHours() {
        return mHours;
    }

    public int getMinutes() {
        return mMinutes;
    }

    /**
     * Get the current calendar time and add current duration hours and minutes
     * @return end time calendar
     */
    public Calendar getDurationEndTime() {
        mDurationEndTime = Calendar.getInstance();
        mDurationEndTime.setTime(new Date());
        mDurationEndTime.add(Calendar.MINUTE, mMinutes);
        mDurationEndTime.add(Calendar.HOUR, mHours);
        return mDurationEndTime;
    }

    public Long getDurationEndTimeInMillis() {
        return getDurationEndTime().getTimeInMillis();
    }

    public String getDurationEndTimeText() {
        String durationText = mContext.getString(R.string.pauseMessageDurationPrefix) + dateFormat.format(getDurationEndTime().getTime()).toString();
        Log.d(TAG, durationText);
        return durationText;
    }

    public boolean isIndefinite() {
        return isIndefinite;
    }

    public void displayDurationSelector() {
        this.setVisibility(View.VISIBLE);
    }

    public void dismissDurationSelector() {
        this.setVisibility(View.GONE);
    }

    public Boolean isDisplayed() {
        return this.getVisibility() == View.VISIBLE;
    }

    public interface OnDurationChangedListener {
        public void onDurationChanged();
    }


}
