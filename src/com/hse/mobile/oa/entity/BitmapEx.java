package com.hse.mobile.oa.entity;

import android.graphics.Bitmap;

/*
 * λͼ��չ��
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
			desc = "�ϴ���...";
			break;	
		case STATUS_UPLOAD_FAIL:
			desc = "�ϴ�ʧ��";
			break;	
		case STATUS_UPLOAD_SUCCESS:
			desc = "�ϴ��ɹ�";
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
