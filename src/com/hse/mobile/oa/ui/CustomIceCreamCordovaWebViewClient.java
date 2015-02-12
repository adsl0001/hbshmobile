package com.hse.mobile.oa.ui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.IceCreamCordovaWebViewClient;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;

import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MyBroadCast;
import com.hse.mobile.oa.util.CookieUtil;
import com.hse.mobile.oapub.BaseActivity;
import com.hse.mobile.oapub.HseApplication;
import com.hse.mobile.oapub.Main;

import android.app.Activity;
import android.app.Application;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
/*
 * 自定义WebViewClient(实现附件下载功能)
 */
public class CustomIceCreamCordovaWebViewClient extends IceCreamCordovaWebViewClient {
	private ProgressDialog progressDlg = null;
	private Context mycontext;
	MyBroadCast mybroadcast=new MyBroadCast();
	private String url;
	public static final int cache = 10 * 1024; 
    public CustomIceCreamCordovaWebViewClient(CordovaInterface cordova, CordovaWebView view) {
		super(cordova, view);
	}
    public void onLoadResource(WebView view, String url) {
		//Log.d("onLoadResource:", url);
		if(url.contains("download?")
			||url.endsWith(".doc") || url.endsWith(".docx") 
			||url.endsWith(".xls") || url.endsWith(".xlsx")|| url.endsWith(".pdf")){
			processDownload(view,url);
			return;
		}
		
		super.onLoadResource(view, url);
	}
	
	@Override
	public void onPageStarted(WebView view, String url, Bitmap favicon) {
		//Log.d("onPageStarted:", url);
		super.onPageStarted(view, url, favicon);
	}
	
	//重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。 
	 @Override 
	 public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		 //Log.d("shouldOverrideUrlLoading:", url);
		 
		 if(url.endsWith(".doc") || url.endsWith(".docx") 
				||url.endsWith(".xls") || url.endsWith(".xlsx")|| url.endsWith(".pdf")
				||url.endsWith(".jpg") || url.endsWith(".jpeg")|| url.endsWith(".png")
				||url.endsWith(".bmp") || url.endsWith(".gif")||url.endsWith("zip")||url.endsWith("rar")){
			 processDownload(view, url);
			 return true;
		 }
		 
	     view.loadUrl(url); 
	     return true;
	     //如果不需要其他对点击链接事件的处理返回true，否则返回false 
	 } 
	 
	 private void processDownload(WebView view, String downloadurl){
		 Log.i("test", "download:  "+downloadurl);
		 CookieSyncManager.createInstance(view.getContext());
		 List<Cookie>downloadCookie=CookieUtil.pushcookie();
		 String ROLTPAToken="";
		 for(int i=0;i<downloadCookie.size();i++){
			 if(downloadCookie.get(i).getName().equals("ROLTPAToken")){
				 ROLTPAToken=downloadCookie.get(i).getValue();
			 }
		 }
		 ROLTPAToken=URLEncoder.encode(ROLTPAToken);
		 url=downloadurl+"&result=login&LTPAToken="+ROLTPAToken;
		 Log.i("test", "download:  "+url);
		 mycontext=view.getContext();
		 new Thread(runnable).start();
		 progressDlg = ProgressDialog.show(view.getContext(), null,"正在下载");
	 }
	 public Runnable runnable=new Runnable() {
		public void run() {
			if(url==null||url.equals("")){
				return;
			}
			else{
				try{
					HttpClient httpclient=new DefaultHttpClient();
					HttpGet httpGet=new HttpGet(url);
					HttpResponse response=httpclient.execute(httpGet);
					HttpEntity entity=response.getEntity();
					InputStream is=entity.getContent();
					String filname=getFilename(response);
					filname= java.net.URLDecoder.decode(filname,"utf-8");
					Log.i("test", "下载文件名称:"+filname);
					String filepath="/sdcard/download/"+filname;
					File file=new File(filepath);
					FileOutputStream fileout=new FileOutputStream(file);
					byte[] buffer=new byte[cache];
					int ch=0;
					while((ch=is.read(buffer))!=-1){
						fileout.write(buffer,0,ch);
					}
					is.close();
					fileout.flush();
					fileout.close();
					progressDlg.dismiss();
					progressDlg=null;
					Intent intent=new Intent();
					intent.setAction("downloadfile");
					intent.putExtra("file", file.toString());
					mycontext.sendBroadcast(intent);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	};
	//校验匹配
	private boolean checkEndsWithInStringArray(String checkItsEnd,String[] fileEndings){
		for(String aEnd : fileEndings){
			if(checkItsEnd.endsWith(aEnd))
				return true;
		}
	  return false;
	}
	public static String getFilename(HttpResponse response){
		org.apache.http.Header contentHeader=response.getFirstHeader("Content-Disposition");
		Log.i("test", "head:"+contentHeader);
		String filename=null;
		if(contentHeader!=null){
			HeaderElement[] values=contentHeader.getElements();
			if(values.length==1){
				NameValuePair parm=values[0].getParameterByName("filename");
				if(parm!=null){
					try{
						filename=parm.getValue();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		return filename;
	}
}
