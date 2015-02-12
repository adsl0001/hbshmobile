package com.hse.mobile.oa.ui;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

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
 * 网络相册装配器
 */
public class InternetGalleryAdapter extends BaseAdapter {
	private Context context;
	private String[] imageUrls;
	private Bitmap[] images;

	public InternetGalleryAdapter(Context c, String[] imageUrls) {
		this.context = c;
		this.imageUrls = imageUrls;
		images = new Bitmap[imageUrls.length];
		//test
		/*
		Resources res = context.getResources();
		images[0] = BitmapFactory.decodeResource(res,
				R.drawable.photo_1);
		images[1] = BitmapFactory.decodeResource(res,
				R.drawable.photo_2);	
		images[2] = BitmapFactory.decodeResource(res,
				R.drawable.photo_3);
		images[3] = BitmapFactory.decodeResource(res,
				R.drawable.photo_4);
		images[4] = BitmapFactory.decodeResource(res,
				R.drawable.photo_1);
				*/
		/*
		Resources res = context.getResources();
		for (int i = 0; i < imageUrls.length; i++) {
			images[i] = BitmapFactory.decodeResource(res,
					R.drawable.ic_launcher);
		}*/

		// TypedArray a = c.obtainStyledAttributes(R.styleable.Gallery);
		/* 取得Gallery属性的Index id */
		// mGalleryItemBackground =
		// a.getResourceId(R.styleable.Gallery_android_galleryItemBackground,
		// 0);
		// 让对象的styleable属性能够反复使用
		// a.recycle();

		ImageLoadTask picLoadTask = new ImageLoadTask();
		picLoadTask.execute();

	}

	public int getCount() {
		return imageUrls.length;
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

	// 很费时的io操作,用异步线程处理
	class ImageLoadTask extends AsyncTask<String, Integer, String> {

		protected String doInBackground(String... params) {
			// 这里采用一次性全部记载的方法,适合少量图片
			for (int i = 0; i < imageUrls.length; i++) {
				try {
					// 从网络获取图片
					String url = imageUrls[i];
					Bitmap image = null;
					
					if(ImageCache.hasImage(url)){
						image = ImageCache.getImage(url);
					}
					
					if(image == null){
						URL aryURI = new URL(url);
						URLConnection conn = aryURI.openConnection();
						conn.connect();
						InputStream is = conn.getInputStream();
						image = BitmapFactory.decodeStream(is);
						is.close();
					}

					images[i] = image;
					handler.post(mUpdateResults); // 发布消息让主线程接收,实现异步线程和主线程的通信

				} catch (Exception e) {
					// 处理异常,图片加载失败
					Log.d("lg", e + "");
				}
			}
			return null;
		}

	}

	final Handler handler = new Handler();

	final Runnable mUpdateResults = new Runnable() {
		public void run() {
			notifyDataSetChanged(); // 不能直接在AsyncTask中调用,因为不是线程安全的
		}
	};
}
