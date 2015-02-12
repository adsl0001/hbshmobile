package com.hse.mobile.oapub;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.IRestServiceListener;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.RestServiceTask;
import com.hse.mobile.oa.entity.BitmapEx;
import com.hse.mobile.oa.entity.Department;
import com.hse.mobile.oa.ui.GalleryAdapter;
import com.hse.mobile.oa.util.BitmapUtil;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

/**
 * @ClassName 拍照页
 * @Description:
 * @author Administrator
 * @date 2013-10-15
 */
public class TakePhoto extends BaseActivity {
	Button btnOK, btnCancel;
	Button btnTakePhoto, btnSelectFile;
	Bitmap photo;
	//ImageView imgPhoto;
	Gallery gallery;
	TextView txtPhotoPage, txtPhotoStatus;
	Button   btnPhotoUpload, btnPhotoDel;
	ViewGroup layPhotoInfo;
	Boolean authority;
	boolean hasJiLiPermission = false;			//权限初始化
	boolean hasWeiZhangPermission = false;
	boolean hasgetdate=false;
	//隐藏背景用的
	LinearLayout backLinearLayout;
	List<BitmapEx> imageList = new ArrayList<BitmapEx>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.takephoto);
		init();
	}
	
	void init(){
		/*
		btnOK = (Button)this.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOkClickListener);
		
		btnCancel =  (Button)this.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(onCancelClickListener);
		*/
		super.setTitleBarRightButton("确定", onOkClickListener);
		//初始化背景
		backLinearLayout=(LinearLayout)this.findViewById(R.id.mylayout);
		btnTakePhoto = (Button)this.findViewById(R.id.btn_take_photo);
		btnTakePhoto.setOnClickListener(onTakePhotoClickListener);
		
		btnSelectFile = (Button)this.findViewById(R.id.btn_select_file);
		btnSelectFile.setOnClickListener(onSelectFileClickListener);
		
		gallery = (Gallery)this.findViewById(R.id.gallery_photo);
		gallery.setOnItemSelectedListener(onPhotoItemSelectListener);
		layPhotoInfo = (ViewGroup)this.findViewById(R.id.lay_photoinfo);
		txtPhotoPage = (TextView)this.findViewById(R.id.txt_photo_page);
		txtPhotoStatus = (TextView)this.findViewById(R.id.txt_photo_status);
		btnPhotoUpload = (Button)this.findViewById(R.id.btn_photo_upload);
		btnPhotoUpload.setOnClickListener(onPhotoUploadClickListener);
		btnPhotoDel = (Button)this.findViewById(R.id.btn_photo_del);
		btnPhotoDel.setOnClickListener(onPhotoDelClickListener);
		authority=isOK();
	}
	
	public void onResumeFromExist(){
		super.setTitleBarRightButton("确定", onOkClickListener);
	}
	
	OnClickListener  onTakePhotoClickListener = new OnClickListener(){
		public void onClick(View v) {
			//权限控制
			if(hasgetdate){
				if(isOK()){
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					startActivityForResult(intent, 1);
				}
				else Toast.makeText(TakePhoto.this, "您没有该权限!", Toast.LENGTH_SHORT).show();
			}
			else{
				//加载权限信息失败! 您没有该权限
				Toast.makeText(TakePhoto.this, "加载权限信息失败!", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	OnClickListener  onSelectFileClickListener = new OnClickListener(){
		public void onClick(View v) {
			//权限控制
			if(hasgetdate){
				if(isOK()){
					Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
					intent.setType("image/*");
					startActivityForResult(intent, 2);
				}
				else Toast.makeText(TakePhoto.this, "您没有该权限!", Toast.LENGTH_SHORT).show();
			}
			else{
				//加载权限信息失败! 您没有该权限
				Toast.makeText(TakePhoto.this, "加载权限信息失败!", Toast.LENGTH_SHORT).show();
			}
			
		}
	};
	
	OnClickListener  onPhotoUploadClickListener = new OnClickListener(){
		public void onClick(View v) {
			int photoIndex = gallery.getSelectedItemPosition();	
			uploadImage(imageList.get(photoIndex));
			setPhotoPageInfo(photoIndex);
		}
	};
	
	OnClickListener  onPhotoDelClickListener = new OnClickListener(){
		public void onClick(View v) {
			int photoIndex = gallery.getSelectedItemPosition();	
			String imageId = imageList.get(photoIndex).getId();
			imageList.remove(photoIndex);
			deletePhoto(imageId);
			refreshGallery();
			if(imageList.size() == 0){
				layPhotoInfo.setVisibility(View.GONE);
				backLinearLayout.setBackgroundResource(R.drawable.suishoupai);
			}
		}
	};
	
	OnItemSelectedListener onPhotoItemSelectListener = new OnItemSelectedListener(){

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			setPhotoPageInfo(arg2);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
	
		}
	};
	
	int getFirstImageNotUpload(){
		for(int i=0; i<imageList.size(); i++){
			BitmapEx image = this.imageList.get(i);
			if(image.getStatus() == BitmapEx.STATUS_NORMAL){
				return i;
			}
		}
		
		return -1;
	}
	
	
	/** 上传所有照片
	 * @Description:
	 * void
	 * @throws 
	 * @author 刘万芬
	 */
	void uploadImages(){
		for(BitmapEx image : this.imageList){
			if(image.getStatus() == BitmapEx.STATUS_NORMAL){
				uploadImage(image);
			}
		}
	}
	
	/*
	 * 上传图片
	 */
	void uploadImage(final BitmapEx image){
		image.setStatus(BitmapEx.STATUS_UPLOADING);
		
		String url = Constant.URL_IMAGE;
		new RestServiceTask(new IRestServiceListener(){
			public void onRestServiceResult(JSONObject result) {
				try
				{
					if(result != null){
						HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
						String imageId = resultData.get("imageId").toString();
						Log.i("uploadImage success", imageId);
						
						image.setId(imageId);
						
						//更新状态
						image.setStatus(BitmapEx.STATUS_UPLOAD_SUCCESS);
					}else{
						image.setStatus(BitmapEx.STATUS_UPLOAD_FAIL);
					}
					
					if(imageList.get(gallery.getSelectedItemPosition()) == image){
						setPhotoPageInfo(gallery.getSelectedItemPosition());
					}
				}catch(Exception ex){
				}
			}
		})
		.execute(url, "post", BitmapUtil.Bitmap2Bytes(image.getBitmap()), "binary");
	}
	
	/*
	 * 设置相册页信息
	 */
	private void setPhotoPageInfo(int photoIndex){
		BitmapEx image = imageList.get(photoIndex);
		
		this.layPhotoInfo.setVisibility(View.VISIBLE);
		this.txtPhotoPage.setVisibility(View.VISIBLE);
		txtPhotoPage.setText(String.format("%d/%d", photoIndex+1, this.imageList.size()));
		
		if(image.getStatus() != BitmapEx.STATUS_NORMAL){
			txtPhotoStatus.setText(image.getStatusDescription());
			txtPhotoStatus.setVisibility(View.VISIBLE);
		}else{
			txtPhotoStatus.setVisibility(View.GONE);
		}
		btnPhotoDel.setVisibility(View.VISIBLE);
		if(image.getId() == null){
			if(image.getStatus() == BitmapEx.STATUS_NORMAL
				|| image.getStatus() == BitmapEx.STATUS_UPLOAD_FAIL){
				this.btnPhotoUpload.setVisibility(View.VISIBLE);
			}else{
				this.btnPhotoUpload.setVisibility(View.GONE);
			}

		}else{
			this.btnPhotoUpload.setVisibility(View.GONE);
		}
	}
	/*
	 * 删除服务器上的图片
	 */
	void deletePhoto(String imageId){
		String url = Constant.URL_IMAGE_DELETE + imageId;
		new RestServiceTask(null).execute(url, "delete");
	}
	
	void addPhoto(Bitmap image){
		BitmapEx imageEx = new BitmapEx(image, null);
		imageList.add(imageEx);
		//uploadImage(imageEx);		
		refreshGallery();
		gallery.setSelection(imageList.size()-1);
	}
	
	
	Bitmap[] getImageArray(){
		Bitmap[] images = new Bitmap[imageList.size()];
		for(int i=0; i<imageList.size(); i++){
			images[i] = imageList.get(i).getBitmap();
		}
		return images;
	}
	
	void refreshGallery(){
		GalleryAdapter adapter = new GalleryAdapter(this, getImageArray());
		gallery.setAdapter(adapter);
	}
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case 1:
			if(resultCode == Activity.RESULT_OK){
				photo = (Bitmap) data.getExtras().get("data");
				backLinearLayout.setBackgroundDrawable(null);
				//imgPhoto.setImageBitmap(photo);
				//Bitmap image = ((HseApplication)getApplication()).getBitmap();
				addPhoto(photo);
			}
			break;	
		case 2:
			if(resultCode == Activity.RESULT_OK){
				ContentResolver resolver = getContentResolver();
				Uri uri = data.getData(); 
				try {
					photo = MediaStore.Images.Media.getBitmap(resolver, uri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} 
				//imgPhoto.setImageBitmap(photo);
				if(photo != null){
					backLinearLayout.setBackgroundDrawable(null);
					addPhoto(photo);
				}
			}
			break;	
		}
		super.onActivityResult(requestCode, resultCode, data);
	};
	
	OnClickListener  onOkClickListener = new OnClickListener(){
		public void onClick(View v) {
			//权限控制
			if(hasgetdate){
				if(isOK()){
					if(imageList.size() == 0){
						Toast.makeText(TakePhoto.this, "请选择照片!", Toast.LENGTH_SHORT).show();
						return;
					}
					
					int photoIndex = getFirstImageNotUpload();
					if(photoIndex != -1){
						uploadImage(imageList.get(photoIndex));
						gallery.setSelection(photoIndex);
						Toast.makeText(TakePhoto.this, "请完成照片上传!", Toast.LENGTH_SHORT).show();
						return;
					}
					
					//intent.putExtra("photo", photo);

					//setResult(Activity.RESULT_OK, intent);
					//finish();
					((HseApplication)getApplication()).session.put("images", imageList);
					Intent intent = new Intent(TakePhoto.this, EasyTicketTypeSelect.class);
					navigate(intent, "随手拍", true);
				}
				else Toast.makeText(TakePhoto.this, "您没有该权限!", Toast.LENGTH_SHORT).show();
			}
			else{
				//加载权限信息失败! 您没有该权限
				Toast.makeText(TakePhoto.this, "加载权限信息失败!", Toast.LENGTH_SHORT).show();
			}
		}
	};
	
	OnClickListener  onCancelClickListener = new OnClickListener(){
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	};
	//权限控制
	public Boolean isOK(){
		Boolean isok=false;
		String url = Constant.URL_AUTHOR + ((HseApplication)this.getApplicationContext()).getLoginUserId();
		showProgressDialog("", "正在加载...");
		new RestServiceTask(serviceListener).execute(url);
		if((!hasWeiZhangPermission)&&(!hasWeiZhangPermission)){
			isok=false;
		}
		else isok=true;
		return isok;
	}
	IRestServiceListener serviceListener = new IRestServiceListener(){
		public void onRestServiceResult(JSONObject result) {
			hideProgressDialog();
			try
			{
				if(result != null){
					HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
					hasJiLiPermission = resultData.get("attach").toString().equals("1");
					hasWeiZhangPermission = resultData.get("ticket").toString().equals("1");	
					hasgetdate=true;
				}else{
					Toast.makeText(TakePhoto.this, "加载权限信息失败!", Toast.LENGTH_SHORT).show();
					hasgetdate=false;
				}
			}catch(Exception ex){
				
			}
		}
	};
}
