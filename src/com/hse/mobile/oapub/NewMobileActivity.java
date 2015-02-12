package com.hse.mobile.oapub;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnGroupExpandListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.SimpleExpandableListAdapter;
import android.widget.Toast;

import com.hbsh.beta.R;
import com.hse.mobile.extapp.DZDPListActivity;
import com.hse.mobile.extapp.MLAreaListActivity;
import com.hse.mobile.oa.ui.SubGridView;
import com.ziteng.cn.test.SafetyLoophole;
import com.ziteng.cn.test.Weather;

public class NewMobileActivity extends BaseActivity {

	// private SubGridView mobileGridView;
	private final int[] mobileGridViewItemImgSrc0 = { R.drawable.zzzlbb,
			R.drawable.sgss, R.drawable.ewm, R.drawable.aqyh };
	private final int[] mobileGridViewItemImgSrc1 = { R.drawable.tianqiyubao,
			R.drawable.ewm, R.drawable.aqyh };
	private final String[] mobileGridViewItemTitle0 = { "报表", "工程随手拍", "二维码",
			"隐患随手拍" };
	private final String[] mobileGridViewItemTitle1 = { "天气预报", "身边美食", "身边优惠" };
	private String[] names = { "便捷工作", "周边生活" };
	private List<HashMap<String, Object>> mobileGridViewData0 = new ArrayList<HashMap<String, Object>>();
	private List<HashMap<String, Object>> mobileGridViewData1 = new ArrayList<HashMap<String, Object>>();

	private Context ctx;
	private final static int item_baobiao = 0;
	private final static int item_suishoupai = 1;
	private final static int item_erweima = 2;
	private final static int item_anquanyinhuan = 3;

