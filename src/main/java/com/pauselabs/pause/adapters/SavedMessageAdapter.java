package com.pauselabs.pause.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pauselabs.R;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.models.PauseBounceBackMessage;
import java.util.ArrayList;

/**
 * Saved Message Adapter is responsible for displaying saved Pause message in the navigation drawer
 * fragment gridview
 */
public class SavedMessageAdapter extends BaseAdapter implements View.OnClickListener {

  private static final String TAG = SavedMessageAdapter.class.getSimpleName();

  private static final int TYPE_ITEM = 0;
  private static final int TYPE_BUTTON = 1;
  private static final int TYPE_MAX_COUNT = TYPE_BUTTON + 1;

  private Context mContext;
  private SavedPauseDataSource mDatasource;
  private ArrayList<PauseBounceBackMessage> mItems = new ArrayList<PauseBounceBackMessage>();
  private ImageLoader mImageLoader;

  private boolean isEditMode = false;

  public SavedMessageAdapter(Context context) {
    this.mContext = context;
    mDatasource = new SavedPauseDataSource(context);
  }

  @Override
  public int getCount() {
    return mItems.size();
  }

  @Override
  public Object getItem(int position) {
    return mItems.get(position);
  }

  @Override
  public long getItemId(int position) {
    return 0;
  }

  @Override
  public int getItemViewType(int position) {
    if (position == mItems.size() - 1) {
      return TYPE_BUTTON;
    } else {
      return TYPE_ITEM;
    }
  }

  @Override
  public int getViewTypeCount() {
    return TYPE_MAX_COUNT;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    ViewHolder holder;
    int type = getItemViewType(position);

    if (mImageLoader == null) {
      mImageLoader = ImageLoader.getInstance();
    }

    if (convertView == null) {
      holder = new ViewHolder();
      switch (type) {
        case TYPE_ITEM:
          convertView = LayoutInflater.from(mContext).inflate(R.layout.saved_item, parent, false);
          holder.thumbnailContainer =
              (FrameLayout) convertView.findViewById(R.id.saveThumbnailContainer);
          holder.imageView = (ImageView) convertView.findViewById(R.id.savedThumbnail);
          holder.textView = (TextView) convertView.findViewById(R.id.savedTextThumbnail);
          holder.editOverlayView = (RelativeLayout) convertView.findViewById(R.id.editOverlay);
          holder.favoriteBtn = (ImageButton) convertView.findViewById(R.id.favoriteBtn);
          holder.deleteBtn = (ImageButton) convertView.findViewById(R.id.deleteBtn);
          holder.favoriteBtn.setTag(position);
          holder.deleteBtn.setTag(position);
          holder.favoriteBtn.setOnClickListener(this);
          holder.deleteBtn.setOnClickListener(this);
          break;
        case TYPE_BUTTON:
          convertView =
              LayoutInflater.from(mContext).inflate(R.layout.saved_item_button, parent, false);
          final Button savedEditBtn = (Button) convertView.findViewById(R.id.savedEditBtn);
          savedEditBtn.setOnClickListener(this);
          break;
      }
      convertView.setTag(holder);
    } else {
      holder = (ViewHolder) convertView.getTag();
    }

    if (type == TYPE_ITEM) {
      PauseBounceBackMessage savedMessage = mItems.get(position);

      if (savedMessage.getPathToOriginal() != null) {
        //                final BitmapFactory.Options options = new BitmapFactory.Options();
        //                options.inSampleSize = 12; // 1/4th the width/height of original, 1/16th num of pixels
        //                Bitmap original = BitmapFactory.decodeFile(savedMessage.getPathToImage(), options);
        //                BitmapDrawable drawableBitmap = new BitmapDrawable(mContext.getResources(), original);
        //                holder.imageView.setImageDrawable(drawableBitmap);

        mImageLoader.displayImage("file:///" + savedMessage.getPathToImage(), holder.imageView);

        holder.imageView.setVisibility(View.VISIBLE);
        holder.textView.setVisibility(View.GONE);
      } else {
        //                Drawable placeholder = mContext.getResources().getDrawable(R.drawable.pause_sms_placeholder);
        holder.imageView.setImageDrawable(null);
        holder.textView.setText(savedMessage.getMessage());

        holder.imageView.setVisibility(View.VISIBLE);
        holder.textView.setVisibility(View.VISIBLE);
      }

      if (isEditMode) {
        holder.editOverlayView.setVisibility(View.VISIBLE);
      } else {
        holder.editOverlayView.setVisibility(View.GONE);
      }

      if (savedMessage.isFavorite()) {
        holder.favoriteBtn.setImageDrawable(
            mContext.getResources().getDrawable(R.drawable.ic_favorite));
        holder.thumbnailContainer.setBackground(
            mContext.getResources().getDrawable(R.drawable.save_thumbnail_background_favorite));

      } else {
        holder.favoriteBtn.setImageDrawable(
            mContext.getResources().getDrawable(R.drawable.ic_favorite_default));
        holder.thumbnailContainer.setBackground(
            mContext.getResources().getDrawable(R.drawable.save_thumbnail_background));
      }
    }

    return convertView;
  }

  public void loadContent() {
    mDatasource.open();
    updateItems();
    mDatasource.close();
  }

  private void updateItems() {
    mItems = new ArrayList<PauseBounceBackMessage>(mDatasource.getAllSavedPauses());

    if (mItems.size() > 0) {
      // add additional dummy item for Edit Button
      mItems.add(new PauseBounceBackMessage());
    }
  }

  private void deleteSavedPauseMessage(int position) {
    mDatasource.open();
    PauseBounceBackMessage savedPauseMessage = (PauseBounceBackMessage) getItem(position);
    mDatasource.deleteSavedPauseMessage(savedPauseMessage);
    updateItems();
    mDatasource.close();
    notifyDataSetChanged();
  }

  private void toggleFavorite(int position) {
    mDatasource.open();
    PauseBounceBackMessage savedPauseMessage = (PauseBounceBackMessage) getItem(position);
    savedPauseMessage.setFavorite(!savedPauseMessage.isFavorite());
    mDatasource.updateSavedPauseFavorite(savedPauseMessage);
    updateItems();
    mDatasource.close();
    notifyDataSetChanged();
  }

  private void toggleEditMode(View editButton) {
    if (isEditMode) {
      editButton.setBackground(mContext.getResources().getDrawable(R.drawable.btn_edit_saved));
      isEditMode = false;
    } else {
      editButton.setBackground(
          mContext.getResources().getDrawable(R.drawable.btn_edit_saved_clicked));
      isEditMode = true;
    }
    notifyDataSetChanged();
  }

  @Override
  public void onClick(View view) {
    int position;
    switch (view.getId()) {
      case R.id.savedEditBtn:
        toggleEditMode(view);
        break;
      case R.id.favoriteBtn:
        position = (Integer) view.getTag();
        toggleFavorite(position);
        break;
      case R.id.deleteBtn:
        position = (Integer) view.getTag();
        deleteSavedPauseMessage(position);
        break;
    }
  }

  public static class ViewHolder {
    private FrameLayout thumbnailContainer;
    private ImageView imageView;
    private TextView textView;
    private RelativeLayout editOverlayView;
    private ImageButton favoriteBtn;
    private ImageButton deleteBtn;
  }
}
