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
 * ����ڻ���Activity���ܷ�װ
 */
public class BaseActivity extends Activity {
	private ProgressDialog progressDlg = null;
	/*
	 * ��ʾ���ȶԻ���
	 */
	public void showProgressDialog(String title, String content){
		progressDlg = ProgressDialog.show(this, title, content);
	}
	
	/*
	 * ���ؽ��ȶԻ���
	 */
	public void hideProgressDialog(){
		if(progressDlg != null){
			progressDlg.dismiss();
			progressDlg = null;
		}
	}
	
	/*
	 * ��ԭ�е�ʵ���лָ�
	 */
	public void onResumeFromExist(){
		
	}
	
	
	/*
	 * ��ܵ�����ָ����Intent
	 * intent 
	 * title     ����
	 * showBack  �Ƿ���ʾ���˰�ť
	 */
	public void navigate(Intent intent, String title, boolean showBack){
		((HseApplication)this.getApplicationContext()).navigate(intent, title, showBack);
		//this.finish();
	}
	
	/*
	 * ����
	 */
	public void back(){
		((HseApplication)this.getApplicationContext()).navigateBack();
	}
	
	/*
	 * ��ʾ/���� ������
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
	 * ���ñ������ұ߰�ť
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
	 * �첽Rest�����������
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
