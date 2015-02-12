package com.hse.mobile.oapub;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MessageKey;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/*
 * ÀÊ ÷≈ƒ
 */
public class EasyTickets extends BaseActivity {
	Button btnTakePhoto, btnSafetyHazard;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.easytickets);
		btnTakePhoto = (Button)this.findViewById(R.id.button_takephoto);
		btnTakePhoto.setOnClickListener(onTakePhotoClickListener);
		btnSafetyHazard = (Button)this.findViewById(R.id.button_safe);
		btnSafetyHazard.setOnClickListener(onSafetyHazardClickListener);
	}
	
	OnClickListener onTakePhotoClickListener = new OnClickListener(){
		public void onClick(View v) {
			Button target = (Button)v;
			Intent intent = new Intent(EasyTickets.this, TakePhoto.class);
			//intent.putExtra(MessageKey.TICKETS_TYPE, "positive");
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, "ÀÊ ÷≈ƒ-≈ƒ’’", true);
		}
	};
	
	OnClickListener onQueryClickListener = new OnClickListener(){
		public void onClick(View v) {
			Button target = (Button)v;
			Intent intent = new Intent(EasyTickets.this, EasyTicketsList.class);
			//intent.putExtra(MessageKey.TICKETS_TYPE, "illegal");
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, target.getText().toString(), true);
		}
	};	
	
	OnClickListener onSafetyHazardClickListener = new OnClickListener(){
		public void onClick(View v) {
			Button target = (Button)v;
			String url = Constant.URL_SAFETYHAZARD;
			Intent intent = new Intent(EasyTickets.this, CondovaActivity.class);
			//intent.putExtra(MessageKey.TICKETS_TYPE, "illegal");
			intent.putExtra(MessageKey.URL, url);
			intent.putExtra(MessageKey.REUSEABLE, false);
			navigate(intent, target.getText().toString(), true);
			showTitlebar(false);
		}
	};
}
