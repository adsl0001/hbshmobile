package com.hse.mobile.oa.business;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
/*
 * 废弃页面
 */
public class GetBindStatusService {
	public static String getBindStatus(String account,String identify)throws Exception{
		try{
			 String url=Constant.URL_GetBindStatus+"?account="+account+"&identify="+identify;
			 String result="";
			 HttpGet httpRequest=new HttpGet(url);
			 HttpClient httpclient=new DefaultHttpClient();
			 HttpResponse httpResponse=httpclient.execute(httpRequest);
			 if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
	            {
	                //取得返回的字符串
	                String strResult=EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
	                String []str=null;
	                str=strResult.split("\\|\\|");
	                if(str[0].equals("0")){
	                	result="0";
	                }
	                else if(str[0].equals("1")){
	                	result="1";
	                }
	                else if(str[0].equals("2")){
	                	Constant.setGetBindStatus_2(str[1]);
	                	result="2";
	                }
	                else if(str[0].equals("3")){
	                	Constant.setGetBindStatus_3(str[1]);
	                	result="3";
	                }
	            }
			 return result;
		}
		catch(Exception e){
			throw e;
		}
	}
}
