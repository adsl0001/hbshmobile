package com.hse.mobile.oa.business;

import java.util.List;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;

import com.hse.mobile.oa.util.RestClient;

/*
 * 异步Rest服务调用任务
 */
public class RestServiceTask extends AsyncTask<Object,Integer,JSONObject>{
	IRestServiceListener serviceListener;
	
	public RestServiceTask(IRestServiceListener serviceListener){
		this.serviceListener = serviceListener;
	}
	
	@Override
	protected JSONObject doInBackground(Object... params) {
		if(params.length == 0)
			return null;
		
		String url = params[0].toString();
		String method = "get";
		if(params.length>1){
			method = params[1].toString().toLowerCase();
		}
		
		JSONObject result = null;
		Object resp = null;
		try {
			if(method.equals("get")){
				resp = RestClient.get(url);
			}
			
			if(method.equals("delete")){
				resp = RestClient.delete(url);
			}
			
			if(method.equals("post")){
				String dataType = "pair";
				if(params.length>3){
					dataType = params[3].toString();
				}
				
				if(dataType.equals("pair")){
					List<NameValuePair> postParams = null;
					if(params.length>2){
						postParams = (List<NameValuePair>)params[2];
					}
					resp = RestClient.post(url, postParams);
				}else if(dataType.equals("binary")){
					byte[] data = (byte[])params[2];
					resp = RestClient.post(url, data);
				}
			}
			
			if(method.equals("put")){
				String dataType = "pair";
				if(params.length>3){
					dataType = params[3].toString();
				}
				
				if(dataType.equals("pair")){
					List<NameValuePair> postParams = null;
					if(params.length>2){
						postParams = (List<NameValuePair>)params[2];
					}
					resp = RestClient.put(url, postParams);
				}else if(dataType.equals("binary")){
					byte[] data = (byte[])params[2];
					//resp = RestClient.post(url, data);
				}
			}
		} catch (Exception e) {
			//e.printStackTrace();
			Log.e("RestService", "hsemobile", e);
		}
		
		if(resp !=null && resp instanceof JSONObject){
			result = (JSONObject)resp;
		}
		return result;
	}
	
	@Override
	protected void onPostExecute(JSONObject result) {
		if(serviceListener != null){
			serviceListener.onRestServiceResult(result);
		}
	}
}

