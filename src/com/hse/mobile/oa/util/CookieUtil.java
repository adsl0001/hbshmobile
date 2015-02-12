package com.hse.mobile.oa.util;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.text.GetChars;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;

/*
 * Cookie管理
 */
public class CookieUtil {
	public static List<Cookie> cookies;
	
	public static void clear(){
		if(cookies != null){
			cookies.clear();
		}
	}
	
	public static List<Cookie> pushcookie(){
		return cookies;
	}
	public static void readCookie( DefaultHttpClient  client){
		cookies = client.getCookieStore().getCookies();  
		for(int i=0; i<cookies.size(); i++){
		}
	}
	public static void writeCookie(DefaultHttpClient  client){
		if(cookies == null)
			return;
		for(Cookie cookie : cookies){
			client.getCookieStore().addCookie(cookie);
		}
	}
	
	
	/*
	 * Cookie 同步到浏览器
	 */
	public static void asyncCookieToWebview(Context context, String url){
		if (cookies!=null && !cookies.isEmpty()){  
		    CookieSyncManager.createInstance(context);  
		    CookieManager cookieManager = CookieManager.getInstance();  
		        //sync all the cookies in the httpclient with the webview by generating cookie string  
		    for (Cookie cookie : cookies){  
		        String cookieString = cookie.getName() + "=" + cookie.getValue() + "; domain=" + cookie.getDomain();  
		        cookieManager.setCookie(cookie.getDomain(), cookieString);  //url
		        CookieSyncManager.getInstance().sync();  
		    }  
		}  
	}
	
	public static void setCookieSpec(DefaultHttpClient client){
		client.getCookieSpecs().register("chinasource",
				new CookieSpecFactory() {
					public CookieSpec newInstance(HttpParams params) {
						return new CommonCookieSpec();
					}
				});
		client.getParams().setParameter(ClientPNames.COOKIE_POLICY,
				"chinasource");
	}

	
}