	private final static int item_tianqiyubao = 0;
	private final static int item_shenbianmeishi = 1;
	private final static int item_shenbianyouhui = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobile_new);
		ctx = this;
		initView();
	}

	private void initView() {
		final ExpandableListView expandableListView = (ExpandableListView) this
				.findViewById(R.id.mobile_ExpandableListView);
		expandableListView.setCacheColorHint(0);
		int width = getWindowManager().getDefaultDisplay().getWidth();
		expandableListView.setIndicatorBounds(width - 40, width - 10);
		// 定义父列表项List数据集合
		List<Map<String, String>> groupData = new ArrayList<Map<String, String>>();
		// 定义子列表项List数据集合
		List<List<Map<String, String>>> ss = new ArrayList<List<Map<String, String>>>();
		for (int i = 0; i < names.length; i++) {
			Map<String, String> maps = new HashMap<String, String>();
			maps.put("name", names[i]);
			groupData.add(maps);
			List<Map<String, String>> child = new ArrayList<Map<String, String>>();
			Map<String, String> mapsj = new HashMap<String, String>();
			mapsj.put("button", names[i]);
			child.add(mapsj);
			ss.add(child);
		}

		final SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(
				ctx, groupData, R.layout.activity_mobile_new_item,
				new String[] { "name" },
				new int[] { R.id.mobile_item_textView1 }, ss,
				R.layout.activity_mobile_new_sub_item,
				new String[] { "button" }, new int[] { R.id.mobile_grid_new }) {
			@Override
			public int getChildrenCount(int groupPosition) {
				// TODO Auto-generated method stub
				return 1;
			}

			public View getChildView(int groupPosition, int childPosition,
					boolean isLastChild, View convertView, ViewGroup parent) {

				LayoutInflater layoutInflater = (LayoutInflater) ctx
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(
						R.layout.activity_mobile_new_sub_item, null);
				SubGridView mobileGridView = (SubGridView) convertView
						.findViewById(R.id.mobile_grid_new);
				mobileGridView.setAdapter(gridAdapter(groupPosition));
				setGrdiViewEvent(groupPosition, mobileGridView);

				return convertView;
			}

		};

		expandableListView.setAdapter(adapter);

	}

	private SimpleAdapter gridAdapter(int groupPosition) {
		System.out.println("groupPosition:" + groupPosition);
		switch (groupPosition) {
		case 0:
			mobileGridViewData0 = new ArrayList<HashMap<String, Object>>();
			for (int mobileGridDataIndex = 0; mobileGridDataIndex < mobileGridViewItemTitle0.length; mobileGridDataIndex++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("mobileGridViewItemImgSrc",
						mobileGridViewItemImgSrc0[mobileGridDataIndex]);
				map.put("mobileGridViewItemTitle",
						mobileGridViewItemTitle0[mobileGridDataIndex]);
				mobileGridViewData0.add(map);
			}
			return new SimpleAdapter(ctx, mobileGridViewData0,
					R.layout.gridview_mobile_item, new String[] {
							"mobileGridViewItemImgSrc",
							"mobileGridViewItemTitle" }, new int[] {
							R.id.gridview_mobile_item_image,
							R.id.gridview_mobile_item_text });
			// break;
		case 1:
			mobileGridViewData1 = new ArrayList<HashMap<String, Object>>();
			for (int mobileGridDataIndex = 0; mobileGridDataIndex < mobileGridViewItemTitle1.length; mobileGridDataIndex++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("mobileGridViewItemImgSrc",
						mobileGridViewItemImgSrc1[mobileGridDataIndex]);
				map.put("mobileGridViewItemTitle",
						mobileGridViewItemTitle1[mobileGridDataIndex]);
				mobileGridViewData1.add(map);
			}
			return new SimpleAdapter(ctx, mobileGridViewData1,
					R.layout.gridview_mobile_item, new String[] {
							"mobileGridViewItemImgSrc",
							"mobileGridViewItemTitle" }, new int[] {
							R.id.gridview_mobile_item_image,
							R.id.gridview_mobile_item_text });
		default:
			break;
		}
		return null;

	}

	private void setGrdiViewEvent(int groupPosition, GridView mobileGridView) {
		OnItemClickListener listener = null;

		switch (groupPosition) {
		case 0:
			listener = new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					switch (position) {
					case item_baobiao:
						Intent intent_baobiao = new Intent(ctx, Report.class);
						navigate(intent_baobiao, "报表", true);
						break;
					case item_suishoupai:
						Intent intent_suishoupai = new Intent(ctx,
								TakePhoto.class);
						navigate(intent_suishoupai, "工程施工随手拍", true);
						break;
					case item_erweima:
						//
						Log.i("test", "二维码");
						Intent intent_qrcode = new Intent(ctx,
								QrcodeScanActivity.class);
						navigate(intent_qrcode, "二维码扫描", true);
						break;
					case item_anquanyinhuan:
						Intent intent_anquanyinhuan = new Intent(ctx,
								SafetyLoophole.class);
						// intent_anquanyinhuan.putExtra(MessageKey.URL,
						// Constant.URL_ROOT+"/SecurityManager/safehazard/input");
						// intent_anquanyinhuan.putExtra(MessageKey.REUSEABLE,
						// false);
						// navigate(intent_anquanyinhuan, "安全隐患", true);
						navigate(intent_anquanyinhuan, "隐患随手拍", true);
						break;
					default:
						break;
					}
				}
			};
			break;
		case 1:
			listener = new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {

					switch (position) {
					case item_tianqiyubao:
						Intent intent_weather = new Intent(ctx, Weather.class);
						navigate(intent_weather, "天气预报", true);
						break;
					case item_shenbianmeishi:
						Intent intent_shenbianmeishi = new Intent(ctx,
								DZDPListActivity.class);
						navigate(intent_shenbianmeishi, "身边美食", true);
						break;
					case item_shenbianyouhui:
						Intent intent_shenbianyouhui = new Intent(ctx,
								MLAreaListActivity.class);
						navigate(intent_shenbianyouhui, "身边优惠", true);
						break;
					default:
						break;
					}
				}
			};
			break;

		default:
			break;
		}
		mobileGridView.setOnItemClickListener(listener);
	}
}
