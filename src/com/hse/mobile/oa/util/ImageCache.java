package com.hse.mobile.oa.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * 图片缓存
 */
public class ImageCache {
	private static String cachePath;
	public static final int IMAGE_DOWNLOAD_FINISH = 63001;
	public static final int IMAGE_DOWNLOAD_FAIL = 63000;
	
	private static HashMap<String, Bitmap> imageMap = new HashMap<String, Bitmap>();
	
	/*
	 * 获取缓存路径
	 */
	private static String getCachePath(){
		if(cachePath == null){
			Log.i("sd_path", Environment.getExternalStorageDirectory().getAbsolutePath());
			Log.i("data_path", Environment.getDataDirectory().getAbsolutePath());
			String path;
			boolean sdcardExits =  Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
			if(sdcardExits){
				path = Environment.getExternalStorageDirectory().getAbsolutePath();
			}else{
				path = Environment.getDataDirectory().getAbsolutePath();
			}
			cachePath = path + "/hse_images/";
			File cache = new File(cachePath);
			if(!cache.exists()){
				cache.mkdir();
			}
		}
		 
		return cachePath;
	}
	
	private static String urlToFileName(String url){
		String fileName = url.substring(url.lastIndexOf('/') + 1);
		return fileName;
	}
	
	private static String getCacheFilePath(String url){
		return getCachePath() + urlToFileName(url);
	}
	
	/*
	 * 是否有该文件
	 */
	public static boolean hasImage(String url){
		try{
			if(imageMap.containsKey(url)){
				return true;
			}
			
			String filePath = getCachePath() + urlToFileName(url);
			return new File(filePath).exists();
		}catch(Exception ex){
			Log.e("hseoa", "ImageCache", ex);
			return false;
		}
	}
	
	public static void putImage(String url, Bitmap image){
		try {
			if(image == null)
				return;
			
			imageMap.put(url, image);
			
			String filePath = getCacheFilePath(url);
			File file = new File(filePath);
			file.createNewFile();
			FileOutputStream fos = new FileOutputStream(file);
			image.compress(Bitmap.CompressFormat.PNG, 100, fos);  
			fos.flush();
			fos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static Bitmap getImage(String url){
		try{
			if(imageMap.containsKey(url)){
				return imageMap.get(url);
			}
			
			String filePath = getCacheFilePath(url);
			Bitmap image = BitmapFactory.decodeFile(filePath);
			return image;
		}catch(Exception ex){
			return null;
		}
	}
	
	/*
	 * 异步下载图片,下载完后发送消息通知
	 * url 图片地址
	 * handler 消息处理器
	 */
	public static void asyncDownloadImage(final String url, final Handler handler){

		ImageDownloadTask imageTask = new ImageDownloadTask(){
			@Override
			protected void onPostExecute(Bitmap[] result) {
				Message msg = Message.obtain();
				if(result !=null && result.length>0 && result[0]!=null){
					 msg.what = IMAGE_DOWNLOAD_FINISH;
					 msg.obj = result[0];
					 
					 putImage(url, result[0]);
				}else{
					msg.what = IMAGE_DOWNLOAD_FAIL;
				}
				
				handler.sendMessage(msg);
			}
		};
		
		imageTask.execute(url);
	}
	
	public static void asyncDownloadImages(final String[] url, final Handler handler){

		ImageDownloadTask imageTask = new ImageDownloadTask(){
			@Override
			protected void onPostExecute(Bitmap[] result) {
				Message msg = Message.obtain();
				if(result !=null && result.length>0){
					 msg.what = IMAGE_DOWNLOAD_FINISH;
					 msg.obj = result;
					 
					 for(int i=0; i<result.length; i++){
						 putImage(url[i], result[i]);
					 }
				}else{
					msg.what = IMAGE_DOWNLOAD_FAIL;
				}
				
				handler.sendMessage(msg);
			}
		};
		
		imageTask.execute(url);
	}
}
