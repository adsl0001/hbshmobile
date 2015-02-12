package com.hse.mobile.oa.util;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

/*
 * ͼƬ�첽��������
 */
public class ImageDownloadTask extends AsyncTask<String, Integer, Bitmap[]>{
		private Bitmap[] images;
		
		private ImageDownloadListener imageDownloadListener;
		
		public Bitmap[] getImages() {
			return images;
		}
		
		public ImageDownloadTask(){
		}
		
		public ImageDownloadTask(ImageDownloadListener listener){
			this.imageDownloadListener = listener;
		}
		
		protected void onPostExecute(Bitmap[] result) {
			if(imageDownloadListener != null){
				imageDownloadListener.onImageDownloadResult(result);
			}
		}
		
		protected Bitmap[] doInBackground(String... params) {
			images = new Bitmap[params.length];
			
			// �������һ����ȫ�����صķ���,�ʺ�����ͼƬ
			for (int i = 0; i < params.length; i++) {
				try {
					// �������ȡͼƬ
					String url = params[i];
					Bitmap image = null;
					
					if(ImageCache.hasImage(url)){
						image = ImageCache.getImage(url);
					}
					
					if(image == null){
						URL aryURI = new URL(NetUtil.encodeURI(url));
						URLConnection conn = aryURI.openConnection();
						conn.connect();
						InputStream is = conn.getInputStream();
						image = BitmapFactory.decodeStream(is);
						is.close();
					}
					
					images[i] = image;
					
					Log.i("ImageDownloadTask " + (image==null?"fail":"success"), url);
				} catch (Exception e) {
					// �����쳣,ͼƬ����ʧ��
					images[i] = null;
					Log.d("ImageDownloadTask fail[" + params[i] + "]:", e + "");
				}
			}
			
			return images;
		}
		
		public interface ImageDownloadListener{
			public void onImageDownloadResult(Bitmap[] result);
		}
}
