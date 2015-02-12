package com.hse.mobile.oapub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.IRestServiceListener;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.RestServiceTask;
import com.hse.mobile.oa.ui.SimpleGroupAdapter;
import com.hbsh.beta.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

/*
 * 代办流程(已废弃)
 */
public class Flow extends BaseActivity{
	ListView listView;
	List<HashMap<String,Object>>  listData;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.flow);
		listView = (ListView) this.findViewById(R.id.listview_portal);
		listView.setOnItemClickListener(onListItemClickListener);

		loadData();
	}
	
	void refreshListView(){
		ListAdapter adapter = new SimpleGroupAdapter(this
				, listData
				, R.layout.list_item_portal
				, R.layout.list_item_portal_groupheader
				, new String []{"name"}
				, new int []{R.id.list_item_portal_title}
				, "groupHeader");
		listView.setAdapter(adapter);
	}
	
	void loadData(){
		showProgressDialog("", "正在加载...");
		String url = Constant.URL_PORTAL_PROCESS;
		new RestServiceTask(serviceListener).execute(url);
	}
	
	IRestServiceListener serviceListener = new IRestServiceListener(){
		public void onRestServiceResult(JSONObject result) {
			hideProgressDialog();
			
			try
			{
				if(result != null){
					listData = new ArrayList<HashMap<String,Object>>();
					HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
					List<HashMap<String, Object>> groups = JsonDataConvert.parseArray((JSONArray)resultData.get("groups"));
					for(HashMap<String, Object> group:groups){		
						HashMap<String,Object> groupHeader = new HashMap<String,Object>();
						groupHeader.put("name", group.get("name"));
						groupHeader.put("groupHeader", true);
						listData.add(groupHeader);
						
						List<HashMap<String, Object>> items = JsonDataConvert.parseArray((JSONArray)group.get("entries"));
						listData.addAll(items);
					}
					
					refreshListView();
				}else{
					Toast.makeText(Flow.this, "加载失败!", Toast.LENGTH_SHORT).show();
				}
			}catch(Exception ex){
				Log.e("HseMobileOA", "onRestServiceResult", ex);
			}
		}
	};
		
	@Override
	protected void onStart() {
		Log.i("Flow", "onStart");
		super.onStart();
		super.setTitleBarRightButton(" 快速入口 ", onFlowClickListener);
	}
	
	public void onResumeFromExist(){
		super.setTitleBarRightButton(" 快速入口 ", onFlowClickListener);
	}
	
	@Override
	protected void onStop() {
		Log.i("Flow", "onStop");
		super.onStop();
		super.setTitleBarRightButton("", null);
	}
	
	OnClickListener onFlowClickListener = new OnClickListener(){
		@Override
		public void onClick(View v) {
			Intent intent = new Intent(Flow.this, Portal.class);
			navigate(intent, "快速入口", false);
		}
	};
	
	OnItemClickListener onListItemClickListener = new OnItemClickListener(){

		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			String url = listData.get(rowIndex).get("url").toString();
			Intent intent = new Intent(Flow.this, CondovaActivity.class);
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "", true);
			showTitlebar(false);
			//startActivity(intent);
			
			onLeave();
		}
	};
	
	private void onLeave(){
		super.setTitleBarRightButton("", null);
	}
}
