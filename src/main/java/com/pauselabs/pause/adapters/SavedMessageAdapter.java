package com.pauselabs.pause.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
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
        ViewHolder holder;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            convertView = LayoutInflater.from(mContext).inflate(R.layout.saved_item, parent, false);

            holder = new ViewHolder();
            holder.imageView = (ImageView) convertView.findViewById(R.id.savedThumbnail);
            holder.textView = (TextView) convertView.findViewById(R.id.savedTextThumbnail);

//            if(savedMessage.getPathToOriginal() != null){
//                imageView = new ImageView(mContext);
//                int size = (int) mContext.getResources().getDimension(R.dimen.saved_image_thumb_size);
//                imageView.setLayoutParams(new GridView.LayoutParams(size, size));
//                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
//                //imageView.setPadding(8, 8, 8, 8);
//            }
//            else{
//                textView = new TextView(mContext);
//            }
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }

        PauseBounceBackMessage savedMessage = mItems.get(position);

        if(savedMessage.getPathToOriginal() != null) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 12; // 1/4th the width/height of original, 1/16th num of pixels
            Bitmap original = BitmapFactory.decodeFile(savedMessage.getPathToImage(), options);
            BitmapDrawable drawableBitmap = new BitmapDrawable(mContext.getResources(), original);
            holder.imageView.setImageDrawable(drawableBitmap);

            holder.imageView.setVisibility(View.VISIBLE);
            holder.textView.setVisibility(View.GONE);
        }
        else{
            //Drawable placeholder = mContext.getResources().getDrawable(R.drawable.pause_sms_placeholder);
            holder.textView.setBackgroundResource(R.drawable.pause_sms_placeholder);
            holder.textView.setText(savedMessage.getMessage());

            holder.imageView.setVisibility(View.GONE);
            holder.textView.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

    public void loadContent() {
        mDatasource.open();
        mItems = new ArrayList<PauseBounceBackMessage>(mDatasource.getAllSavedPauses());
        mDatasource.close();
    }

    public static class ViewHolder {
        private ImageView imageView;
        private TextView textView;
    }

//    public class ImageGetter extends AsyncTask<File, Void, Bitmap> {
//        private ImageView iv;
//        public ImageGetter(ImageView v) {
//            iv = v;
//        }
//        @Override
//        protected Bitmap doInBackground(File... params) {
//            return decodeFile(params[0]);
//        }
//        @Override
//        protected void onPostExecute(Bitmap result) {
//            super.onPostExecute(result);
//            iv.setImageBitmap(result);
//        }
//    }
}
