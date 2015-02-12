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
 * 应用程序类
 */
public class HseApplication extends Application{
	private Stack<Intent>	navigationQueue = new Stack<Intent>();
	private Handler frameworkMessageHandler;
	private GestureDetector gestureDetector;
	private Boolean 		isLogged = false;	//是否已登陆成功
	private Boolean			isInBackground = false; //是否在后台运行
	private Bitmap			bitmap = null;		//Activity之间传递数据用
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
	 * 初始化推送服务
	 */
	private void initJPush(){
        JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(HseApplication.this);
        //221.195.72.44 推送类型1的显示样式
        builder.statusBarDrawable=R.drawable.push_icon;
        builder.notificationFlags=Notification.FLAG_AUTO_CANCEL;
        builder.notificationDefaults=Notification.DEFAULT_LIGHTS|Notification.DEFAULT_SOUND;
        JPushInterface.setPushNotificationBuilder(1, builder);
        //初始化jpush后就停止服务 登录成功后在开启
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
		//原有逻辑
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
	 * 停止推送
	 */
	public void stoppush(){
		JPushInterface.stopPush(this.getApplicationContext());
	}
	/*
	 * 导航到根站点
	 */
	public void navigateRoot(Intent intent, String title){
		clearAllIntent();
		navigate(intent, title, false);
	}
	
	/*
	 * 导航
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
	 * 设置是否显示标题栏
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
	 * 后退
	 */
	public void navigateBack(){
		popIntent();
		Intent intent = popIntent();
		if(intent != null){
			navigate(intent);
		}
	}
	/*
	 * 后退判断
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
