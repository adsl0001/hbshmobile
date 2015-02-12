package com.hse.mobile.oa.entity;

import android.graphics.Bitmap;

/*
 * 位图扩展类
 */
public class BitmapEx {

	public BitmapEx(Bitmap bitmap, String id) {
		super();
		this.bitmap = bitmap;
		this.id = id;
		
	}
	public Bitmap getBitmap() {
		return bitmap;
	}
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	
	public String getStatusDescription() {
		String desc = "";
		switch(status){
		case STATUS_NORMAL:
			break;
		case STATUS_UPLOADING:
			desc = "上传中...";
			break;	
		case STATUS_UPLOAD_FAIL:
			desc = "上传失败";
			break;	
		case STATUS_UPLOAD_SUCCESS:
			desc = "上传成功";
			break;			
		}
		
		return desc;
	}
	
	private Bitmap bitmap;
	
	private String id;
	
	private int status;
	
	public static final int STATUS_NORMAL = 0;
	public static final int STATUS_UPLOADING = 1;
	public static final int STATUS_UPLOAD_FAIL = 2;
	public static final int STATUS_UPLOAD_SUCCESS = 3;
	
}
