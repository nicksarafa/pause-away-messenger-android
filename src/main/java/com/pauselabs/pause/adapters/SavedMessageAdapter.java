package com.pauselabs.pause.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.pauselabs.R;
import com.pauselabs.pause.core.SavedPauseDataSource;
import com.pauselabs.pause.models.PauseBounceBackMessage;

import java.util.ArrayList;

/**
 * Saved Message Adapter is responsible for displaying saved Pause message in the navigation drawer fragment gridview
 */
public class SavedMessageAdapter extends BaseAdapter {

    private Context mContext;
    private SavedPauseDataSource mDatasource;
    private ArrayList<PauseBounceBackMessage> mItems = new ArrayList<PauseBounceBackMessage>();

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
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
            int size = (int) mContext.getResources().getDimension(R.dimen.saved_image_thumb_size);
            imageView.setLayoutParams(new GridView.LayoutParams(size, size));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        PauseBounceBackMessage savedMessage = mItems.get(position);

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4; // 1/4th the width/height of original, 1/16th num of pixels
        Bitmap original = BitmapFactory.decodeFile(savedMessage.getPathToImage(), options);
        BitmapDrawable drawableBitmap = new BitmapDrawable(mContext.getResources(), original);
        imageView.setImageDrawable(drawableBitmap);
        return imageView;
    }

    public void loadContent() {
        mDatasource.open();
        mItems = new ArrayList<PauseBounceBackMessage>(mDatasource.getAllSavedPauses());
        mDatasource.close();
    }

    public static class ViewHolder {
        private ImageView imageView;
    }
}
