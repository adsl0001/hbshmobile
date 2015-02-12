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
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

/*
 * 单位选择
 */
public class DepartSelectDialog extends BaseActivity {
	
	/*
	public interface OnDepartmentSetListener{
		public void OnDepartmentSet(Department department);
	}
	*/
	
	String catgetory;
	ListView listView;
	Department selectedDepart;
	//OnDepartmentSetListener onDepratSet;
	Button btnOK, btnCancel;

	List<HashMap<String, Object>> listData = new ArrayList<HashMap<String, Object>>();
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		catgetory = this.getIntent().getStringExtra(MessageKey.CATEGORY);
		//this.getIntent().get
		
		this.setContentView(R.layout.depart_select);
		btnOK = (Button)this.findViewById(R.id.btnOK);
		btnOK.setOnClickListener(onOkClickListener);
		
		btnCancel =  (Button)this.findViewById(R.id.btnCancel);
		btnCancel.setOnClickListener(onCancelClickListener);
		listView = (ListView)this.findViewById(R.id.listDepart);
		listView.setOnItemClickListener(onListItemClickListener);
		loadDepart();
	}
	
	OnClickListener  onOkClickListener = new OnClickListener(){
		public void onClick(View v) {
			if(selectedDepart == null){
				Toast.makeText(DepartSelectDialog.this, "请选择单位!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Intent intent = new Intent();
			intent.putExtra("clientId", selectedDepart.getId());
			intent.putExtra("clientName", selectedDepart.getName());
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
			String name = listData.get(rowIndex).get("clientName").toString();
			String id = listData.get(rowIndex).get("clientId").toString();
			
			selectedDepart = new Department(id, name);
		}
	};
	
	void loadDepart(){
		String url = Constant.URL_DEPART_LIST + catgetory;
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
					List<HashMap<String, Object>> data = JsonDataConvert.parseArray((JSONArray)resultData.get("clientList"));
					listData.addAll(data);
					
					//Log.i("HseOA", String.format("Total=%d, show=%d", newsTotal, listData.size()));
					
					String[] names = new String[listData.size()];
					for(int i=0; i<listData.size(); i++){
						names[i] = listData.get(i).get("clientName").toString();
					}
					ArrayAdapter<String> adapter = new ArrayAdapter<String>(DepartSelectDialog.this,
			                R.layout.simple_list_item_checked, names);
			        listView.setAdapter(adapter);        
			        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
			
			        /*
					SimpleAdapter adapter = new SimpleAdapter(DepartSelectDialog.this
							, listData
							, R.layout.list_item_depart
							,new String []{"clientName"}
							,new int []{R.id.list_item_depart_name
						});  
					listView.setAdapter(adapter);
					*/
				}else{
					Toast.makeText(DepartSelectDialog.this, "加载单位失败!", Toast.LENGTH_SHORT).show();
				}
			}catch(Exception ex){
				Log.e("HseMobileOA", "onRestServiceResult", ex);
			}
		}
	};

}
