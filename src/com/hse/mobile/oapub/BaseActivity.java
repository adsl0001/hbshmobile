package com.hse.mobile.oapub;

import java.net.URL;

import org.json.JSONObject;

import com.hse.mobile.oa.business.IRestServiceListener;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.MessageType;
import com.hse.mobile.oa.business.RestServiceTask;
import com.hse.mobile.oa.util.RestClient;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View.OnClickListener;

/*
 * 框架内基础Activity功能封装
 */
public class BaseActivity extends Activity {
	private ProgressDialog progressDlg = null;
	/*
	 * 显示进度对话框
	 */
	public void showProgressDialog(String title, String content){
		progressDlg = ProgressDialog.show(this, title, content);
	}
	
	/*
	 * 隐藏进度对话框
	 */
	public void hideProgressDialog(){
		if(progressDlg != null){
			progressDlg.dismiss();
			progressDlg = null;
		}
	}
	
	/*
	 * 从原有的实例中恢复
	 */
	public void onResumeFromExist(){
		
	}
	
	
	/*
	 * 框架导航至指定的Intent
	 * intent 
	 * title     标题
	 * showBack  是否显示后退按钮
	 */
	public void navigate(Intent intent, String title, boolean showBack){
		((HseApplication)this.getApplicationContext()).navigate(intent, title, showBack);
		//this.finish();
	}
	
	/*
	 * 后退
	 */
	public void back(){
		((HseApplication)this.getApplicationContext()).navigateBack();
	}
	
	/*
	 * 显示/隐藏 标题栏
	 */
	public void showTitlebar(boolean show){
		Bundle bundle = new Bundle();
		bundle.putBoolean(MessageKey.SHOW_TITLE, show);
		Message msg = Message.obtain();
		msg.what = MessageType.SHOW_HEAD;	
		msg.setData(bundle);
		((HseApplication)this.getApplicationContext()).sendMessageToFramework(msg);
	}
	
	/*
	 * 设置标题栏右边按钮
	 */
	public void setTitleBarRightButton(String title, OnClickListener listener){
		Bundle bundle = new Bundle();
		bundle.putString(MessageKey.TITLE, title);
		Message msg = Message.obtain();
		msg.what = MessageType.SHOW_HEAD_RIGHTBUTTON;	
		msg.obj = listener;
		msg.setData(bundle);
		((HseApplication)this.getApplicationContext()).sendMessageToFramework(msg);
	}
	public void onRestServiceResult(JSONObject result){
		
	}
	
	/*
	 * 异步Rest服务调用任务
	 */
	public class RestService extends RestServiceTask{

		public RestService() {
			super(null);
		}

		@Override
		protected void onPostExecute(JSONObject result) {
			onRestServiceResult(result);
		}
	}
	
}
