package com.hse.mobile.oapub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hse.mobile.extapp.DZDPListActivity;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MessageKey;
import com.hbsh.beta.R;
import com.ziteng.cn.test.SafetyLoophole;
import com.ziteng.cn.test.Weather;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;

public class MobileActivity extends BaseActivity {
	
	private GridView mobileGridView;
	private final int[]mobileGridViewItemImgSrc={R.drawable.zzzlbb,R.drawable.sgss,R.drawable.tianqiyubao,R.drawable.ewm,R.drawable.aqyh,R.drawable.aqyh};
	private final String[]mobileGridViewItemTitle={"报表","工程随手拍","天气预报","二维码","隐患随手拍","身边美食"};
	private SimpleAdapter mobileAdapter;
	private List<HashMap<String, Object>>mobileGridViewData=new ArrayList<HashMap<String,Object>>();
	private Context ctx;
	private final static int item_baobiao=0;
	private final static int item_suishoupai=1;
	private final static int item_tianqiyubao=2;
	private final static int item_erweima=3;
	private final static int item_anquanyinhuan=4;
	private final static int item_shenbianmeishi=5;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobile);
		mobileGridView=(GridView)this.findViewById(R.id.mobile_grid);
		ctx=this;
		setGrdiViewData();
		showGridView();
	}
	private void setGrdiViewData(){
		for(int mobileGridDataIndex=0;mobileGridDataIndex<mobileGridViewItemTitle.length;mobileGridDataIndex++){
			HashMap<String, Object>map=new HashMap<String, Object>();
			map.put("mobileGridViewItemImgSrc", mobileGridViewItemImgSrc[mobileGridDataIndex]);
			map.put("mobileGridViewItemTitle", mobileGridViewItemTitle[mobileGridDataIndex]);
			mobileGridViewData.add(map);
		}
		mobileAdapter=new SimpleAdapter(ctx, mobileGridViewData, R.layout.gridview_mobile_item, new String[]{"mobileGridViewItemImgSrc","mobileGridViewItemTitle"}, new int[]{R.id.gridview_mobile_item_image,R.id.gridview_mobile_item_text});
		mobileGridView.setAdapter(mobileAdapter);
		mobileGridView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.i("test", "点击第"+position+"个图标");
				switch (position) {
				case item_baobiao:
					Intent intent_baobiao=new Intent(ctx,Report.class);
					navigate(intent_baobiao, "报表", true);
					break;
				case item_suishoupai:
					Intent intent_suishoupai=new Intent(ctx, TakePhoto.class);
					navigate(intent_suishoupai, "工程施工随手拍", true);
					break;
				case item_tianqiyubao:
					Intent intent_weather=new Intent(ctx, Weather.class);
					navigate(intent_weather, "天气预报", true);
					break;
				case item_erweima:
					//
					Log.i("test", "二维码");
					Intent intent_qrcode=new Intent(ctx, QrcodeScanActivity.class);
					navigate(intent_qrcode, "二维码扫描", true);
					break;
				case item_anquanyinhuan:
					Intent intent_dzdp=new Intent(ctx, SafetyLoophole.class);
//					intent_anquanyinhuan.putExtra(MessageKey.URL, Constant.URL_ROOT+"/SecurityManager/safehazard/input");
//					intent_anquanyinhuan.putExtra(MessageKey.REUSEABLE, false);
//					navigate(intent_anquanyinhuan, "安全隐患", true);
					navigate(intent_dzdp, "身边美食", true);
					break;
				case item_shenbianmeishi:
					Intent intent_shenbianmeishi=new Intent(ctx, DZDPListActivity.class);
					navigate(intent_shenbianmeishi, "身边美食", true);
					break;
				default:
					break;
				}
			}
		});
	}
	private void showGridView(){
		
	}
}
