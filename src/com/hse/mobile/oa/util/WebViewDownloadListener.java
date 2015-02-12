package com.hse.mobile.oa.util;

import java.util.List;

import org.apache.http.cookie.Cookie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.DownloadListener;
/*
 * WebView ÏÂÔØ¼àÌýÆ÷
 */
public class WebViewDownloadListener implements DownloadListener {
	private Context mContext;
	public WebViewDownloadListener(Context ctx) {
		this.mContext = ctx;
	}

	public void onDownloadStart(String url, String userAgent,
			String contentDisposition, String mimetype, long contentLength) {
		 Log.i("test", "download listener:"+url);
		 Log.d("onDownloadStart:", url);
		 Uri uri = Uri.parse(url);
		 Intent intent = new Intent(Intent.ACTION_VIEW,uri);  
		 mContext.startActivity(intent); 
	}
}
