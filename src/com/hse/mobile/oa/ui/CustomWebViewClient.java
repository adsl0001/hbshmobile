package com.hse.mobile.oa.ui;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaWebViewClient;
import org.apache.cordova.IceCreamCordovaWebViewClient;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/*
 * 自定义WebViewClient(实现附件下载功能)
 * 已经废弃掉的
 */
public class CustomWebViewClient extends WebViewClient {

	public void onLoadResource(WebView view, String url) {
		Log.d("onLoadResource:", url);
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
		Log.d("onPageStarted:", url);
		super.onPageStarted(view, url, favicon);
	}
	
	//重写shouldOverrideUrlLoading方法，使点击链接后不使用其他的浏览器打开。 
	 @Override 
	 public boolean shouldOverrideUrlLoading(WebView view, String url) { 
		 Log.d("shouldOverrideUrlLoading:", url);
		 
		 if(url.endsWith(".doc") || url.endsWith(".docx") 
				||url.endsWith(".xls") || url.endsWith(".xlsx")|| url.endsWith(".pdf")
				||url.endsWith(".jpg") || url.endsWith(".jpeg")|| url.endsWith(".png")
				||url.endsWith(".bmp") || url.endsWith(".gif")){
			 processDownload(view, url);
			 return true;
		 }
		 
	     view.loadUrl(url); 
	     return true;
	     //如果不需要其他对点击链接事件的处理返回true，否则返回false 
	     //return super.shouldOverrideUrlLoading(view, url); 
	 } 
	 
	 private void processDownload(WebView view, String url){
		 Uri uri = Uri.parse(url);
		 Intent intent = new Intent(Intent.ACTION_VIEW,uri);  
		 view.getContext(). startActivity(intent); 
		 
		 Log.d("processDownload:", url);
	 }
}
