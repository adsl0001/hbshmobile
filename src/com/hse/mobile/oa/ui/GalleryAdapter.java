package com.hse.mobile.oa.ui;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import com.hse.mobile.oa.util.ImageCache;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

/*
 * Ïà²á×°ÅäÆ÷
 */
public class GalleryAdapter extends BaseAdapter {
	private Context context;
	private Bitmap[] images;

	public GalleryAdapter(Context c, Bitmap[] images) {
		this.context = c;
		this.images = images;
	}
	
	public GalleryAdapter(Context c, List<Bitmap> images) {
		this.context = c;
		this.images = new Bitmap[images.size()];
		this.images = images.toArray(this.images);
	}

	public int getCount() {
		return images.length;
	}

	public Object getItem(int position) {
		return images[position];
	}

	public long getItemId(int position) {
		return position;
	}

	public float getScale(boolean focused, int offset) {
		return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d("lg", "getView");
		AspectRatioImageView imageView = new AspectRatioImageView(context);
		imageView.setImageBitmap(images[position]);

		imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT
				,LayoutParams.WRAP_CONTENT));
		imageView.setPadding(3, 0, 3, 0);
		return imageView;
	}
}
