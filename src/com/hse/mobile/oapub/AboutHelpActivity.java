package com.hse.mobile.oapub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.LoginPortalService;
import com.hse.mobile.oa.business.MessageKey;
import com.hbsh.beta.R;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

public class AboutHelpActivity extends BaseActivity {
	TextView codeTextView,updateTextView;
	ViewGroup updateview,functionview,helpview;
	String basecode=null;
	String updatecode=null;
	private static final int PortalOk =1;
	Handler myHandler;
	private Thread downLoadThread ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_about_help);
		codeTextView=(TextView)this.findViewById(R.id.aboutcode);
		updateview=(ViewGroup)this.findViewById(R.id.about_setting1);
		updateview.setOnClickListener(onupdate);
		functionview=(ViewGroup)this.findViewById(R.id.about_setting2);
		functionview.setOnClickListener(onfunction);
		helpview=(ViewGroup)this.findViewById(R.id.about_setting3);
		helpview.setOnClickListener(onhelp);
		updateTextView=(TextView)findViewById(R.id.updatedata);
		/*�µĸ����߼�
		myHandler=new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
				case PortalOk:
					init();
				}
			}
		};
		*/
		init();
	}
	//�޸ĸ�����ʾ����Ϣ
	public void init(){
		//���ذ汾��
		PackageManager pm=getPackageManager();
		PackageInfo nPackageInfo;
		try {
			nPackageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
			basecode=nPackageInfo.versionName;
		} 
		catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		String newbasecode="�汾��"+basecode;
		codeTextView.setText(newbasecode);
		updatecode=Constant.APP_VERSION_RECIVE;
		if(basecode.equals(updatecode)){
			updateTextView.setText("�������°汾");
		}
		else{
			updateTextView.setText("�����°汾����������");
		}
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.about_help, menu);
		return true;
	}
	/*�µĸ����߼�
	//��ȡ�������汾��Ϣ
	public void checkupdate(){
		new LoginPortalTask().execute(Constant.User_name,Constant.User_imei,Constant.User_mac);
	}
	//�첽���ð�������¼��ȡ�汾��Ϣ
	public class LoginPortalTask extends AsyncTask<String, Integer, Boolean>{
		@Override
		protected Boolean doInBackground(String... params) {
			// TODO Auto-generated method stub
			if(params.length<2){
				return null;
			}
			try{
				return LoginPortalService.loginPortal(params[0], params[1],params[2]);
			}
			catch (Exception e) {
				// TODO: handle exception
				Log.e("RestService","hsemobile",e);
			}
			return null;
		}
		protected void onPostExecute(Boolean result) {
			onLoginPortalResult(result);
		}
	}
	//�����ȡ����Ϣ ���޸ĸ�����ʾ��Ϣ
	private void onLoginPortalResult(Boolean result){
		if(result == null){
		}else if(result){
			Message myMessage=new Message();
			myMessage.what=PortalOk;
			myHandler.sendMessage(myMessage);
		}else{
		}
	}
	*/
	//�汾����
	OnClickListener onupdate=new OnClickListener() {
		public void onClick(View v) {
			//���µķ���
			
			Log.i("test","basecode:"+basecode);
			Log.i("test","updatecode:"+updatecode);
			if(basecode.equals(updatecode)){
				Log.i("test","�汾����");
				Toast toast=Toast.makeText(getApplicationContext(), "�������°汾", Toast.LENGTH_SHORT); 
				toast.show();
			}
			else{
				Log.i("test","gengxin");
				//���·���Ĵ���
				update();
			}
		}
	};
	OnClickListener onfunction=new OnClickListener() {
		public void onClick(View v) {
			//��ת���汾˵��
			String url=Constant.URL_VERSION_DESCRIPTION;
			Intent intent=new Intent(AboutHelpActivity.this,CondovaActivity.class);
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "����˵��", true);
		}
	};
	OnClickListener onhelp=new OnClickListener() {
		public void onClick(View v) {
			String helpUrl=Constant.URL_HELP;
//			String helpUrl="http://221.195.72.41:81/help/help1.html";
			Intent intent = new Intent(AboutHelpActivity.this, CondovaActivity.class);
			intent.putExtra(MessageKey.URL, helpUrl);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "����", true);
		}
	};
	//���µķ���
	public void update(){
		new AlertDialog.Builder(this).setMessage("�ƶ��칫ƽ̨�����°汾������").setPositiveButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				downloadApk();
			}
		}).show();
	}
	//����
	public void downloadApk(){
		showProgressDialog("", "�������ء���");
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	//mdownApkRunnable
	public Runnable mdownApkRunnable= new Runnable() {
		public void run() {
			try{
				URL downloadUrl= new URL(Constant.URL_APK_DOWNLOAD);
				HttpURLConnection conn = (HttpURLConnection)downloadUrl.openConnection();
				conn.connect();
				int length=conn.getContentLength();
				InputStream is=conn.getInputStream();
				File file=new File(Constant.SDCARD_PATH);
				if(!file.exists()){
					file.mkdir();
				}
				Log.i("test","��ʼ����");
				String apkfile=Constant.SDCARD_PATH+"hse_mobile_oa.apk";
				File apkFile=new File(apkfile);
				FileOutputStream fos=new FileOutputStream(apkFile);
				byte []buf=new byte[1024];
				int count=0;
				float jindu=0.0f;
				do{
					int numread=is.read(buf);
					count+=numread;
		            //���½���
			        if(numread <= 0){ 
			         break;
			         }
			        fos.write(buf,0,numread); 
				}
				while(true);
				fos.close();
				is.close();
				installApk();
			}
			catch (Exception e) {
				// TODO: handle exception
			}
		}
	};
	public void installApk() {
		String newApkFile=Constant.SDCARD_PATH+"hse_mobile_oa.apk";
		File newapkFile=new File(newApkFile);
		if(!newapkFile.exists()){
			return;
		}
		hideProgressDialog();
		Intent i=new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://"+newapkFile.toString()), "application/vnd.android.package-archive");
		startActivity(i);
	}
}
