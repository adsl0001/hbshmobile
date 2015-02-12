package com.hse.mobile.oa.business;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

import com.hse.mobile.oa.util.RestClient;

public class HandleMyTask {
	public static boolean getMytask(String name)throws Exception{
		String account=Constant.getUser_name();
		String url=Constant.URL_HanldeMyTask+"?account="+account;
		HttpGet httpRequest=new HttpGet(url);
		try{
			HttpClient httpClient=new DefaultHttpClient();
			 HttpResponse httpResponse=httpClient.execute(httpRequest);
			 Log.i("test", "result:"+httpResponse.getStatusLine());
			 if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
			 {
				 String respData = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				 Object result = RestClient.parseJson(respData);
				 Log.i("test","获取的MyTask数据："+result);
				 if(result.getClass()==JSONObject.class){
					 JSONObject jsonResult = (JSONObject)result;
					 if(jsonResult.has("status")){
						 Constant.setMytaskNumber(jsonResult.getString("status").toString());
					 }
					 else Constant.setNoticenumber("1");
				 }
				 return true;
			 }
		}
		catch(Exception e){
			Constant.setNoticenumber("1");
			throw e;
		}
	}
}
