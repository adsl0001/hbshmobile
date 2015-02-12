package com.hse.mobile.oa.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.util.Log;

import com.hse.mobile.oa.util.RestClient;
/*
 * 废弃页面
 */
public class LoginPortalService {
/*
 * 白名单设置
 */
	public static boolean loginPortal(String userName,String myimei,String mac) throws Exception{
		Boolean loginPortal=false;
		//取得机器型号
		String os=android.os.Build.MODEL;
		os=os.replaceAll(" ", "");
		String url=Constant.URL_LOGIN_PORTAL+"?account="+userName+"&identify="+myimei+"&os=android&ostype="+os+"&mac="+mac;
		Log.i("test","url:"+url);
		HttpGet httpRequest=new HttpGet(url);
		try{
			 HttpClient httpclient=new DefaultHttpClient();
			 HttpResponse httpResponse=httpclient.execute(httpRequest);
			 if(httpResponse.getStatusLine().getStatusCode()==HttpStatus.SC_OK)
	            {
	                //取得返回的字符串
	                String strResult=EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
	                String []str=null;
	                str=strResult.split("\\|\\|");
	                if(str[0].equals("1")){
	                	loginPortal=true;
	                	if(str.length>1){
	                		String sss=str[1];
	                		Constant.setAPP_VERSION_RECIVE(sss);
	                	}
	                }
	                else{
	                	if(str.length>1&&str[1]!=null&&str[1]!=""){
	                		Constant.setLOGIN_PORTAL_RESULT(str[1]);
	                	}
	                }
	                /*
	                if(strResult.equals("1")){
	                	loginPortal=true;
	                }
	                else{
	                	String []str=null;
	                	str=strResult.split("|",2);
	                	if(str.length>2&&str[1]!=null&&str[1]!=""){
	                		Constant.LOGIN_PORTAL_RESULT=str[1];
	                	}
	                }
	                */
	            }
		}
		catch(Exception e){
		}
		return loginPortal;
	}
}
		/*try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("account", userName));
			params.add(new BasicNameValuePair("identify", myimei));
			//params.add(new BasicNameValuePair("userid", userName));
			//params.add(new BasicNameValuePair("j_username", userName));	
			//params.add(new BasicNameValuePair("j_password", password));
			
			Boolean loginPortalOk = false;
			Object result = RestClient.post(Constant.URL_LOGIN_PORTAL, params);
			//白名单接口返回的是字符串用字符串去接收
			if(result.getClass() == String.class){
				String portalResult=result.toString();
				if(portalResult.equals("1")){
					loginPortalOk=true;
				}
			}

			return loginPortalOk;
		} catch (Exception e) {
			throw e;
		}
	}*/
