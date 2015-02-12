package com.hse.mobile.oapub;

import com.hbsh.beta.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class TextSetting extends Activity {
	ViewGroup bigsize,middlesize,smallesize;
	ImageView bigpng,middlepng,smallpng;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_setting);
		bigsize=(ViewGroup)findViewById(R.id.text_big);
		bigsize.setOnClickListener(onBig);
		middlesize=(ViewGroup)findViewById(R.id.text_middle);
		middlesize.setOnClickListener(onMiddle);
		smallesize=(ViewGroup)findViewById(R.id.text_small);
		smallesize.setOnClickListener(onSmall);
		bigpng=(ImageView)findViewById(R.id.big_png);
		middlepng=(ImageView)findViewById(R.id.middle_png);
		smallpng=(ImageView)findViewById(R.id.small_png);
		init();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.text_setting, menu);
		return true;
	}
	private void init(){
		//根据缓存判断字体大小
			SharedPreferences sp = this.getSharedPreferences("HseOAConfig", 0);
			String textsize=sp.getString("thetextsize", "middle");
			setTextSize(textsize);
	}
	//显示选择结果
	public void setTextSize(String size){
		bigpng.setVisibility(View.INVISIBLE);
		middlepng.setVisibility(View.INVISIBLE);
		smallpng.setVisibility(View.INVISIBLE);
		if(size.equals("big")){
			bigpng.setVisibility(View.VISIBLE);
		}
		else if(size.equals("middle")){
			middlepng.setVisibility(View.VISIBLE);
		}
		else if(size.equals("small")){
			smallpng.setVisibility(View.VISIBLE);
		}
	}
	//点击选择
	OnClickListener onBig=new OnClickListener() {
		public void onClick(View v) {
			setTextSize("big");
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			editor.putString("thetextsize", "big");
			editor.commit();
		}
	};
	OnClickListener onMiddle=new OnClickListener() {
		public void onClick(View v) {
			setTextSize("middle");
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			editor.putString("thetextsize", "middle");
			editor.commit();
		}
	};
	OnClickListener onSmall=new OnClickListener() {
		public void onClick(View v) {
			setTextSize("small");
			Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
			editor.putString("thetextsize", "small");
			editor.commit();
		}
	};
}
