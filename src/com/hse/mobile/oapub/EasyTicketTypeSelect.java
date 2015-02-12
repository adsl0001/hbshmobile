package com.hse.mobile.oapub;

import java.util.HashMap;

import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.IRestServiceListener;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.RestServiceTask;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

/*
 * 随手拍类型选择
 */
public class EasyTicketTypeSelect extends BaseActivity {
	Button btnJiLi, btnWeiZhang, btnSafetyHazard;
	boolean hasJiLiPermission = false;			//权限初始化
	boolean hasWeiZhangPermission = false;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.easytickets_type);
		btnJiLi = (Button)this.findViewById(R.id.button_tickets_jl);
		btnJiLi.setOnClickListener(onJiliClickListener);
		btnWeiZhang = (Button)this.findViewById(R.id.button_tickets_wz);
		btnWeiZhang.setOnClickListener(onWeiZhangClickListener);
		btnSafetyHazard = (Button)this.findViewById(R.id.button_safe);
		btnSafetyHazard.setOnClickListener(onSafetyHazardClickListener);
		
		checkPermission();
	}
	
	void checkPermission(){
		String url = Constant.URL_AUTHOR + ((HseApplication)this.getApplicationContext()).getLoginUserId();
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
					hasJiLiPermission = resultData.get("attach").toString().equals("1");
					hasWeiZhangPermission = resultData.get("ticket").toString().equals("1");	
				}else{
					Toast.makeText(EasyTicketTypeSelect.this, "加载权限信息失败!", Toast.LENGTH_SHORT).show();
				}
			}catch(Exception ex){
				
			}
		}
	};
	
	OnClickListener onJiliClickListener = new OnClickListener(){
		public void onClick(View v) {
			if(!hasJiLiPermission){
				Toast.makeText(EasyTicketTypeSelect.this, "您没有该权限", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Button target = (Button)v;
			Intent intent = new Intent(EasyTicketTypeSelect.this, IllegalTicket.class);
			intent.putExtra(MessageKey.TICKETS_TYPE, "positive");
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, target.getText().toString(), true);
		}
	};
	
	OnClickListener onWeiZhangClickListener = new OnClickListener(){
		public void onClick(View v) {
			if(!hasWeiZhangPermission){
				Toast.makeText(EasyTicketTypeSelect.this, "您没有该权限", Toast.LENGTH_SHORT).show();
				return;
			}
			
			Button target = (Button)v;
			Intent intent = new Intent(EasyTicketTypeSelect.this, IllegalTicket.class);
			intent.putExtra(MessageKey.TICKETS_TYPE, "illegal");
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, target.getText().toString(), true);
		}
	};	
	
	OnClickListener onSafetyHazardClickListener = new OnClickListener(){
		public void onClick(View v) {
			Button target = (Button)v;
			String url = Constant.URL_SAFETYHAZARD;
			Intent intent = new Intent(EasyTicketTypeSelect.this, CondovaActivity.class);
			//intent.putExtra(MessageKey.TICKETS_TYPE, "illegal");
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, target.getText().toString(), true);
			showTitlebar(false);
		}
	};	
}
