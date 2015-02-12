package com.hse.mobile.oapub;


import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import com.hse.mobile.oa.business.Constant;
import com.hbsh.beta.R;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public class Suggestion extends BaseActivity {
	EditText editview,moblieview;
	String mysuggest,mymobilenumber;
	Handler handler;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.suggestion);
		super.setTitleBarRightButton("确定",onPassOK);
		editview=(EditText)this.findViewById(R.id.suggestview);
		moblieview=(EditText)this.findViewById(R.id.suggetmobile);
		editview.setFocusable(true);
		editview.setFocusableInTouchMode(true);
		editview.requestFocus();
		InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editview, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
	}
	public void onResumeFromExist(){
		editview.requestFocus();
		InputMethodManager imm=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(editview, InputMethodManager.RESULT_SHOWN);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
		super.setTitleBarRightButton("确定", onPassOK);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.suggestion, menu);
		return true;
	}
	OnClickListener onPassOK=new OnClickListener() {
		public void onClick(View v) {
			mymobilenumber=moblieview.getText().toString();
			mysuggest=editview.getText().toString();
			if(mysuggest==null||mysuggest.trim().equals("")){
				Toast.makeText(getApplicationContext(), "请输入您的意见!", Toast.LENGTH_SHORT).show();
				return;
			}
			else if(mysuggest.length()>400){
				Toast.makeText(getApplicationContext(), "请将您的意见控制在200字以内!", Toast.LENGTH_SHORT).show();
				return;
			}
			else{
				Log.i("test","意见："+mysuggest);
				if(mymobilenumber==null||mymobilenumber.trim().equals("")){
					Toast.makeText(getApplicationContext(), "请输入您的电话号码", Toast.LENGTH_SHORT).show();
					return;
				}
				else if(mymobilenumber.length()!=11){
					Toast.makeText(getApplicationContext(), "请输入正确的手机号码", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			showProgressDialog("","正在上传您的意见");
			new suggestioin().execute(mysuggest,mymobilenumber);
		}
	};
	public class suggestioin extends AsyncTask<String, Integer, Boolean>{
		@Override
		protected Boolean doInBackground(String... arg0) {
			if(arg0.length==2){
				return getresponse(arg0[0],arg0[1]);
			}
			else return null;
		}
		protected void onPostExecute(Boolean result) {
			onsuggestioinResult(result);
		}
	}
	public void onsuggestioinResult(Boolean result){
		hideProgressDialog();
		if(result==null){
			Toast.makeText(getApplicationContext(), "反馈失败!", Toast.LENGTH_SHORT).show();
		}
		else if(result){
			Toast.makeText(getApplicationContext(), "反馈成功!", Toast.LENGTH_SHORT).show();
			editview.setText("");
			moblieview.setText("");
			((HseApplication)this.getApplication()).navigateBack();
		}
		else{
			Toast.makeText(getApplicationContext(), "反馈失败!", Toast.LENGTH_SHORT).show();
		}
	}
	public boolean getresponse(String str1,String str2){
		String parma="";
		try {
			parma=URLEncoder.encode(str1, "UTF-8");
		} catch (UnsupportedEncodingException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String url=Constant.URL_SUGGESTION+"?personcode="+Constant.User_name+"&content="+parma+"&mobile="+str2;
		HttpGet getMethod=new HttpGet(url);
		HttpClient httpClient = new DefaultHttpClient(); 
		try{
			HttpResponse response = httpClient.execute(getMethod);
			if(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
				 String strResult=EntityUtils.toString(response.getEntity(),"UTF-8");
				 if(strResult.equals("ok"))
					 return true;
				 else return false;
			}
		}
		catch(Exception e){
			Log.i("test",e.toString());
		}
		return false;
	}
}
