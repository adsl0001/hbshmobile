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
 * 登陆
 */
public class Login extends BaseActivity {
	public static final int LoginPortalFalse=30*1000;
	private Context ctx;
	private Button btnLogin;
	private EditText txtAccount, txtPassword;
	//用户名
	private String account;
	//密码
	private String password;
	//操作系统
	private String os="android";
	//手机型号
	private String ostype;
	//mac地址
	private String mac;
	//机器码
	private String identify;
	//ip地址
	private String ip;
	//本机版本号
	private String versionCode;
	
	Handler myHandler;
	private Thread downLoadThread ;
	private static final int DOWN_UPDATE =1;
	private static final int DOWN_OVER = 2;
	//白名单验证结果
	private static final int AppLoginCheckOK=3;
	private static final int AppLoginCheckFaile=4;
	private static final int AppLoginCheckFaile2=5;
	private static final int AppLoginCheckFaile3=6;
	private static final int AppLoginCheckFaile4=7;
	//登录结果
	private static final int LoginOK=10;
	private static final int LoginFaile=11;
	private static final int LoginFaile1=12;
	private static final int LoginFaile2=13;
	private static final int LoginFaile3=14;
	//更新登录状态
	private static final int refreshok=111;
	//取消更新app
	private static final int refuseupdate=222;
	private String AppLoginCheckTips;
	//设置别名
	private static final int MSG_SET_ALIAS=1102;
	//设置标签
	private static final int MSG_SET_TAG=1103;
	//
	private static final int toupdate=219;
	//更新窗口
	private AlertDialog dlg;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.login);
		ctx=this;
		txtAccount =  (EditText) this.findViewById(R.id.txt_login_account);
		txtPassword =  (EditText) this.findViewById(R.id.txt_login_password);
		btnLogin = (Button) this.findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(onLoginClickListener);
		//获取设备码
		TelephonyManager tm=(TelephonyManager)getBaseContext().getSystemService(Context.TELEPHONY_SERVICE);
		identify=tm.getDeviceId();
		//获取mac地址 
		WifiManager wifi=(WifiManager)getSystemService(Context.WIFI_SERVICE);
		WifiInfo info=wifi.getConnectionInfo();
		mac=info.getMacAddress();
		//ip地址
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
			Log.i("test", "获取ip地址失败");
		}
		//获取手机型号
		ostype=android.os.Build.MODEL;
		ostype=ostype.replaceAll(" ", "");
		//获取版本号
		PackageManager pm=getPackageManager();
		PackageInfo nPackageInfo;
		try {
			nPackageInfo = pm.getPackageInfo(getPackageName(), PackageManager.GET_CONFIGURATIONS);
			versionCode=nPackageInfo.versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//设置账号密码
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
		//自动登录
		if(autologin&&!account.isEmpty()&&!password.isEmpty()){
			AppLoginCheck();
		}
		myHandler=new Handler(){
			public void handleMessage(Message msg){
				switch(msg.what){
				
				case AppLoginCheckOK:
					Log.i("test", "白名单检测成功");
					login();
					return;
				case AppLoginCheckFaile:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage("网络错误!").setPositiveButton("确定", null).show();
					return;
				case AppLoginCheckFaile2:
					hideProgressDialog();
					Log.i("test", "白名单检测失败："+AppLoginCheckTips);
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage(AppLoginCheckTips).setPositiveButton("确定", null).show(); 
					return;
				case AppLoginCheckFaile3:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage(AppLoginCheckTips).setPositiveButton("确定", null).show(); 
					return;
				case AppLoginCheckFaile4:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage(AppLoginCheckTips).setPositiveButton("确定", null).show(); 
					return;
				case LoginFaile:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage("登录验证出错,请检查网络连接!").setPositiveButton("确定", null).show();
					return;
				case LoginOK:
					//登录验证成功 刷新登录状态
					refreshLogin();
					return;
				case LoginFaile1:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage("您输入的账号或密码不正确，请重新输入!").setPositiveButton("确定", null).show();
					return;
				case LoginFaile2:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage("您的账号已经被锁定!").setPositiveButton("确定", null).show();
					return;	
				case LoginFaile3:
					hideProgressDialog();
					new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage("帐号合法，但修改帐号登录信息失败!").setPositiveButton("确定", null).show();
					return;	
				case toupdate:
					appupdate();
					return;
				case refuseupdate:
					//重启极光推送 设置tag和alis
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
					//进入Main
					hideProgressDialog();
					checkguide();
					return;
				case refreshok:
					//登录成功设置各种参数 
					Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
					editor.putString("userName", account);
					editor.putString("password", password);
					editor.commit();
					Constant.setUser_imei(identify);
					Constant.setUser_mac(mac);
					Constant.setUser_name(account);
					//然后进行app更新验证  
					//然后进行app更新验证  指定周一上午10点更新
					if(timetoupdate()&checkupdate()){
						hideProgressDialog();
						showUpdateView();
						return;
					}
					else{
						//然后重启极光推送 设置tag和alis
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
	//查询用户消息列表
	private void AppLoginCheck(){
		showProgressDialog("","正在进行登录");
		new Thread(appRunnable).start();
	}
	//登录方法
	private void login(){
		Log.i("test", "登录");
		new Thread(loginRunnable).start();
	}
	//刷新登录状态
	private void refreshLogin(){
		Log.i("test", "刷新登录状态");
		new Thread(refreshLoginStatus).start();
	}
	//检查更新
	private Boolean checkupdate(){
		Log.i("test","检查版本更新");
		Log.i("test", "当前版本："+versionCode);
		String versionrecive=Constant.APP_VERSION_RECIVE;
		Log.i("test", "服务器版本："+versionrecive);
		if(versionCode!=null&&!"".equals(versionCode)&&!"".equals(versionrecive)&&!versionCode.equals(versionrecive)){
			return true;
//			hideProgressDialog();
//			new AlertDialog.Builder(this).setTitle("温馨提示").setMessage("移动办公平台已有新版本请升级")
//			.setPositiveButton("升级", new DialogInterface.OnClickListener() {
//				public void onClick(DialogInterface dialog, int which) {
//					showProgressDialog("", "正在下载……");
//					new Thread(downloadApk).start();
//				}
//			}).show();
		}
		return false;
	}
	//点击登录按钮
	OnClickListener onLoginClickListener=new OnClickListener() {
		public void onClick(View v) {
			account = txtAccount.getText().toString().trim();
			password = txtPassword.getText().toString().trim();
			if(account.equals("")||account==null){
				new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage("请输入账号").setPositiveButton("确定", null).show(); 
			}
			else if(password.equals("")||password==null){
				new AlertDialog.Builder(Login.this).setTitle("温馨提示").setMessage("请输入密码").setPositiveButton("确定", null).show();
			}
			else{
				AppLoginCheck();
			}
		}
	};
	//白名单登录验证
	public Runnable appRunnable=new Runnable() {
		public void run() {
			String url =Constant.URL_AppLoginCheck+"?account="+account+"&identify="+identify+"&os="+os+"&ostype="+ostype+"&mac="+mac+"&ip="+ip+"&version="+versionCode;
			Log.i("test", "白名单url:"+url);
			HttpGet httpRequest=new HttpGet(url);
			try{
				HttpClient httpClient=new DefaultHttpClient();
				HttpResponse httpResponse=httpClient.execute(httpRequest);
				Log.i("test","返回结果"+ httpResponse);
				if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
				 {
					 String respData = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
					 Object result = RestClient.parseJson(respData);
					 Log.i("test","获取的登录验证结果：："+result);
					 if(result.getClass()==JSONObject.class){
						 JSONObject jsonResult = (JSONObject)result;
						 //设置服务端版本号
						 Constant.setAPP_VERSION_RECIVE(jsonResult.getString("androidver").toString());
						 Constant.setUpdatepoint(jsonResult.getString("androidUpdate").toString());
						 //判断验证结果
						 String AppLoginCheckResult=jsonResult.getString("status").toString();
						 if(AppLoginCheckResult.equals("1")){
							 //校验通过
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
							 //校验失败，账号或机器码为空，
							 Message myMessage=new Message();
							 myMessage.what=AppLoginCheckFaile2;
							 AppLoginCheckTips="获取手机权限失败";
							 myHandler.sendMessage(myMessage);
						 }
						 else if(AppLoginCheckResult.equals("3")){
							 //未在白名单范围内或者该账号与其他机器绑定或者该机器与其他账号绑定
							 Message myMessage=new Message();
							 myMessage.what=AppLoginCheckFaile3;
							 AppLoginCheckTips=jsonResult.getString("statusTip").toString();
							 myHandler.sendMessage(myMessage);
						 }
					 }
				 }
				else{
					 Log.i("test", "白名单验证失败：服务器返回状态不是200");
					 Message myMessage=new Message();
					 myMessage.what=AppLoginCheckFaile;
					 myHandler.sendMessage(myMessage);
				}
					
			}
			catch(Exception e){
				Log.i("test", "白名单验证失败："+e.toString());
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
				Log.i("test", "登录参数："+params);
				httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
				DefaultHttpClient httpclient = new DefaultHttpClient();
				CookieUtil.setCookieSpec(httpclient);
				CookieUtil.writeCookie(httpclient);
				HttpResponse httpResponse = httpclient.execute(httpRequest);
				String respData;
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					CookieUtil.readCookie(httpclient);
					respData = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
					Log.i("test", "登录结果"+respData);
					if(respData.contains("106")){
						Log.i("test", "密码错误");
						Message myMessage=new Message();
						myMessage.what=LoginFaile1;
						myHandler.sendMessage(myMessage);
					}
					else if(respData.contains("107")||respData.contains("105")){
						Log.i("test", "已经被锁定");
						Message myMessage=new Message();
						myMessage.what=LoginFaile2;
						myHandler.sendMessage(myMessage);
					}
					else if(respData.contains("102")){
						Log.i("test", "修改信息失败");
						Message myMessage=new Message();
						myMessage.what=LoginFaile3;
						myHandler.sendMessage(myMessage);
					}
					else{
						Object result = RestClient.parseJson(respData);
						if(result.getClass() == JSONObject.class){
							JSONObject jsonResult = (JSONObject)result;
							if(jsonResult.get("msg").toString().equalsIgnoreCase("ok")){
								//登陆成功
								Log.i("test", "登陆成功");
								Message myMessage=new Message();
								myMessage.what=LoginOK;
								myHandler.sendMessage(myMessage);
							}
						}
					}
				}else{
					//登录失败服务器返回值不是200
					Log.i("test", "登录失败服务器返回值不是200");
					Message myMessage=new Message();
					myMessage.what=LoginFaile;
					myHandler.sendMessage(myMessage);
				}
			} catch (Exception e) {
				Log.i("test", "登录失败："+e.toString());
				Message myMessage=new Message();
				myMessage.what=LoginFaile;
				myHandler.sendMessage(myMessage);
			}
		}
	};
	Runnable refreshLoginStatus=new Runnable() {
		public void run() {
			String url=Constant.URL_UpdateLoginStatus+"?account="+account+"&identify="+identify;
			Log.i("test", "刷新登录状态:"+url);
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
				Log.i("test","开始下载");
				String apkfile=Constant.SDCARD_PATH+"hse_mobile_oa.apk";
				File apkFile=new File(apkfile);
				FileOutputStream fos=new FileOutputStream(apkFile);
				byte []buf=new byte[1024];
				int count=0;
				float jindu=0.0f;
				do{
					int numread=is.read(buf);
					count+=numread;
		            //更新进度
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
			Log.i("test","设置别名成功");
			break;
		case 6002:
			Log.i("test", "设置别名失败重新设置 errorcode:"+arg0);
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
				Log.i("test", "设置tag成功");
				break;
			case 6002:
				Log.i("test","设置tag失败");
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
		Log.i("test", "当前时间"+curentday);
		Log.i("test","上次更新时间:"+lastcheckupdatedat);
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
		showProgressDialog("", "正在下载……");
		new Thread(downloadApk).start();
	}
	private void checkguide(){
		//进入Main
		SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
    	Boolean isFirstload=sp.getBoolean("isFirstload", true);
    	String versioncode=sp.getString("version", "1.1.3");
    	Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
				
    	//获取版本号
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

