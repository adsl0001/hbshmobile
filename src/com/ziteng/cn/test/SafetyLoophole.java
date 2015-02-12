package com.ziteng.cn.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore.Images.Media;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oapub.BaseActivity;
import com.hse.mobile.oapub.CondovaActivity;




public class SafetyLoophole extends BaseActivity implements Callback {
	SurfaceView cameraView;
	SurfaceHolder surfaceHolder;
	Camera camera;
	Button takephoto,nextype;
	public static final int LARGEST_WIDTH=320;
	public static final int LARGEST_HEIGTH=320;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_safety_loophole);
		
		takephoto=(Button)findViewById(R.id.btn_takephoto);
		nextype=(Button)findViewById(R.id.btn_next);
		takephoto.setOnClickListener(OnPhoto);
		nextype.setOnClickListener(onNext);
		
		cameraView=(SurfaceView)findViewById(R.id.cameraView);
		surfaceHolder=cameraView.getHolder();
		surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		surfaceHolder.addCallback(this);
		cameraView.setFocusable(true);
	}
	
	
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
		// TODO Auto-generated method stub
		camera.startPreview();
	}
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		camera=Camera.open();
		try {
			camera.setPreviewDisplay(holder);    
	        Camera.Parameters parameters = camera.getParameters();  
	        //取景旋转
	        if (this.getResources().getConfiguration().orientation   
	            !=  Configuration.ORIENTATION_LANDSCAPE)  
	       {  
	            camera.setDisplayOrientation(90);  
	        }  
	        else   
	        {  
	             camera.setDisplayOrientation(0);   
	  
	        } 
	        
	        List<Camera.Size> previewSizes=parameters.getSupportedPreviewSizes();
	        //获取设备支持预览的值
	        if(previewSizes.size()>1){
	        	Iterator<Camera.Size> cei=previewSizes.iterator();
	        	while(cei.hasNext()){
	        		Camera.Size aSize=cei.next();
	        	}
	        }
	        
	      camera.setParameters(parameters);  
		} catch (Exception e) {
			// TODO: handle exception
			camera.release();
		}
		camera.startPreview();
	}
	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub
		camera.stopPreview();
		camera.release();
	}
	//拍照的回调函数
		private Camera.PictureCallback picture = new Camera.PictureCallback(){
			
			public void onPictureTaken(byte[] arg0, Camera arg1) {
				// TODO Auto-generated method stub
				Uri imageFileUri=getContentResolver().insert(Media.EXTERNAL_CONTENT_URI, new ContentValues());
				Log.i("test","保存路径"+imageFileUri);
				
				try {
		//			Bitmap bitmap=BitmapFactory.decodeByteArray(arg0, 0, arg0.length);
		//			MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "11", "11");
					OutputStream imageFileOS=getContentResolver().openOutputStream(imageFileUri);
					imageFileOS.write(arg0);
					imageFileOS.flush();
					imageFileOS.close();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				camera.startPreview();
				
			}
		};
		OnClickListener OnPhoto=new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				 camera.takePicture(null, null, picture);
			}
		};
		OnClickListener onNext=new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent safetylooper=new Intent(SafetyLoophole.this, CondovaActivity.class);
				safetylooper.putExtra(MessageKey.URL, Constant.URL_ROOT+"/SecurityManager/safehazard/input");
				safetylooper.putExtra(MessageKey.REUSEABLE, false);
				navigate(safetylooper, "安全隐患", true);
			}
		};
}