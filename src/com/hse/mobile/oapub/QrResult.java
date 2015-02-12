package com.hse.mobile.oapub;

import java.util.Arrays;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.hse.mobile.oa.business.Constant;
import com.hbsh.beta.R;

public class QrResult extends BaseActivity {
	TextView equipment_type,equipment_id,equipment_number,equipment_name,equipment_department,equipment_user,equipment_repair;
	private String qrcode;
	Context ctx;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qr_result);
		equipment_type=(TextView)findViewById(R.id.equipment_type);
		equipment_id=(TextView)findViewById(R.id.equipment_id);
		equipment_number=(TextView)findViewById(R.id.equipment_number);
		equipment_name=(TextView)findViewById(R.id.equipment_name);
		equipment_department=(TextView)findViewById(R.id.equipment_department);
		equipment_user=(TextView)findViewById(R.id.equipment_user);
		equipment_repair=(TextView)findViewById(R.id.equipment_repair);
		qrcode=this.getIntent().getStringExtra("qrcode");
		ctx=this;
		Log.i("test", "qrcode:"+qrcode);
		String Dncode=QrDivision(qrcode);
		checkEquipment(Dncode);
	}
	/**
	 * 从扫描出的结果中截取DN-00000010形式的字段
	 * @param code
	 * @return
	 */
	private String QrDivision(String code){
		String divsionStr[];
		divsionStr=code.split("DN");
		String result="DN"+divsionStr[1];
		return result;
	}
	private void checkEquipment(String equipment_number){
		String url=Constant.URL_Equipment+"?meanscode="+equipment_number;
		showProgressDialog("", "正在加载中...");
		new RestService().execute(url);
	}
	public void onRestServiceResult(JSONObject result){
		this.hideProgressDialog();
		if(result!=null){
			try {
				Log.i("test", result.toString());
				if(result.has("gghx")){
					
					for(int i=0;i<result.length();i++){
						//Log.i("test","分组解析："+result.);
					}
					equipment_type.setText(result.getString("gghx").toString());
					equipment_id.setText(result.getString("id").toString());
					equipment_number.setText(result.getString("meanscode").toString());
					equipment_name.setText(result.getString("meansname").toString());
					equipment_department.setText(result.getString("orgname").toString());
					equipment_user.setText(result.getString("useperson").toString());
					String repair=result.getString("wxjj");
					repair= repair.replaceAll("^\\[|\\]$", "");
					if(TextUtils.isEmpty(repair)){
						equipment_repair.setText("暂无维修记录");
					}
					else{
						JSONObject repair_obj=new JSONObject(repair);
						String repair_str="维修时间：\n"+repair_obj.getString("statechangetime")+"\n故障现象：\n"+
						       repair_obj.getString("ext1")+"\n处理措施：\n"+repair_obj.getString("ext2")+"\n";
						equipment_repair.setText(repair_str);
					}
					
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
	}
}
