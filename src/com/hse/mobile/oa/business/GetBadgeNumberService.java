package com.hse.mobile.oa.business;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.util.RestClient;
public class GetBadgeNumberService {
	public static boolean getnumber(String name)throws Exception
	{
		String personid=Constant.getUser_name();
		String url=Constant.URL_SETBADGENUMBER+"?personcode="+personid;
		Log.i("teset", "badge url:"+url);
		HttpGet httpRequest=new HttpGet(url);
		try{
			HttpClient httpClient=new DefaultHttpClient();
			 HttpResponse httpResponse=httpClient.execute(httpRequest);
			 Log.i("test", "result:"+httpResponse.getStatusLine());
			 if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
			 {
				 String respData = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
				 Object result = RestClient.parseJson(respData);
				 Log.i("test","获取的badgenumber数据："+result);
				 if(result.getClass()==JSONObject.class){
					 JSONObject jsonResult = (JSONObject)result;
					 if(jsonResult.has("CompanyNotice")){
						 Constant.setNoticenumber(jsonResult.getString("CompanyNotice").toString());
					 }
					 else Constant.setNoticenumber("0");
					 if(jsonResult.has("CompanyNews")){
						 Constant.setNewsnumber(jsonResult.getString("CompanyNews").toString());
					 }
					 else Constant.setNewsnumber("0");
				 }
				 return true;
			 }
		}
		catch(Exception e){
			throw e;
		}
	}
}
