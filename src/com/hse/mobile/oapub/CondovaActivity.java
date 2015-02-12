/*
       Licensed to the Apache Software Foundation (ASF) under one
       or more contributor license agreements.  See the NOTICE file
       distributed with this work for additional information
       regarding copyright ownership.  The ASF licenses this file
       to you under the Apache License, Version 2.0 (the
       "License"); you may not use this file except in compliance
       with the License.  You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

       Unless required by applicable law or agreed to in writing,
       software distributed under the License is distributed on an
       "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
       KIND, either express or implied.  See the License for the
       specific language governing permissions and limitations
       under the License.
 */

package com.hse.mobile.oapub;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.Toast;

import org.apache.cordova.*;

import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.ui.CustomCordovaWebView;
import com.hse.mobile.oa.ui.CustomIceCreamCordovaWebViewClient;
import com.hse.mobile.oa.ui.CustomWebViewClient;
import com.hse.mobile.oa.util.CookieUtil;
import com.hse.mobile.oa.util.WebViewDownloadListener;

/*
public class CondovaActivity extends Activity {
	WebView webView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		webView = new WebView(this);
		this.setContentView(webView);
		
		webView.setWebViewClient(new CustomWebViewClient());
		webView.setDownloadListener(new WebViewDownloadListener(this));
		webView.getSettings().setJavaScriptEnabled(true);
		webView.getSettings().setSupportZoom(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.requestFocusFromTouch();
		
        String url = this.getIntent().getStringExtra(MessageKey.URL);
        Log.i("open url", url);
        CookieUtil.asyncCookieToWebview(this, url);
		webView.loadUrl(url);
	}
}

*/


/*
 * PhoneGap 浏览器Activity
 */

public class CondovaActivity extends DroidGap
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Set by <content src="index.html" /> in config.xml
        //super.loadUrl(Config.getStartUrl());
        //super.loadUrl("file:///android_asset/www/index.html");
        String url = this.getIntent().getStringExtra(MessageKey.URL);
        Log.i("open url", url);
        CookieUtil.asyncCookieToWebview(this, url);
		super.setStringProperty("errorUrl", "file:///android_asset/error.html");
        super.setIntegerProperty("backgroundColor", Color.WHITE);
        super.setStringProperty("loadingDialog", "正在加载中...");
        super.setStringProperty("loadingPageDialog", "正在加载中...");
        super.loadUrl(url);
    }
    @Override
    public void init() {
        //只是把源码中的CordovaWebView换成NobackWebView，其他还是源码
        CordovaWebView webView = new CustomCordovaWebView(this);
        webView.setDownloadListener(new WebViewDownloadListener(this));
        CordovaWebViewClient webViewClient;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
            //webViewClient = new CordovaWebViewClient(this, webView);
        	webViewClient = new CustomIceCreamCordovaWebViewClient(this, webView);
        } else {
            webViewClient = new CustomIceCreamCordovaWebViewClient(this, webView);
        }
        this.init(webView, webViewClient,
                new CordovaChromeClient(this, webView));
        
    	if(super.appView != null){
            super.appView.getSettings().setSupportZoom(true);
            super.appView.getSettings().setBuiltInZoomControls(true);
    	}
    }
    public void showdownload(){
    	Log.i("test", "在这里发生了调用");
    }
} 

