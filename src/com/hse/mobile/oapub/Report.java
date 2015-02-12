package com.hse.mobile.oapub;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.ui.PageControl;
import com.hse.mobile.oa.util.ImageCache;
import com.hse.mobile.oapub.HseApplication;

/*
 * 报表
 */
public class Report extends BaseActivity implements OnGestureListener{
	public static final int PAGE_SIZE = 16;  //每页的图标数
	private static int verticalMinDistance = 20; 
	private static int minVelocity = 0;  
	ViewFlipper viewFlipper;
	GestureDetector  gestureDetector;
	List<HashMap<String, Object>> listData;
	List<List<HashMap<String, Object>>> gridData;
	GridView[] gridViews;
	//RadioGroup radioGroupPager;
	PageControl pager;
	GridView   gridView;
	int 	   totalPage = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.report);
		viewFlipper = (ViewFlipper)this.findViewById(R.id.report_viewFlipper);
		gestureDetector =new GestureDetector(this);
		((HseApplication)this.getApplicationContext()).setGestureDetector(gestureDetector);
		pager = (PageControl) this.findViewById(R.id.report_ctl_pager);
		pager.setIndicatorSize(12);
		loadData();
	}
	
	void loadData(){
		showProgressDialog("", "正在加载中...");
		String url =  Constant.URL_REPORT;
		new RestService().execute(url);
	}
	public void onRestServiceResult(JSONObject result) {
		if(result!=null){
			try{
				JSONArray iconList = result.getJSONArray("poralIconList");
				if(iconList.length()==0){
					hideProgressDialog();
					Toast.makeText(this, "您暂时没有报表的模块", Toast.LENGTH_SHORT).show();
					return;
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			try {
				JSONArray iconList = result.getJSONArray("poralIconList");
				int iconCount = iconList.length();
				listData = new ArrayList<HashMap<String, Object>>();
				
				//动态生成分页GridView
				totalPage = (int)Math.ceil(iconCount*1.0 / PAGE_SIZE);
				gridViews = new GridView[totalPage];
				
				for(int i=0;i<totalPage; i++){
					gridViews[i] = (GridView)this.getLayoutInflater().inflate(R.layout.portal_gridview, null);//new GridView(this);
					gridViews[i].setOnItemClickListener(this.onGridItemClickListener);
					gridViews[i].setOnTouchListener(this.onGridTouchListener);
					this.viewFlipper.addView(gridViews[i], i);
						
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
					listItem.put("title", setFourNumber(jsonItem.get("name").toString()));
					listItem.put("icon", null);
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
		else
		{
			Log.i("test","加载失败");
				Toast.makeText(this, "加载模块失败", Toast.LENGTH_SHORT).show();
			
		}
			hideProgressDialog();
	}
	OnItemClickListener onGridItemClickListener = new OnItemClickListener(){
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			int pageIndex = getPageIndex();
			try{
				String url = gridData.get(pageIndex).get(rowIndex).get("url").toString();
				String title = gridData.get(pageIndex).get(rowIndex).get("name").toString();
				Intent intent = new Intent(Report.this, CondovaActivity.class);
				intent.putExtra(MessageKey.URL, url);
				intent.putExtra(MessageKey.REUSEABLE, false);
				navigate(intent, title, true);
			}catch(Exception ex){
				//滑动过快时可能出错
			}
		}
	};
	OnTouchListener onGridTouchListener = new OnTouchListener(){
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			return gestureDetector.onTouchEvent(event);
		}
	};
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
	int getPageIndex(){
		if(viewFlipper.getChildCount()>0){
			return this.viewFlipper.getDisplayedChild();
		}
		else return 0;
	}
	@Override
	public boolean onDown(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		int index = this.viewFlipper.getDisplayedChild();
		if(Math.abs(e1.getY()-e2.getY())>Math.abs(e1.getX()-e2.getX())){
		}
		else if(index==0){
			Toast.makeText(this, "只有一页", Toast.LENGTH_SHORT).show();
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
	private void refreshPageInfo(){
		int index = this.viewFlipper.getDisplayedChild();
		Log.i("test", "翻第"+index+"页");
		pager.setCurrentPage(index);
	}
	protected void onDestroy() {
		((HseApplication)this.getApplicationContext()).setGestureDetector(null);
		super.onDestroy();
	}
	@Override
	public void onLongPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2,
			float arg3) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void onShowPress(MotionEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onSingleTapUp(MotionEvent arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	private class NameLengthFilter implements InputFilter {
		int MAX_EN;// 最大英文/数字长度 一个汉字算两个字母
		String regEx = "[\\u4e00-\\u9fa5]"; // unicode编码，判断是否为汉字

		public NameLengthFilter(int mAX_EN) {
			super();
			MAX_EN = mAX_EN;
		}

		@Override
		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			int destCount = dest.toString().length()
					+ getChineseCount(dest.toString());
			int sourceCount = source.toString().length()
					+ getChineseCount(source.toString());
			if (destCount + sourceCount > MAX_EN) {
				return "";

			} else {
				return source;
			}
		}

		private int getChineseCount(String str) {
			int count = 0;
			Pattern p = Pattern.compile(regEx);
			Matcher m = p.matcher(str);
			while (m.find()) {
				for (int i = 0; i <= m.groupCount(); i++) {
					count = count + 1;
				}
			}
			return count;
		}

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
