package com.hse.mobile.oapub;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Stack;

import org.apache.http.cookie.Cookie;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;

import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.MessageType;

import com.hbsh.beta.R;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.GestureDetector;

/*
 * Ӧ�ó�����
 */
public class HseApplication extends Application{
	private Stack<Intent>	navigationQueue = new Stack<Intent>();
	private Handler frameworkMessageHandler;
	private GestureDetector gestureDetector;
	private Boolean 		isLogged = false;	//�Ƿ��ѵ�½�ɹ�
	private Boolean			isInBackground = false; //�Ƿ��ں�̨����
	private Bitmap			bitmap = null;		//Activity֮�䴫��������
	public Map<String, Object> session = new HashMap<String, Object>();
    @Override
    public void onCreate() {    	     
    	 Log.d("HseApplication", "onCreate");
         super.onCreate();
         
         initJPush();
    }
    
	@Override
	public void onTerminate() {
		clearAllIntent();
		super.onTerminate();
	}
	
	/*
	 * ��ʼ�����ͷ���
	 */
	private void initJPush(){
        JPushInterface.setDebugMode(true); 	//���ÿ�����־,����ʱ��ر���־
        JPushInterface.init(this);     		// ��ʼ�� JPush
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(HseApplication.this);
        //221.195.72.44 ��������1����ʾ��ʽ
        builder.statusBarDrawable=R.drawable.push_icon;
        builder.notificationFlags=Notification.FLAG_AUTO_CANCEL;
        builder.notificationDefaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        JPushInterface.setPushNotificationBuilder(1, builder);
        //��ʼ��jpush���ֹͣ���� ��¼�ɹ����ڿ���
        JPushInterface.stopPush(this);
//        if(!Setting.isPushEnable(this)){
//       	 JPushInterface.stopPush(this);
//        }
	}
	
	public Handler getFrameworkMessageHandler() {
		return frameworkMessageHandler;
	}

	public void setFrameworkMessageHandler(Handler frameworkHandler) {
		this.frameworkMessageHandler = frameworkHandler;
	}
	
	private void pushIntent(Intent intent){
		navigationQueue.push(intent);
	}
	
	private Intent popIntent(){
		//ԭ���߼�
		if(!navigationQueue.isEmpty()){
			return navigationQueue.pop();
		}else{
			return null;
		}
		
	}
	
	public Intent getTopIntent(){
		return navigationQueue.peek();
	}
	
	public void clearAllIntent(){
		navigationQueue.removeAllElements();
	}
	/*
	 * ֹͣ����
	 */
	public void stoppush(){
		JPushInterface.stopPush(this.getApplicationContext());
	}
	/*
	 * ��������վ��
	 */
	public void navigateRoot(Intent intent, String title){
		clearAllIntent();
		navigate(intent, title, false);
	}
	
	/*
	 * ����
	 */
	public void navigate(Intent intent, String title, boolean showBack){
		Bundle bundle = intent.getExtras();
		if(bundle == null){
			bundle = new Bundle();
		}
		bundle.putBoolean(MessageKey.SHOW_TITLE_BACK, showBack);
		bundle.putString(MessageKey.TITLE, title);
		intent.putExtras(bundle);
		navigate(intent);
	}
	
	private void navigate(Intent intent){
		if(frameworkMessageHandler != null){
			Message msg = frameworkMessageHandler.obtainMessage();
			msg.what = MessageType.NAVIGATE;
			msg.obj = intent;	
			msg.setData(intent.getExtras());
			frameworkMessageHandler.sendMessage(msg);
			
			pushIntent(intent);
		}else{
			this.startActivity(intent);
		}
	}
	
	/*
	 * �����Ƿ���ʾ������
	 */
	public void showTitlebar(boolean show){
		Bundle bundle = new Bundle();
		bundle.putBoolean(MessageKey.SHOW_TITLE, show);
		Message msg = Message.obtain();
		msg.what = MessageType.SHOW_HEAD;	
		msg.setData(bundle);
		sendMessageToFramework(msg);
	}
	
	public void sendMessageToFramework(Message msg){
		if(frameworkMessageHandler != null){
			frameworkMessageHandler.sendMessage(msg);
		}
	}
	
	/*
	 * ����
	 */
	public void navigateBack(){
		popIntent();
		Intent intent = popIntent();
		if(intent != null){
			navigate(intent);
		}
	}
	/*
	 * �����ж�
	 */
	public Boolean goBack(){
		if(!navigationQueue.isEmpty()){
			Intent intent1=navigationQueue.pop();
			if(!navigationQueue.isEmpty()){
				pushIntent(intent1);
				return true;
			}
			else {
				pushIntent(intent1);
				return false;
			}
		}
		else {
			return false;
		}
	}
	public void mygoBack(){
		Intent intent1=navigationQueue.pop();
		Intent intent2=navigationQueue.pop();
		navigate(intent2);
	}
	public GestureDetector getGestureDetector() {
		return gestureDetector;
	}

	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}

	public Boolean getIsLogged() {
		return isLogged;
	}

	public void setIsLogged(Boolean isLogged) {
		this.isLogged = isLogged;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public void setLoginUserId(String userId){
		session.put("userId", userId);
		Log.i("test","session:"+session);
		
//		try{
//			JPushInterface.setAliasAndTags(this, userId, null);   
//		}catch(Exception ex){
//			
//		}
	}
	
	public String getLoginUserId(){
		return session.get("userId").toString();
	}

	public Boolean isInBackground() {
		return isInBackground;
	}

	public void setIsInBackground(Boolean isInBackground) {
		this.isInBackground = isInBackground;
	}
	
	/*
	TagAliasCallback setJPushAliasCallback = new TagAliasCallback(){
		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs ;
			switch (code) {
			case 0:
				logs = "Set tag and alias success, alias = " + alias + "; tags = " + tags;
				Log.i(TAG, logs);
				break;
			
			default:
				logs = "Failed with errorCode = " + code + " " + alias + "; tags = " + tags;
				Log.e(TAG, logs);
			}
			ExampleUtil.showToast(logs, getApplicationContext());
		}
	}
	*/
}
