package com.example.user.funserverproject;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.util.List;

/**
 * Created by user on 2017-12-27.
 */

public class GridViewAdapter extends BaseAdapter {

    private Context mContext;
    private List<Bitmap> images;

    public GridViewAdapter(Context c, List<Bitmap> images) {
        mContext = c;
        this.images = images;
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public Object getItem(int i) {

        return i;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ImageView imageView;

        if (convertView == null) {
            imageView = new ImageView(mContext);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(300,300));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8,20,8,8);
        } else {
            imageView = (ImageView) convertView;
        }
        imageView.setImageBitmap(images.get(position));
        Log.d("GridViewAdapter", "Accessed get view of position number "+position);
        return imageView;
    }
}
