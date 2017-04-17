package com.pauselabs.pause.core;

import android.content.Context;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/** CameraPreview is responsible for implementing the camera */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
  private static final String TAG = CameraPreview.class.getSimpleName();
  private SurfaceHolder mHolder;
  private Camera mCamera;
  private List<Camera.Size> mSupportedPreviewSizes;
  private List<Camera.Size> mSupportedPictureSizes;
  private Camera.Size mPreviewSize;
  private Camera.Size mPictureSize;

  public CameraPreview(Context context, Camera camera) {
    super(context);
    mCamera = camera;
    mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
    mSupportedPictureSizes = mCamera.getParameters().getSupportedPictureSizes();

    // Install a SurfaceHolder.Callback so we get notified when the
    // underlying surface is created and destroyed.
    mHolder = getHolder();
    mHolder.addCallback(this);
    // deprecated setting, but required on Android versions prior to 3.0
    mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
  }

  public void surfaceCreated(SurfaceHolder holder) {
    // The Surface has been created, now tell the camera where to draw the preview.
    try {
      mCamera.setPreviewDisplay(holder);
      mCamera.startPreview();
    } catch (IOException e) {
      Log.d(TAG, "Error setting camera preview: " + e.getMessage());
    }
  }

  public void surfaceDestroyed(SurfaceHolder holder) {
    // empty. Take care of releasing the Camera preview in your activity.
  }

  public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
    // If your preview can change or rotate, take care of those events here.
    // Make sure to stop the preview before resizing or reformatting it.

    if (mHolder.getSurface() == null) {
      // preview surface does not exist
      Log.v(TAG, "surface does not exist!");
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
    mCamera.setDisplayOrientation(90);

    Camera.Parameters parameters = mCamera.getParameters();

    List<String> focusModes = parameters.getSupportedFocusModes();
    if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH
        && focusModes.contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
      parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
    }
    //        if (focusModes.contains(Camera.Parameters.FOCUS_MODE_AUTO)) {
    //            // Autofocus mode is supported
    //            // set the focus mode
    //            params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
    //        }

    parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
    parameters.setPictureSize(mPictureSize.width, mPictureSize.height);

    // start preview with new settings
    try {
      // set camera params
      mCamera.setParameters(parameters);

      mCamera.setPreviewDisplay(mHolder);
      mCamera.startPreview();

    } catch (Exception e) {
      Log.d(TAG, "Error starting camera preview: " + e.getMessage());
    }
  }

  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
    final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
    setMeasuredDimension(width, height);

    if (mSupportedPreviewSizes != null) {
      mPreviewSize = getBestAspectPreviewSize(90, width, height, mSupportedPreviewSizes, 0.0d);
      mPictureSize = getBestAspectPreviewSize(90, width, height, mSupportedPictureSizes, 0.0d);
    }
  }

  public static Camera.Size getBestAspectPreviewSize(
      int displayOrientation, int width, int height, List<Camera.Size> sizes, double closeEnough) {
    double targetRatio = (double) width / height;
    Camera.Size optimalSize = null;
    double minDiff = Double.MAX_VALUE;

    if (displayOrientation == 90 || displayOrientation == 270) {
      targetRatio = (double) height / width;
    }

    Collections.sort(sizes, Collections.reverseOrder(new SizeComparator()));

    for (Camera.Size size : sizes) {
      double ratio = (double) size.width / size.height;

      if (Math.abs(ratio - targetRatio) < minDiff) {
        optimalSize = size;
        minDiff = Math.abs(ratio - targetRatio);
      }

      if (minDiff < closeEnough) {
        break;
      }
    }

    return (optimalSize);
  }

  private static class SizeComparator implements Comparator<Camera.Size> {
    @Override
    public int compare(Camera.Size lhs, Camera.Size rhs) {
      int left = lhs.width * lhs.height;
      int right = rhs.width * rhs.height;

      if (left < right) {
        return (-1);
      } else if (left > right) {
        return (1);
      }

      return (0);
    }
  }
}
