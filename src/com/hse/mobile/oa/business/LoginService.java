package com.hse.mobile.oa.business;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.util.Log;
/*
 * ����ҳ��
 */
import com.hse.mobile.oa.util.RestClient;

/*
 * ��½����
 */
public class LoginService {
	
	/*
	 * ��¼
	 */
	public static boolean login(String userName, String password) throws Exception{
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("userName", userName));
			params.add(new BasicNameValuePair("password", password));
			//params.add(new BasicNameValuePair("userid", userName));
			//params.add(new BasicNameValuePair("j_username", userName));	
			//params.add(new BasicNameValuePair("j_password", password));
			
			//?? Ϊʲôhttp���󷵻�location�󣬻��Զ�����location��ָҳ��? 
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
