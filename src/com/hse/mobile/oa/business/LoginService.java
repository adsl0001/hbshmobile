package com.hse.mobile.oa.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;
/*
 * 废弃页面
 */
import com.hse.mobile.oa.util.RestClient;

/*
 * 登陆服务
 */
public class LoginService {
	
	/*
	 * 登录
	 */
	public static boolean login(String userName, String password) throws Exception{
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", userName));
			params.add(new BasicNameValuePair("password", password));
			//params.add(new BasicNameValuePair("userid", userName));
			//params.add(new BasicNameValuePair("j_username", userName));	
			//params.add(new BasicNameValuePair("j_password", password));
			
			//?? 为什么http请求返回location后，会自动请求location所指页面? 
			Boolean loginOk = false;
			Log.i("test", "************");
			Object result = RestClient.post(Constant.URL_LOGIN, params);
			Log.i("test", "loginresule:"+result);
			if(result.getClass() == JSONObject.class){
					JSONObject jsonResult = (JSONObject)result;
					if(jsonResult.get("msg").toString().equalsIgnoreCase("ok")){
						loginOk = true;
					}
			}

			return loginOk;
		} catch (Exception e) {
			throw e;
		}
	}
}
