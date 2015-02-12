package com.hse.mobile.oa.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

/*
 * 图片缓存
 */
public class ImageCache {
	private static String cachePath;
	
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
			String filePath = getCachePath() + urlToFileName(url);
			return new File(filePath).exists();
		}catch(Exception ex){
			Log.e("hseoa", "ImageCache", ex);
			return false;
		}
	}
	
	public static void putImage(String url, Bitmap image){
		try {
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
			String filePath = getCacheFilePath(url);
			Bitmap image = BitmapFactory.decodeFile(filePath);
			return image;
		}catch(Exception ex){
			return null;
		}
	}
}
