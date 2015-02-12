package com.ziteng.cn.test;

import java.util.List;

import org.ksoap2.serialization.SoapObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.WebServiceUtil;
import com.hse.mobile.oapub.BaseActivity;
import com.squareup.okhttp.internal.DiskLruCache.Editor;

public class Weather extends  BaseActivity {
	
	private static final int CITY = 0x11;
	private String city_str;
	private Spinner province_spinner;
	private Spinner city_spinner;
	private List<String> provinces; 
	private List<String> citys;
	private SharedPreferences preference;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_weather);
		//preference = getSharedPreferences("weather", 0);
		
		findViewById(R.id.content_today_layout).getBackground().setAlpha(120);
		findViewById(R.id.content_small_bg1).getBackground().setAlpha(120);
		findViewById(R.id.content_small_bg2).getBackground().setAlpha(120);
		findViewById(R.id.content_small_bg3).getBackground().setAlpha(120);
		
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		city_str = this.readSharpPreference();
		Log.i("TAG", "��ȡ����"+city_str);
		super.setTitleBarRightButton(city_str,onCity);
		refresh(city_str);
	}
	@Override
	public void onResumeFromExist() {
		// TODO Auto-generated method stub
		super.onResumeFromExist();
		city_str = this.readSharpPreference();
		Log.i("TAG", "��ȡ����"+city_str);
		super.setTitleBarRightButton(city_str,onCity);
		refresh(city_str);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.weather, menu);
		return true;
	}
	
	OnClickListener onCity=new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			show_dialog(CITY);
		}
	};
	public String readSharpPreference(){
		//����Ĭ�ϳ���
		SharedPreferences sp = this.getSharedPreferences("HseOAConfig", 0);
		String city=sp.getString("city", "����");
		return city;
	}
	protected void refresh(String city_str)
	{
		super.setTitleBarRightButton(city_str,onCity);
		SoapObject detail = WebServiceUtil.getWeatherByCity(city_str);
		
		try
		{
			// ȡ��<string>10��13�� ����תС��</string>�е�����
			String date = detail.getProperty(7).toString();
			// ��"10��13�� ����תС��"��ֳ���������
			String[] date_array = date.split(" ");
			TextView today_text = (TextView) findViewById(R.id.today);
			today_text.setText(date_array[0]);

			// ȡ��<string>���� ����</string>�е�����
			TextView city_text = (TextView) findViewById(R.id.city_text);
			city_text.setText(detail.getProperty(1).toString());

			TextView today_weather = (TextView) findViewById(R.id.today_weather);
			today_weather.setText(date_array[1]);

			// ȡ��<string>15��/21��</string>�е�����
			TextView qiweng_text = (TextView) findViewById(R.id.qiweng);
			qiweng_text.setText(detail.getProperty(8).toString());

			// ȡ��<string>��������ʵ�������£�20�棻����/���������Ϸ�
			// 2����ʪ�ȣ�79%</string>�е�����,��ͨ��":"��ֳ�����
			TextView shidu_text = (TextView) findViewById(R.id.shidu);
			String date1 = detail.getProperty(4).toString();
			shidu_text.setText(date1.split("��")[4]);

			// ȡ��<string>������3-4��</string>�е�����
			TextView fengli_text = (TextView) findViewById(R.id.fengli);
			fengli_text.setText(detail.getProperty(9).toString());

			// ȡ��<string>��������������������ǿ�ȣ�����</string>�е�����,��ͨ��";"���,��ͨ��":"���,�������,ȡ��������Ҫ������
			String date2 = detail.getProperty(5).toString();
			String[] date2_array = date2.split("��");
			TextView kongqi_text = (TextView) findViewById(R.id.kongqi);
			kongqi_text.setText(date2_array[0].split("��")[1]);

			TextView zhiwai_text = (TextView) findViewById(R.id.zhiwai);
			zhiwai_text.setText(date2_array[1].split("��")[1]);

			// ����С��ʿ����
			// <string>����ָ��������ˬ�������ų�������ӵ���ȴ������װ������������������֯�����������׺ͳ��㡣��ðָ������Ȼ�¶����˵������ϴ��Խ��׷�����ð�����ʽ�����������ע���ʵ�������
			//�˶�ָ�������죬�����˿�չ���ֻ������˶���ϴ��ָ�����ϲ���ϴ����·��������ˮ�����ִ���ϴ������Ҫ���ý�����ˮ������׼������ɹָ��������������������ˮ�ֵ�Ѹ����������̫������ɹ������Ҫ��ɹ���뾡��ѡ��ͨ��ĵص㡣
			//����ָ�������죬���Դ󣬵��¶����ˣ�������˵���Ǻ��������������������������Σ������Ծ������ܴ���Ȼ�ķ�⡣·��ָ�������죬·��Ƚϸ��·���Ϻá����ʶ�ָ�����¶����ˣ����������������������������£���е��Ƚ���ˬ�����ʡ�
			//������Ⱦָ�����������������ڿ�����Ⱦ��ϡ�͡���ɢ����������������������������ָ�������������߷��������������ر�������������ڻ��⣬����Ϳ��SPF��8-12֮��ķ�ɹ����Ʒ��</string>
			String[] xiaotieshi = detail.getProperty(6).toString().split("\n");
			TextView xiaotieshi_text = (TextView) findViewById(R.id.xiaotieshi);
			xiaotieshi_text.setText(xiaotieshi[0]);

			// ���õ���ͼƬ
			ImageView image = (ImageView) findViewById(R.id.imageView1);
			int icon = parseIcon(detail.getProperty(10).toString());
			image.setImageResource(icon);

			// ȡ�õڶ�����������
			String[] date_str = detail.getProperty(12).toString().split(" ");
			TextView tomorrow_date = (TextView) findViewById(R.id.tomorrow_date);
			tomorrow_date.setText(date_str[0]);

			TextView tomorrow_qiweng = (TextView) findViewById(R.id.tomorrow_qiweng);
			tomorrow_qiweng.setText(detail.getProperty(13).toString());

			TextView tomorrow_tianqi = (TextView) findViewById(R.id.tomorrow_tianqi);
			tomorrow_tianqi.setText(date_str[1]);

			ImageView tomorrow_image = (ImageView) findViewById(R.id.tomorrow_image);
			int icon1 = parseIcon(detail.getProperty(15).toString());
			tomorrow_image.setImageResource(icon1);

			// ȡ�õ�������������
			String[] date_str1 = detail.getProperty(17).toString().split(" ");
			TextView afterday_date = (TextView) findViewById(R.id.afterday_date);
			afterday_date.setText(date_str1[0]);

			TextView afterday_qiweng = (TextView) findViewById(R.id.afterday_qiweng);
			afterday_qiweng.setText(detail.getProperty(18).toString());

			TextView afterday_tianqi = (TextView) findViewById(R.id.afterday_tianqi);
			afterday_tianqi.setText(date_str1[1]);

			ImageView afterday_image = (ImageView) findViewById(R.id.afterday_image);
			int icon2 = parseIcon(detail.getProperty(20).toString());
			afterday_image.setImageResource(icon2);

			// ȡ�õ�������������
			String[] date_str3 = detail.getProperty(22).toString().split(" ");
			TextView nextday_date = (TextView) findViewById(R.id.nextday_date);
			nextday_date.setText(date_str3[0]);

			TextView nextday_qiweng = (TextView) findViewById(R.id.nextday_qiweng);
			nextday_qiweng.setText(detail.getProperty(23).toString());

			TextView nextday_tianqi = (TextView) findViewById(R.id.nextday_tianqi);
			nextday_tianqi.setText(date_str3[1]);

			ImageView nextday_image = (ImageView) findViewById(R.id.nextday_image);
			int icon3 = parseIcon(detail.getProperty(25).toString());
			nextday_image.setImageResource(icon3);

		} catch (Exception e)
		{
			showTast(detail.getProperty(0).toString().split("��")[0]);
		}

	}
	private int parseIcon(String strIcon)
	{
		if (strIcon == null)
			return -1;
		if ("0.gif".equals(strIcon))
			return R.drawable.a_0;
		if ("1.gif".equals(strIcon))
			return R.drawable.a_1;
		if ("2.gif".equals(strIcon))
			return R.drawable.a_2;
		if ("3.gif".equals(strIcon))
			return R.drawable.a_3;
		if ("4.gif".equals(strIcon))
			return R.drawable.a_4;
		if ("5.gif".equals(strIcon))
			return R.drawable.a_5;
		if ("6.gif".equals(strIcon))
			return R.drawable.a_6;
		if ("7.gif".equals(strIcon))
			return R.drawable.a_7;
		if ("8.gif".equals(strIcon))
			return R.drawable.a_8;
		if ("9.gif".equals(strIcon))
			return R.drawable.a_9;
		if ("10.gif".equals(strIcon))
			return R.drawable.a_10;
		if ("11.gif".equals(strIcon))
			return R.drawable.a_11;
		if ("12.gif".equals(strIcon))
			return R.drawable.a_12;
		if ("13.gif".equals(strIcon))
			return R.drawable.a_13;
		if ("14.gif".equals(strIcon))
			return R.drawable.a_14;
		if ("15.gif".equals(strIcon))
			return R.drawable.a_15;
		if ("16.gif".equals(strIcon))
			return R.drawable.a_16;
		if ("17.gif".equals(strIcon))
			return R.drawable.a_17;
		if ("18.gif".equals(strIcon))
			return R.drawable.a_18;
		if ("19.gif".equals(strIcon))
			return R.drawable.a_19;
		if ("20.gif".equals(strIcon))
			return R.drawable.a_20;
		if ("21.gif".equals(strIcon))
			return R.drawable.a_21;
		if ("22.gif".equals(strIcon))
			return R.drawable.a_22;
		if ("23.gif".equals(strIcon))
			return R.drawable.a_23;
		if ("24.gif".equals(strIcon))
			return R.drawable.a_24;
		if ("25.gif".equals(strIcon))
			return R.drawable.a_25;
		if ("26.gif".equals(strIcon))
			return R.drawable.a_26;
		if ("27.gif".equals(strIcon))
			return R.drawable.a_27;
		if ("28.gif".equals(strIcon))
			return R.drawable.a_28;
		if ("29.gif".equals(strIcon))
			return R.drawable.a_29;
		if ("30.gif".equals(strIcon))
			return R.drawable.a_30;
		if ("31.gif".equals(strIcon))
			return R.drawable.a_31;
		return 0;
	}
	public void showTast(String string)
	{
		Toast.makeText(Weather.this, string, 1).show();

	}
	public void show_dialog(int cityId)
	{

		switch (cityId)
		{
		case CITY:
			// ȡ��city_layout.xml�е���ͼ
			final View view = LayoutInflater.from(this).inflate(
					R.layout.city_layout, null);

			// ʡ��Spinner
			province_spinner = (Spinner) view
					.findViewById(R.id.province_spinner);
			// ����Spinner
			city_spinner = (Spinner) view.findViewById(R.id.city_spinner);

			// ʡ���б�
			provinces = WebServiceUtil.getProvinceList();

			ArrayAdapter adapter = new ArrayAdapter(this,
					android.R.layout.simple_spinner_item, provinces);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			province_spinner.setAdapter(adapter);
			// ʡ��Spinner������
			province_spinner
					.setOnItemSelectedListener(new OnItemSelectedListener()
					{

						@Override
						public void onItemSelected(AdapterView<?> arg0,
								View arg1, int position, long arg3)
						{

							citys = WebServiceUtil
									.getCityListByProvince(provinces
											.get(position));
							ArrayAdapter adapter1 = new ArrayAdapter(
									Weather.this,
									android.R.layout.simple_spinner_item, citys);
							adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
							city_spinner.setAdapter(adapter1);

						}

						@Override
						public void onNothingSelected(AdapterView<?> arg0) {
							// TODO Auto-generated method stub
							
						}
					});

			// ����Spinner������
			city_spinner.setOnItemSelectedListener(new OnItemSelectedListener()
			{

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3)
				{
					city_str = citys.get(position);
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0)
				{

				}
			});

			// ѡ����жԻ���
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setTitle("��ѡ����������");
			dialog.setView(view);
			dialog.setPositiveButton("ȷ��",
					new DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							writeSharpPreference(city_str);
							refresh(city_str);
						}
					});
			dialog.setNegativeButton("ȡ��",
					new DialogInterface.OnClickListener()
					{

						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							dialog.dismiss();

						}
					});

			dialog.show();

			break;

		default:
			break;
		}
	}
	public void writeSharpPreference(String city){
		android.content.SharedPreferences.Editor editor = getSharedPreferences("HseOAConfig", 0).edit();
		editor.putString("city",city);
		Log.i("TAG","���ó��л���"+city);
		editor.commit();
	
	}
}
