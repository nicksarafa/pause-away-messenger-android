package com.pauselabs.pause.views;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.widget.TextView;
import com.pauselabs.R;
import java.util.Date;

/**
 * This TextView is designed to be set with a start and/or end time. The timer will either count up
 * indefinitely or count down from a designated end time
 */
public class AutoUpdatingTimerView extends TextView {

  private Date mEndTime = null;
  private Date mStartTime;
  private boolean isCountdown = true;
  private boolean mStophandler = false;
  private Context mContext;

  private Handler handler = new Handler();

  private Runnable runnable =
      new Runnable() {
        @Override
        public void run() {
          if (!mStophandler) {
            updateView();
            handler.postDelayed(this, 1000);
          }
        }
      };

  public AutoUpdatingTimerView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
  }

  private void updateView() {
    Date currentDate = new Date();

    if (isCountdown) {
      if (currentDate.getTime() > mEndTime.getTime()) {
        //            // timer has expired
        //            stopPauseSession();

        // display results dialog
        mStophandler = true;

        setText(mContext.getString(R.string.pause_session_ended));

      } else {
        long diff = mEndTime.getTime() - currentDate.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        setText(hours % 24 + "h " + minutes % 60 + "m " + seconds % +60 + "s");
      }
    } else {
      long diff = currentDate.getTime() - mStartTime.getTime();
      long seconds = diff / 1000;
      long minutes = seconds / 60;
      long hours = minutes / 60;
      long days = hours / 24;
      setText(hours % 24 + "h " + minutes % 60 + "m " + seconds % +60 + "s");
    }
  }

  public void setMode(boolean isCountdown) {
    this.isCountdown = isCountdown;
  }

  public void startTimer() {
    mStartTime = new Date();
    if (mEndTime == null || mEndTime.getTime() == 0L) {
      isCountdown = false;
    }
    runnable.run();
  }

  public void stopTimer() {
    mStophandler = true;
    handler.removeCallbacks(runnable);
  }

  public void setEndTime(Long endTime) {
    mEndTime = new Date(endTime);
  }
}
