package com.hse.mobile.oa.business;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

public class MinusBadgeNumberService {
	public static boolean minusBadge(String nid,String newstype) throws Exception{
		String personid=Constant.getUser_name();
		String url=Constant.URL_SETBADGENUMBER+"?personcode="+personid+"&nid="+nid+"&cat="+newstype;
		HttpGet httpRequest=new HttpGet(url);
		try{
			HttpClient httpClient=new DefaultHttpClient();
			HttpResponse httpResponse=httpClient.execute(httpRequest);
			if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				Log.i("test","badgenumber³É¹¦");
			}
			return true;
		}
		catch(Exception e){
			throw e;
		}
	}
}
