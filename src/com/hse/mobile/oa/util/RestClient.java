package com.hse.mobile.oa.util;

import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

import com.hse.mobile.oa.business.Constant;

/*
 * Rest服务器客户端
 */
public class RestClient {
	
	public static JSONObject stringToJson(String value) throws JSONException{
		   JSONTokener jsonParser = new JSONTokener(value);  
		   JSONObject json = (JSONObject) jsonParser.nextValue();  
		   return json;
	}
	
	public static Object parseJson(String value)throws JSONException{
		   JSONTokener jsonParser = new JSONTokener(value);  
		   return jsonParser.nextValue();  
	}
	
	public static Object get(String url) throws Exception{
		HttpGet httpRequest = new HttpGet(url);
		return execute(httpRequest);
	}
	
	public static Object delete(String url) throws Exception{
		HttpDelete httpRequest = new HttpDelete(url);
		return execute(httpRequest);
	}
	
	public static Object post(String url, List<NameValuePair> params) throws Exception{
		HttpPost httpRequest = new HttpPost(url);
		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return execute(httpRequest);
	}
	
	public static Object post(String url, byte[] data) throws Exception{
		HttpPost httpRequest = new HttpPost(url);
		MultipartEntity entity = new MultipartEntity();
		entity.addPart("upper", new ByteArrayBody(data, "img.png"));
		httpRequest.setEntity(entity);
		return execute(httpRequest);
	}
	
	public static Object put(String url, List<NameValuePair> params) throws Exception{
		HttpPut httpRequest = new HttpPut(url);
		httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
		return execute(httpRequest);
	}
	
	protected static Object execute(HttpUriRequest httpRequest)throws Exception{
		DefaultHttpClient httpclient = new DefaultHttpClient();
		CookieUtil.setCookieSpec(httpclient);
		CookieUtil.writeCookie(httpclient);
		HttpResponse httpResponse = httpclient.execute(httpRequest);
		String respData;
		if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			CookieUtil.readCookie(httpclient);
			respData = EntityUtils.toString(httpResponse.getEntity(),"UTF-8");
			Object result = RestClient.parseJson(respData);
			return result;
		}else{
			return null;
		}
	}
}
