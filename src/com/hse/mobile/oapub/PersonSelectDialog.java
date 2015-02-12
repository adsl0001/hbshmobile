package com.hse.mobile.oapub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.IRestServiceListener;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.RestServiceTask;
import com.hse.mobile.oa.entity.Department;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 人员选择界面
 */
public class PersonSelectDialog extends BaseActivity {	
	ListView listView;
	
	String selectedName = null;
	String selectedId = null;
	Button btnOK, btnCancel;
	TextView txtTitle;

	List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.depart_select);
		btnOK = (Button)this.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOkClickListener);
		
		btnCancel =  (Button)this.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(onCancelClickListener);
		listView = (ListView)this.findViewById(R.id.listDepart);
		listView.setOnItemClickListener(onListItemClickListener);
		
		txtTitle = (TextView)this.findViewById(R.id.txtTitle);
		txtTitle.setText("选择审批人");
		
		loadData();
	}
	
	OnClickListener  onOkClickListener = new OnClickListener(){
		public void onClick(View v) {
			if(selectedId == null){
				Toast.makeText(PersonSelectDialog.this, "请选择审批人!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Intent intent = new Intent();
			intent.putExtra("value", selectedId);
			intent.putExtra("name", selectedName);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	};
	
	OnClickListener  onCancelClickListener = new OnClickListener(){
		public void onClick(View v) {
			setResult(Activity.RESULT_CANCELED);
			finish();
		}
	};
	
	
	OnItemClickListener onListItemClickListener = new OnItemClickListener(){
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			int rowIndex = arg2;
			selectedName = listData.get(rowIndex).get("name").toString();
			selectedId = listData.get(rowIndex).get("value").toString();
		}
	};
	
	void loadData(){
		String url = Constant.URL_NEXTPERSON ;
		showProgressDialog("", "正在加载...");
		new RestServiceTask(serviceListener).execute(url);
	}
	
	IRestServiceListener serviceListener = new IRestServiceListener(){
		public void onRestServiceResult(JSONObject result) {
			hideProgressDialog();
			
			try
			{
				if(result != null){
					HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
					List<HashMap<String, Object>> data = JsonDataConvert.parseArray((JSONArray)resultData.get("nextPersons"));
					listData.addAll(data);
					
					//Log.i("HseOA", String.format("Total=%d, show=%d", newsTotal, listData.size()));
					
					String[] names = new String[listData.size()];
					for(int i=0; i<listData.size(); i++){
						names[i] = listData.get(i).get("name").toString();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(PersonSelectDialog.this,
			                R.layout.simple_list_item_checked, names);
			        listView.setAdapter(adapter);        
			        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			
				}else{
					Toast.makeText(PersonSelectDialog.this, "加载审批人失败!", Toast.LENGTH_SHORT).show();
				}
			}catch(Exception ex){
				Log.e("HseMobileOA", "onRestServiceResult", ex);
			}
		}
	};
	
	

}
