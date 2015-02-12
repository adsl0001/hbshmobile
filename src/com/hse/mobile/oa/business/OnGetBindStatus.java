package com.hse.mobile.oa.business;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;
/*
 * ����ҳ��
 */
public class OnGetBindStatus {
	public static Boolean onGetBindStatus(String account,String identify,String mac,String os,String osType)throws Exception{
		try{
			String url=Constant.URL_OnGetBindStatus+"?account="+account+"&identify="+identify+"&mac="+mac+"&os="+os+"&osType="+osType;
			Log.i("test","�󶨵�url��"+url);
			HttpGet httpRequest=new HttpGet(url);
			 HttpClient httpclient=new DefaultHttpClient();
			 HttpResponse httpResponse=httpclient.execute(httpRequest);
			 if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				 String strResult=EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				 Log.i("test","�󶨵ķ��ؽ����"+strResult);
				 if(strResult.equals("ok")){
					 return true;
				 }
			 }
			return false;
		}
		catch(Exception e){
			throw e;
		}
	}
}
