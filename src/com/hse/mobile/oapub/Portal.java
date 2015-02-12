package com.hse.mobile.oapub;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.GetBadgeNumberService;
import com.hse.mobile.oa.business.HandleMyTask;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.ui.PageControl;
import com.hse.mobile.oa.util.ImageCache;
import com.hse.mobile.oapub.HseApplication;
import com.hse.mobile.oapub.BaseActivity.RestService;
import com.hse.mobile.oapub.Home.getNumberTask;

/*
 * 快速入口
 */
public class Portal extends BaseActivity implements OnGestureListener{
	public static final int PAGE_SIZE = 16;  //每页的图标数
	private static int verticalMinDistance = 20; 
	private static int minVelocity         = 0;  
	ViewFlipper viewFlipper;
	Button mytask,mycollection,myauthorization;
	GestureDetector  gestureDetector;
	List<HashMap<String, Object>> listData;
	List<List<HashMap<String, Object>>> gridData;
	GridView[] gridViews;
	//RadioGroup radioGroupPager;
	PageControl pager;
	GridView   gridView;
	int 	   totalPage = 0;
	int isbegin=0;
	boolean isCollection=false;
	boolean isanthorization=false;
	boolean FirstLoadImage=true;
	boolean Firstcollection=true;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.portal);
		viewFlipper = (ViewFlipper)this.findViewById(R.id.viewFlipper);
		gestureDetector = new GestureDetector(this);
		((HseApplication)this.getApplicationContext()).setGestureDetector(gestureDetector);

		//radioGroupPager = (RadioGroup) this.findViewById(R.id.radiogroup_pager);
		pager = (PageControl) this.findViewById(R.id.ctl_pager);
		pager.setIndicatorSize(12);
		mytask=(Button)this.findViewById(R.id.mytask);
		mytask.setOnClickListener(onFlowClickListener);
		mycollection=(Button)this.findViewById(R.id.mycollection);
		mycollection.setOnClickListener(onCollection);
		myauthorization=(Button)this.findViewById(R.id.myauthorization);
		myauthorization.setOnClickListener(onAuthorization);
		//gridView = (GridView) this.findViewById(R.id.gridView1);
		//gridView.setOnItemClickListener(onItemClickListener);
		//fillGridView();
		mycollection.setSelected(true);
	}
	
	@Override
	protected void onStart() {
		Log.i("test", "onStart");
		super.onStart();
		//super.setTitleBarRightButton("代办流程", onFlowClickListener);
	}
	//
	@Override
	protected void onResume() {
		Log.i("test", "onResume");
		switchTaskOrPortal();
		super.onResume();
//		super.setTitleBarRightButton(" 待办流程 ", onFlowClickListener);
	}
	
	public void onResumeFromExist(){
		Log.i("test", "onResumeFromExist");
		switchTaskOrPortal();
//		super.setTitleBarRightButton(" 待办流程  ", onFlowClickListener);
	}
	
	@Override
	protected void onPause() {
		Log.i("Portal", "onPause");
		super.onPause();
		super.setTitleBarRightButton("", null);
	}
	
	@Override
	protected void onStop() {
		Log.i("Portal", "onStop");
		super.onStop();
		//super.setTitleBarRightButton("", null);
	}
	
	int getPageIndex(){
		if(viewFlipper.getChildCount()>0){
			return this.viewFlipper.getDisplayedChild();
		}
		else return 0;
	}
	OnClickListener onFlowClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			String url = Constant.URL_TASKLIST;
			Intent intent = new Intent(Portal.this, CondovaActivity.class);
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "待办流程", true);
			//showTitlebar(false);
			/*
			Intent intent = new Intent(Portal.this, Flow.class);
			navigate(intent, "待办流程", false);
			*/
		}
	};
	OnClickListener onAuthorization=new OnClickListener() {
		public void onClick(View v) {
//			myauthorization.setSelected(true);
//			mycollection.setSelected(false);
			//授权的图标
			isanthorization=true;
			loadData();
		}
	};
	OnClickListener onCollection=new OnClickListener() {
		public void onClick(View v) {
			//收藏的图标
//			mycollection.setSelected(true);
//			myauthorization.setSelected(false);
			isCollection=true;
			myloadData();
		}
	};
	void switchTaskOrPortal(){
		Log.i("test", "switchTaskOrPortal");
		String HandleNewTask=Constant.getMytaskNumber();
		Boolean backFromTask=Constant.getBackfromMyTask();
		if(backFromTask){
			if(Firstcollection){
				Firstcollection=false;
				myloadData();
			}
			return;
		}
		if(HandleNewTask.equals("0")){
			//跳转到待办任务
			Constant.setBackfromMyTask(true);
			Log.i("test","跳转到待办");
			String url = Constant.URL_TASKLIST;
			Intent intent = new Intent(Portal.this, CondovaActivity.class);
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "待办流程", true);
		}
		else{
			Log.i("test","跳转到Portal");
			if(Firstcollection){
				Firstcollection=false;
				myloadData();
			}
		}
	}
	void loadData(){
		FirstLoadImage=false;
		showProgressDialog("", "正在加载中...");
		String url =  Constant.URL_PORTAL_DATA;
		new RestService().execute(url);
//		if(FirstLoadImage){
//			FirstLoadImage=false;
//			showProgressDialog("", "正在加载中...");
//			String url =  Constant.URL_PORTAL_DATA;
//			new RestService().execute(url);
//			
//		}
//		else{
//			return;
//		}
		
	}
	void myloadData(){
		showProgressDialog("", "正在加载中...");
		String url =  Constant.URL_PRRTAL_COLLECTION;
		new RestService().execute(url);
	}
	@Override
	public void onRestServiceResult(JSONObject result) {
		if(result!=null){
			try{
				JSONArray iconList = result.getJSONArray("poralIconList");
				if(iconList.length()==0){
					hideProgressDialog();
					Toast.makeText(this, "您暂时没有收藏的模块", Toast.LENGTH_SHORT).show();
					myauthorization.setSelected(true);
					mycollection.setSelected(false);
					mytask.setSelected(false);
					loadData();
					return;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			try {
				//清除数据
				if(isbegin!=0){
					pager.setCurrentPage(0);
					listData.clear();
					gridData.clear();
					for(int i=0;i<gridViews.length;i++){
						gridViews[i].setAdapter(null);
					}
					if(isanthorization){
						mycollection.setSelected(false);
						myauthorization.setSelected(true);
						isanthorization=false;
					}
					else if(isCollection){
						mycollection.setSelected(true);
						myauthorization.setSelected(false);
						isCollection=false;
					}
					viewFlipper.removeAllViews();
					pager.removeAllViews();
				}
				isbegin++;
				JSONArray iconList = result.getJSONArray("poralIconList");
				int iconCount = iconList.length();
				listData = new ArrayList<HashMap<String, Object>>();
				
				//动态生成分页GridView
				totalPage = (int)Math.ceil(iconCount*1.0 / PAGE_SIZE);
				gridViews = new GridView[totalPage];
				
				//int cellHeight = (viewFlipper.getHeight()-20)/4;
				//int itemHeight = 80;//this.getLayoutInflater().inflate(R.layout.gridview_item, null).getHeight();
				
				for(int i=0;i<totalPage; i++){
					gridViews[i] = (GridView)this.getLayoutInflater().inflate(R.layout.portal_gridview, null);//new GridView(this);
					//gridViews[i].setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
					//gridViews[i].setNumColumns(4);
					//gridViews[i].setVerticalSpacing(10);
					gridViews[i].setOnItemClickListener(this.onGridItemClickListener);
					gridViews[i].setOnTouchListener(this.onGridTouchListener);
					this.viewFlipper.addView(gridViews[i], i);
						
//					RadioButton radioButton =(RadioButton)this.getLayoutInflater().inflate(R.layout.radio_pager, null);
					//this.radioGroupPager.addView(radioButton);
				}
				pager.setPageCount(totalPage);
				pager.setCurrentPage(0);
				//Log.i("View count", "" + viewFlipper.getChildCount());
				
				//			
				String[] iconUrls = new String[iconList.length()];
				for(int i=0; i<iconList.length(); i++){
					JSONObject jsonItem = iconList.getJSONObject(i);
					HashMap<String, Object> listItem = new HashMap<String, Object>();
					listItem.put("name", jsonItem.get("name").toString());
					listItem.put("iconurl", jsonItem.get("iconurl").toString());
					listItem.put("url", jsonItem.get("url").toString());
					listItem.put("icon", null);
					listItem.put("title", setFourNumber(jsonItem.get("name").toString()));
					listData.add(listItem);
					iconUrls[i] = jsonItem.get("iconurl").toString();
				}
				//refreshGridView();
				
				//异步加载图片
				ImageCache.asyncDownloadImages(iconUrls, handler);
			} catch (JSONException e) {
				e.printStackTrace();
				hideProgressDialog();
			}
		}
		else{
			Log.i("test","加载失败");
			if(isCollection){
				Toast.makeText(this, "加载授权图标失败！", Toast.LENGTH_SHORT).show();
				isCollection=false;
			}
			else if(isanthorization){
				Toast.makeText(this, "加载收藏模块失败", Toast.LENGTH_SHORT).show();
				isanthorization=false;
			}
			hideProgressDialog();
		}
	}
	
    Handler handler = new Handler(new Handler.Callback(){
    	public boolean handleMessage(android.os.Message msg) {
    		switch(msg.what){
    			case ImageCache.IMAGE_DOWNLOAD_FINISH:
    				Bitmap[] images = (Bitmap[])msg.obj;
    				for(int i=0; i<listData.size(); i++){
    					String iconUrl = listData.get(i).get("iconurl").toString();
    					Bitmap image = images[i];//ImageCache.getImage(iconUrl);
    					
    					if(image != null){
        		        	Drawable drawable = new BitmapDrawable(image);
        		        	int width = image.getWidth();//drawable.getIntrinsicWidth();
        		            int height = image.getHeight();//drawable.getIntrinsicHeight();
        		            drawable.setBounds(0, 0, width, height);
        					listData.get(i).put("icon", drawable);
    					}
    				}
    				refreshGridView();
    				hideProgressDialog();
    			break;
    		}
    		
    		return true;
    	}
    });
    
	@Override
	protected void onDestroy() {
		((HseApplication)this.getApplicationContext()).setGestureDetector(null);
		super.onDestroy();
	}
	
	void refreshGridView(){
		int currentPage = getPageIndex();
		gridData = new ArrayList<List<HashMap<String, Object>>>();
		for(int i=0; i<totalPage; i++){
			int start = i*PAGE_SIZE;
			int end = (i+1)*PAGE_SIZE;
			if(end > listData.size()){
				end = listData.size();
			}
			gridData.add(listData.subList(start, end));
			SimpleAdapter adapter = new SimpleAdapter(this
					, gridData.get(i)
					, R.layout.gridview_item
					, new String[]{"title", "icon"}
					, new int[]{R.id.gridview_item_text, R.id.gridview_item_image});
			adapter.setViewBinder(iconViewBinder);
			gridViews[i].setAdapter(adapter);
		}
	}
	
	ViewBinder iconViewBinder = new ViewBinder(){

		@Override
		public boolean setViewValue(View view, Object data,
				String textRepresentation) {
			if(view instanceof ImageView){
				ImageView imageView = (ImageView)view;
				if(data instanceof Bitmap){
					Bitmap bitmap = (Bitmap)data;
					imageView.setImageBitmap(bitmap);
				}else if(data instanceof Drawable){
					Drawable drawable = (Drawable)data;
					imageView.setImageDrawable(drawable);
				}else{
					return false;
				}
				
				return true;
			}
			
			return false;
		}
	};
	
	OnTouchListener onGridTouchListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}
	};
	
	OnItemClickListener onGridItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			int pageIndex = getPageIndex();
			try{
				String url = gridData.get(pageIndex).get(rowIndex).get("url").toString();
				String title = gridData.get(pageIndex).get(rowIndex).get("name").toString();
				Intent intent = new Intent(Portal.this, CondovaActivity.class);
				intent.putExtra(MessageKey.URL, url);
				intent.putExtra(MessageKey.REUSEABLE, false);
				navigate(intent, title, true);
				//showTitlebar(false);
				onLeave();
			}catch(Exception ex){
				//滑动过快时可能出错
			}
		}
	};
	
	private void refreshPageInfo(){
		int index = this.viewFlipper.getDisplayedChild();
		pager.setCurrentPage(index);
		/*
		RadioButton selItem = (RadioButton)radioGroupPager.getChildAt(index);
		if(selItem != null){
			selItem.setChecked(true);
		}*/
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return this.gestureDetector.onTouchEvent(event);
	}
	
	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(Math.abs(e1.getY()-e2.getY())>Math.abs(e1.getX()-e2.getX())){
			
		}
		else if(e1.getX() - e2.getX() > verticalMinDistance 
				&& Math.abs(velocityX) > minVelocity){
			//左翻页
			viewFlipper.setInAnimation(getApplicationContext(), R.animator.push_left_in);   
	        viewFlipper.setOutAnimation(getApplicationContext(), R.animator.push_left_out);	
			this.viewFlipper.showNext();
			refreshPageInfo();
		}else if(e2.getX() - e1.getX() > verticalMinDistance 
				&& Math.abs(velocityX) > minVelocity){
			//右翻页
			viewFlipper.setInAnimation(getApplicationContext(), R.animator.push_right_in);   
	        viewFlipper.setOutAnimation(getApplicationContext(), R.animator.push_right_out);			
			this.viewFlipper.showPrevious();
			refreshPageInfo();
		}

		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {	
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}
	
	private void onLeave(){
		super.setTitleBarRightButton("", null);
	}
	//处理带英文的返回4字中文
	private String setFourNumber(String str){
		StringBuffer result=new StringBuffer();
		int count=0;
		for(int i=0;i<str.length();i++){
			String s=str.charAt(i)+"";
			byte[] by;
			try {
				by = s.getBytes("gbk");
				count=count+by.length;
				if(count>8){
					break;
				}
				else{
					result.append(s);
				}
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result.toString();
	}
}
