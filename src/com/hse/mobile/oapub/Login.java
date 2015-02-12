package com.hse.mobile.oapub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.GetBindStatusService;
import com.hse.mobile.oa.business.LoginPortalService;
import com.hse.mobile.oa.business.LoginService;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.OnGetBindStatus;
import com.hse.mobile.oa.util.CookieUtil;
import com.hse.mobile.oa.util.RestClient;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ��½
 */
public class Login extends BaseActivity {
	public static final int LoginPortalFalse=30*1000;
	private Context ctx;
	private Button btnLogin;
	private EditText txtAccount, txtPassword;
	//�û���
	private String account;
	//����
	private String password;
	//����ϵͳ
	private String os="android";
	//�ֻ��ͺ�
	private String ostype;
	//mac��ַ
	private String mac;
	//������
	private String identify;
	//ip��ַ
	private String ip;
	//�����汾��
	private String versionCode;
	
	Handler myHandler;
	private Thread downLoadThread ;
	private static final int DOWN_UPDATE =1;
	private static final int DOWN_OVER = 2;
	//��������֤���
	private static final int AppLoginCheckOK=3;
	private static final int AppLoginCheckFaile=4;
	private static final int AppLoginCheckFaile2=5;
	private static final int AppLoginCheckFaile3=6;
	private static final int AppLoginCheckFaile4=7;
	//��¼���
	private static final int LoginOK=10;
	private static final int LoginFaile=11;
	private static final int LoginFaile1=12;
	private static final int LoginFaile2=13;
	private static final int LoginFaile3=14;
	//���µ�¼״̬
	private static final int refreshok=111;
	//ȡ������app
	private static final int refuseupdate=222;
	private String AppLoginCheckTips;
	//���ñ���
	private static final int MSG_SET_ALIAS=1102;
	//���ñ�ǩ
	private static final int MSG_SET_TAG=1103;
	//
	private static final int toupdate=219;
	//���´���
	private AlertDialog dlg;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		ctx=this;
		txtAccount =  (EditText) this.findViewById(R.id.txt_login_account);
		txtPassword =  (EditText) this.findViewById(R.id.txt_login_password);
		btnLogin = (Button) this.findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(onLoginClickListener);
		//��ȡ�豸��
		TelephonyManager tm=(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		identify=tm.getDeviceId();
		//��ȡmac��ַ 
		WifiManager wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo info=wifi.getConnectionInfo();
		mac=info.getMacAddress();
		//ip��ַ
		try{
			for(Enumeration<NetworkInterface>en=NetworkInterface.getNetworkInterfaces();en.hasMoreElements();){
				NetworkInterface intf=en.nextElement();
				for(Enumeration<InetAddress> enumIpAddr = intf  
                        .getInetAddresses(); enumIpAddr.hasMoreElements();){
					 InetAddress inetAddress = enumIpAddr.nextElement();  
                     if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()) {
                    	 ip=inetAddress.getHostAddress().toString();
                     }
				}
			}
		}catch(SocketException e){
			Log.i("test", "��ȡip��ַʧ��");
		}
		//��ȡ�ֻ��ͺ�
		ostype=android.os.Build.MODEL;
		ostype=ostype.replaceAll(" ", "");
		//��ȡ�汾��
		PackageManager pm=getPackageManager();
		PackageInfo nPackageInfo;
		try {
			nPackageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
			versionCode=nPackageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//�����˺�����
		SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
		account=sp.getString("userName", "");
		password=sp.getString("password", "");
		boolean savePassword = Setting.getBooleanConfig(Login.this, MessageKey.CONFIG_SAVEPASSWORD, true);
		boolean autologin=Setting.getBooleanConfig(Login.this, MessageKey.CONFIG_AUTOLOGIN, true);
		Log.i("test", "savePassword:"+savePassword+"             autologin"+autologin);
		txtAccount.setText(account);
		if(savePassword){
			txtPassword.setText(password);
		}
		//�Զ���¼
		if(autologin&&!account.isEmpty()&&!password.isEmpty()){
			AppLoginCheck();
		}
		myHandler=new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
				
				case AppLoginCheckOK:
					Log.i("test", "���������ɹ�");
					login();
					return;
				case AppLoginCheckFaile:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage("�������!").setPositiveButton("ȷ��", null).show();
					return;
				case AppLoginCheckFaile2:
					hideProgressDialog();
					Log.i("test", "���������ʧ�ܣ�"+AppLoginCheckTips);
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage(AppLoginCheckTips).setPositiveButton("ȷ��", null).show(); 
					return;
				case AppLoginCheckFaile3:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage(AppLoginCheckTips).setPositiveButton("ȷ��", null).show(); 
					return;
				case AppLoginCheckFaile4:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage(AppLoginCheckTips).setPositiveButton("ȷ��", null).show(); 
					return;
				case LoginFaile:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage("��¼��֤����,������������!").setPositiveButton("ȷ��", null).show();
					return;
				case LoginOK:
					//��¼��֤�ɹ� ˢ�µ�¼״̬
					refreshLogin();
					return;
				case LoginFaile1:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage("��������˺Ż����벻��ȷ������������!").setPositiveButton("ȷ��", null).show();
					return;
				case LoginFaile2:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage("�����˺��Ѿ�������!").setPositiveButton("ȷ��", null).show();
					return;	
				case LoginFaile3:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage("�ʺźϷ������޸��ʺŵ�¼��Ϣʧ��!").setPositiveButton("ȷ��", null).show();
					return;	
				case toupdate:
					appupdate();
					return;
				case refuseupdate:
					//������������ ����tag��alis
					if(Setting.isPushEnable(ctx)){
						JPushInterface.resumePush(ctx);
						JPushInterface.setAlias(ctx, account, aliasCallback);
						String tag="hse_hbsh";
						Set<String>jpushTag=new LinkedHashSet<String>();
						jpushTag.add(tag);
						JPushInterface.setTags(getApplicationContext(), jpushTag, tagCallback);
					}
					((HseApplication)getApplicationContext()).setIsLogged(true);
					((HseApplication)getApplicationContext()).setLoginUserId(account);
					//����Main
					hideProgressDialog();
					checkguide();
					return;
				case refreshok:
					//��¼�ɹ����ø��ֲ��� 
					Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
					editor.putString("userName", account);
					editor.putString("password", password);
					editor.commit();
					Constant.setUser_imei(identify);
					Constant.setUser_mac(mac);
					Constant.setUser_name(account);
					//Ȼ�����app������֤  
					//Ȼ�����app������֤  ָ����һ����10�����
					if(timetoupdate()&checkupdate()){
						hideProgressDialog();
						showUpdateView();
						return;
					}
					else{
						//Ȼ�������������� ����tag��alis
						if(Setting.isPushEnable(ctx)){
							JPushInterface.resumePush(ctx);
							JPushInterface.setAlias(ctx, account, aliasCallback);
							String tag="hse_hbsh";
							Set<String>jpushTag=new LinkedHashSet<String>();
							jpushTag.add(tag);
							JPushInterface.setTags(getApplicationContext(), jpushTag, tagCallback);
						}
						((HseApplication)getApplicationContext()).setIsLogged(true);
						((HseApplication)getApplicationContext()).setLoginUserId(account);
						checkguide();
						return;
					}
				}
			}
		};
	}
	//��ѯ�û���Ϣ�б�
	private void AppLoginCheck(){
		showProgressDialog("","���ڽ��е�¼");
		new Thread(appRunnable).start();
	}
	//��¼����
	private void login(){
		Log.i("test", "��¼");
		new Thread(loginRunnable).start();
	}
	//ˢ�µ�¼״̬
	private void refreshLogin(){
		Log.i("test", "ˢ�µ�¼״̬");
		new Thread(refreshLoginStatus).start();
	}
	//������
	private Boolean checkupdate(){
		Log.i("test","���汾����");
		Log.i("test", "��ǰ�汾��"+versionCode);
		String versionrecive=Constant.APP_VERSION_RECIVE;
		Log.i("test", "�������汾��"+versionrecive);
		if(versionCode!=null&&!"".equals(versionCode)&&!"".equals(versionrecive)&&!versionCode.equals(versionrecive)){
			return true;
//			hideProgressDialog();
//			new AlertDialog.Builder(this).setTitle("��ܰ��ʾ").setMessage("�ƶ��칫ƽ̨�����°汾������")
//			.setPositiveButton("����", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					showProgressDialog("", "�������ء���");
//					new Thread(downloadApk).start();
//				}
//			}).show();
		}
		return false;
	}
	//�����¼��ť
	OnClickListener onLoginClickListener=new OnClickListener() {
		public void onClick(View v) {
			account = txtAccount.getText().toString().trim();
			password = txtPassword.getText().toString().trim();
			if(account.equals("")||account==null){
				new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage("�������˺�").setPositiveButton("ȷ��", null).show(); 
			}
			else if(password.equals("")||password==null){
				new AlertDialog.Builder(Login.this).setTitle("��ܰ��ʾ").setMessage("����������").setPositiveButton("ȷ��", null).show();
			}
			else{
				AppLoginCheck();
			}
		}
	};
	//��������¼��֤
	public Runnable appRunnable=new Runnable() {
		public void run() {
			String url =Constant.URL_AppLoginCheck+"?account="+account+"&identify="+identify+"&os="+os+"&ostype="+ostype+"&mac="+mac+"&ip="+ip+"&version="+versionCode;
			Log.i("test", "������url:"+url);
			HttpGet httpRequest=new HttpGet(url);
			try{
				HttpClient httpClient=new DefaultHttpClient();
				HttpResponse httpResponse=httpClient.execute(httpRequest);
				Log.i("test","���ؽ��"+ httpResponse);
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
				 {
					 String respData = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
					 Object result = RestClient.parseJson(respData);
					 Log.i("test","��ȡ�ĵ�¼��֤�������"+result);
					 if(result.getClass()==JSONObject.class){
						 JSONObject jsonResult = (JSONObject)result;
						 //���÷���˰汾��
						 Constant.setAPP_VERSION_RECIVE(jsonResult.getString("androidver").toString());
						 Constant.setUpdatepoint(jsonResult.getString("androidUpdate").toString());
						 //�ж���֤���
						 String AppLoginCheckResult=jsonResult.getString("status").toString();
						 if(AppLoginCheckResult.equals("1")){
							 //У��ͨ��
							Message myMessage=new Message();
							myMessage.what=AppLoginCheckOK;
							myHandler.sendMessage(myMessage);
						 }
						 else if(AppLoginCheckResult.equals("0")){
							 Message myMessage=new Message();
							 myMessage.what=AppLoginCheckFaile4;
							 AppLoginCheckTips=jsonResult.getString("statusTip").toString();
							 myHandler.sendMessage(myMessage);
						 }
						 else if(AppLoginCheckResult.equals("2")){
							 //У��ʧ�ܣ��˺Ż������Ϊ�գ�
							 Message myMessage=new Message();
							 myMessage.what=AppLoginCheckFaile2;
							 AppLoginCheckTips="��ȡ�ֻ�Ȩ��ʧ��";
							 myHandler.sendMessage(myMessage);
						 }
						 else if(AppLoginCheckResult.equals("3")){
							 //δ�ڰ�������Χ�ڻ��߸��˺������������󶨻��߸û����������˺Ű�
							 Message myMessage=new Message();
							 myMessage.what=AppLoginCheckFaile3;
							 AppLoginCheckTips=jsonResult.getString("statusTip").toString();
							 myHandler.sendMessage(myMessage);
						 }
					 }
				 }
				else{
					 Log.i("test", "��������֤ʧ�ܣ�����������״̬����200");
					 Message myMessage=new Message();
					 myMessage.what=AppLoginCheckFaile;
					 myHandler.sendMessage(myMessage);
				}
					
			}
			catch(Exception e){
				Log.i("test", "��������֤ʧ�ܣ�"+e.toString());
				Message myMessage=new Message();
				myMessage.what=AppLoginCheckFaile;
				myHandler.sendMessage(myMessage);
			}
		}
	};
	Runnable loginRunnable=new Runnable() {
		public void run() {
			String url=Constant.URL_LOGIN;
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", account));
			params.add(new BasicNameValuePair("password", password));
			try {
				HttpPost httpRequest = new HttpPost(url);
				Log.i("test", "��¼������"+params);
				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				DefaultHttpClient httpclient = new DefaultHttpClient();
				CookieUtil.setCookieSpec(httpclient);
				CookieUtil.writeCookie(httpclient);
				HttpResponse httpResponse = httpclient.execute(httpRequest);
				String respData;
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					CookieUtil.readCookie(httpclient);
					respData = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
					Log.i("test", "��¼���"+respData);
					if(respData.contains("106")){
						Log.i("test", "�������");
						Message myMessage=new Message();
						myMessage.what=LoginFaile1;
						myHandler.sendMessage(myMessage);
					}
					else if(respData.contains("107")||respData.contains("105")){
						Log.i("test", "�Ѿ�������");
						Message myMessage=new Message();
						myMessage.what=LoginFaile2;
						myHandler.sendMessage(myMessage);
					}
					else if(respData.contains("102")){
						Log.i("test", "�޸���Ϣʧ��");
						Message myMessage=new Message();
						myMessage.what=LoginFaile3;
						myHandler.sendMessage(myMessage);
					}
					else{
						Object result = RestClient.parseJson(respData);
						if(result.getClass() == JSONObject.class){
							JSONObject jsonResult = (JSONObject)result;
							if(jsonResult.get("msg").toString().equalsIgnoreCase("ok")){
								//��½�ɹ�
								Log.i("test", "��½�ɹ�");
								Message myMessage=new Message();
								myMessage.what=LoginOK;
								myHandler.sendMessage(myMessage);
							}
						}
					}
				}else{
					//��¼ʧ�ܷ���������ֵ����200
					Log.i("test", "��¼ʧ�ܷ���������ֵ����200");
					Message myMessage=new Message();
					myMessage.what=LoginFaile;
					myHandler.sendMessage(myMessage);
				}
			} catch (Exception e) {
				Log.i("test", "��¼ʧ�ܣ�"+e.toString());
				Message myMessage=new Message();
				myMessage.what=LoginFaile;
				myHandler.sendMessage(myMessage);
			}
		}
	};
	Runnable refreshLoginStatus=new Runnable() {
		public void run() {
			String url=Constant.URL_UpdateLoginStatus+"?account="+account+"&identify="+identify;
			Log.i("test", "ˢ�µ�¼״̬:"+url);
			HttpGet httpRequest=new HttpGet(url);
			HttpClient httpClient=new DefaultHttpClient();
			try {
				HttpResponse httpResponse=httpClient.execute(httpRequest);
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
					Message myMessage=new Message();
					myMessage.what=refreshok;
					myHandler.sendMessage(myMessage);
				}
				else{
					Message myMessage=new Message();
					myMessage.what=refreshok;
					myHandler.sendMessage(myMessage);
				}
			} catch(Exception e){
				Message myMessage=new Message();
				myMessage.what=refreshok;
				myHandler.sendMessage(myMessage);
			}
		}
	};
	Runnable downloadApk=new Runnable() {
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
			catch(Exception e){
				e.printStackTrace();
			}
		}

		private void installApk() {
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
	};
	private final TagAliasCallback aliasCallback=new TagAliasCallback() {
	public void gotResult(int arg0, String arg1, Set<String> arg2) {
		switch (arg0) {
		case 0:
			Log.i("test","���ñ����ɹ�");
			break;
		case 6002:
			Log.i("test", "���ñ���ʧ���������� errorcode:"+arg0);
			Message setmessage=new Message();
			setmessage.what=MSG_SET_ALIAS;
			msetHandler.sendMessage(setmessage);
			break;
		default:
			break;
		}
		}
	};
	private final TagAliasCallback tagCallback=new TagAliasCallback() {
		public void gotResult(int arg0, String arg1, Set<String> arg2) {
			switch (arg0) {
			case 0:
				Log.i("test", "����tag�ɹ�");
				break;
			case 6002:
				Log.i("test","����tagʧ��");
				Message tagmessage=new Message();
				tagmessage.what=MSG_SET_TAG;
				msetHandler.sendMessage(tagmessage);
			default:
				break;
			}
		}
	};
	private final Handler msetHandler=new Handler(){
		public void handleMessage(android.os.Message msg){
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SET_ALIAS:
				JPushInterface.setAlias(getApplicationContext(), account, aliasCallback);
				break;
			case MSG_SET_TAG:
				String tag="hse_hbsh";
				Set<String>jpushTag=new LinkedHashSet<String>();
				jpushTag.add(tag);
				JPushInterface.setTags(getApplicationContext(), jpushTag, tagCallback);
			default:
				break;
			}
		}
	};
	private void updaterefuse(){
		Message myMessage=new Message();
		myMessage.what=refuseupdate;
		myHandler.sendMessage(myMessage);
	}
	@SuppressLint("SimpleDateFormat")
	private Boolean timetoupdate(){
		Date date=new Date();
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		String curentday=new SimpleDateFormat("yyyy-MM-dd").format(date);
		SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
		String lastcheckupdatedat=sp.getString("lastcheckupdate", "2014-6-12");
		Log.i("test", "��ǰʱ��"+curentday);
		Log.i("test","�ϴθ���ʱ��:"+lastcheckupdatedat);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date date1 = null,date2=null;
		try{
			date1= simpleDateFormat.parse(curentday);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		try{
			date2=simpleDateFormat.parse(lastcheckupdatedat);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		GregorianCalendar cal1 = new GregorianCalendar();  
		GregorianCalendar cal2 = new GregorianCalendar();  
		cal1.setTime(date1);  
		cal2.setTime(date2);
		double dayCount = (cal1.getTimeInMillis()-cal2.getTimeInMillis())/(1000*3600*24);
//		Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
//		editor.putString("lastcheckupdate", curentday);
//		editor.commit();
		if(dayCount>7.0){
			return true;
		}
		return false;
	}
	private void showUpdateView(){
		Date date=new Date();
		Calendar c=Calendar.getInstance();
		c.setTime(date);
		String curentday=new SimpleDateFormat("yyyy-MM-dd").format(date);
		Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
		editor.putString("lastcheckupdate", curentday);
		editor.commit();
		
		dlg=new AlertDialog.Builder(ctx).create();
		dlg.show();
		Window window=dlg.getWindow();
		window.setContentView(R.layout.updateview);
		TextView updatepoint=(TextView)window.findViewById(R.id.updatepoint);
		String point=Constant.getUpdatepoint();
		point=point.replaceAll("<br/>", "");
		updatepoint.setText(point);
		Button btn_update=(Button)window.findViewById(R.id.updatebutton);
		btn_update.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				Message myMessage=new Message();
				myMessage.what=toupdate;
				myHandler.sendMessage(myMessage);
				dlg.dismiss();
			}
		});
		Button btn_cancle=(Button)window.findViewById(R.id.canclebutton);
		btn_cancle.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				updaterefuse();
				dlg.dismiss();
			}
		});
	}
	private void appupdate(){
		showProgressDialog("", "�������ء���");
		new Thread(downloadApk).start();
	}
	private void checkguide(){
		//����Main
		SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
    	Boolean isFirstload=sp.getBoolean("isFirstload", true);
    	String versioncode=sp.getString("version", "1.1.3");
    	Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
				
    	//��ȡ�汾��
 		PackageManager pm=getPackageManager();
 		PackageInfo nPackageInfo;
 		String spversioncode="";
 		try {
 			nPackageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
 			spversioncode=nPackageInfo.versionName;
 		} catch (NameNotFoundException e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
		Intent guideIntent=new Intent(Login.this, HSEGuideSEActivity.class);
		Intent mainIntent = new Intent(Login.this,Main.class); 
 		if(isFirstload||!versioncode.equals(spversioncode)){
 			editor.putString("version", spversioncode);
 			editor.putBoolean("isFirstload", false);
				editor.commit();
				Login.this.startActivity(mainIntent);
 			//Login.this.startActivity(guideIntent); 
 		}
 		else{
 			editor.putString("version", spversioncode);
 			editor.putBoolean("isFirstload", false);
				editor.commit();
 			Login.this.startActivity(mainIntent); 
 		}
		hideProgressDialog();
		Login.this.finish();
	}
}

