package com.hse.mobile.oapub;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.IRestServiceListener;
import com.hse.mobile.oa.business.JsonDataConvert;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.RestServiceTask;
import com.hse.mobile.oa.entity.BitmapEx;
import com.hse.mobile.oa.entity.Department;
import com.hse.mobile.oa.ui.AspectRatioImageView;
import com.hse.mobile.oa.ui.GalleryAdapter;
import com.hse.mobile.oa.util.BitmapUtil;
import com.hse.mobile.oa.util.ImageDownloadTask;
import com.hse.mobile.oa.util.ImageDownloadTask.ImageDownloadListener;
import com.hse.mobile.oapub.BaseActivity.RestService;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

/*
 * 随手拍表单页
 */
public class IllegalTicket extends BaseActivity {
	Spinner spnTicketType;
	Button  btnTakephoto;
	Button  btnSave, btnPhotoDel;
	//DatePicker dpkTicketDate;
	EditText txtName, txtCause, txtAmount, txtJlAmount, txtDepart, txtJlDepart, txtNextPerson;
	TextView txtDate;
	Date ticketDate;
	String catetory = "illegal";
	Department depart, jlDepart;
	Gallery gallery;
	TextView txtGalleryPage, txtPhotoUpload;
	String uniqueId;
	String nextPerson = null;		//下一步审批人
	boolean isAddNew = true;
	boolean isIllegal = true;
	List<BitmapEx> imageList = new ArrayList<BitmapEx>();
	HashMap<String, Object> ticket;
	
	final String[] ticket_type_array = {"工程类","安全类","质量类"}; 
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		catetory = this.getIntent().getStringExtra(MessageKey.TICKETS_TYPE);
		isIllegal = catetory.equals("illegal");
		if(this.getIntent().hasExtra("uniqueId")){
			uniqueId = this.getIntent().getStringExtra("uniqueId");
			isAddNew = false;
		}
		
		if(isIllegal){
			this.setContentView(R.layout.illegal_ticket);
		}else{
			this.setContentView(R.layout.positive_ticket);
		}

		init();
	}
	
	void init(){
        txtName = (EditText) this.findViewById(R.id.ticket_name);
        txtCause = (EditText) this.findViewById(R.id.ticket_cause);
        txtAmount = (EditText) this.findViewById(R.id.ticket_amount);
        txtJlAmount = (EditText) this.findViewById(R.id.ticket_jlamount);
        txtDepart = (EditText) this.findViewById(R.id.ticket_depart);        
        txtJlDepart = (EditText) this.findViewById(R.id.ticket_jldepart);
        txtNextPerson = (EditText) this.findViewById(R.id.ticket_nextperson);
        
        if(this.isAddNew){
	        txtDepart.setOnTouchListener(new OnTouchListener(){
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if(arg1.getAction() == MotionEvent.ACTION_DOWN){
						onDepartClickListener.onClick(arg0);
						return true;
					}
					return false;
				}
	        });
	        txtJlDepart.setOnTouchListener(new OnTouchListener(){
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if(arg1.getAction() == MotionEvent.ACTION_DOWN){
						onJlDepartClickListener.onClick(arg0);
						return true;
					}
					return false;
				}				
	        });
	        
	        txtNextPerson.setOnTouchListener(new OnTouchListener(){
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if(arg1.getAction() == MotionEvent.ACTION_DOWN){
						selectNextPerson();
						return true;
					}
					return false;
				}				
	        });
        }

        txtDate = (TextView) this.findViewById(R.id.ticket_date);
        txtDate.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				new DatePickerDialog(IllegalTicket.this
					, onDateSetListener
					, ticketDate.getYear() + 1900
					, ticketDate.getMonth()
					, ticketDate.getDate())
				.show();	
			}
		});
        
		spnTicketType = (Spinner) this.findViewById(R.id.ticket_type);
		btnTakephoto = (Button) this.findViewById(R.id.ticket_takephoto);
		btnTakephoto.setOnClickListener(onTakePhotoClickListener);
		btnSave = (Button) this.findViewById(R.id.ticket_save);
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_item, ticket_type_array); 
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); 
        spnTicketType.setAdapter(adapter); 

        gallery =  (Gallery) this.findViewById(R.id.gallery_photo);
        gallery.setOnItemSelectedListener(onPhotoItemSelectListener);
        txtGalleryPage = (TextView) this.findViewById(R.id.txt_gallery_page);
        txtPhotoUpload = (TextView) this.findViewById(R.id.txt_photo_upload);
        btnPhotoDel = (Button) this.findViewById(R.id.btn_photo_del);
        btnPhotoDel.setOnClickListener(onPhotoDelClickListener);
        
        super.setTitleBarRightButton("发送", onPostClickListener);	
        if(!isAddNew){
        	//修改
        	btnTakephoto.setVisibility(View.GONE);
        	btnPhotoDel.setVisibility(View.GONE);
        	this.btnSave.setVisibility(View.GONE);
        	loadTicket();
        }else{
        	//新增
        	setDate(new Date());
        	String name = String.format("%s%s"
        			, new SimpleDateFormat("yyyy年M月d日").format(new Date())
        			, this.isIllegal ? "华北石化违章罚款单" : "华北石化监理正激励");
        	this.txtName.setText(name);
			btnSave.setOnClickListener(onSaveClickListener);
			
			HseApplication application = (HseApplication)getApplication();
			if(application.session.containsKey("images")){
				this.imageList.addAll((List<BitmapEx>)application.session.get("images"));
				this.refreshGallery();
			}
			this.btnTakephoto.setVisibility(View.GONE);
			
        }
	}
	
	void setDate(Date date){
		ticketDate = date;
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		txtDate.setText(format.format(ticketDate));
	}
	
	OnDateSetListener onDateSetListener = new OnDateSetListener(){
		public void onDateSet(DatePicker arg0, int year, int month, int day) {
			setDate(new Date(year - 1900, month, day));		
		}
	};
	
	void loadTicket(){
		String url = Constant.URL_TICKET_DETAIL + uniqueId;
		showProgressDialog("", "正在加载...");
		new RestServiceTask(onLoadTicketListener).execute(url);
	}
	
	IRestServiceListener onLoadTicketListener = new IRestServiceListener(){
		public void onRestServiceResult(JSONObject result) {
			try
			{
				if(result != null){
					ticket = JsonDataConvert.parseObject(result);
					setTicket(ticket);
					loadJlDepart();
				}else{
					Toast.makeText(IllegalTicket.this, "加载数据失败!", Toast.LENGTH_SHORT).show();
					throw new Exception("加载数据失败");
				}
			}catch(Exception ex){
				hideProgressDialog();
				Log.e("HseMobileOA", "onRestServiceResult", ex);
			}
		}
	};
	
	
	void setJlDepart(Department depart){
		this.jlDepart = depart;
		this.txtJlDepart.setText(depart.getName());
	}
	
	void setDepart(Department depart){
		this.depart = depart;
		this.txtDepart.setText(depart.getName());
	}
	
	/*
	 * 装载监理单位信息
	 */
	void loadJlDepart(){
		String url = Constant.URL_DEPART_LIST + "supervisor";
		//showProgressDialog("", "正在加载...");
		new RestServiceTask(loadJlDepartListener).execute(url);
	}
	
	IRestServiceListener loadJlDepartListener = new IRestServiceListener(){
		public void onRestServiceResult(JSONObject result) {
			//hideProgressDialog();
			
			try
			{
				if(result != null){
					HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
					List<HashMap<String, Object>> data = JsonDataConvert.parseArray((JSONArray)resultData.get("clientList"));
					
					for(int i=0; i<data.size(); i++){
						String name = data.get(i).get("clientName").toString();
						String id = data.get(i).get("clientId").toString();
						if(id.equals(jlDepart.getId())){
							jlDepart.setName(name);
							setJlDepart(jlDepart);
							break;
						}
					}
				}else{
					Toast.makeText(IllegalTicket.this, "加载单位失败!", Toast.LENGTH_SHORT).show();
				}
				
				loadDepart();
			}catch(Exception ex){
				Log.e("HseMobileOA", "onRestServiceResult", ex);
				hideProgressDialog();
			}
		}
	};
	
	/*
	 * 装载施工单位信息
	 */
	void loadDepart(){
		String url = Constant.URL_DEPART_LIST + "construction";
		//showProgressDialog("", "正在加载...");
		new RestServiceTask(loadDepartListener).execute(url);
	}
	
	IRestServiceListener loadDepartListener = new IRestServiceListener(){
		public void onRestServiceResult(JSONObject result) {
			hideProgressDialog();
			
			try
			{
				if(result != null){
					HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
					List<HashMap<String, Object>> data = JsonDataConvert.parseArray((JSONArray)resultData.get("clientList"));
					
					for(int i=0; i<data.size(); i++){
						String name = data.get(i).get("clientName").toString();
						String id = data.get(i).get("clientId").toString();
						if(id.equals(depart.getId())){
							depart.setName(name);
							setDepart(depart);
							break;
						}
					}
				}else{
					Toast.makeText(IllegalTicket.this, "加载单位失败!", Toast.LENGTH_SHORT).show();
				}
			}catch(Exception ex){
				Log.e("HseMobileOA", "onRestServiceResult", ex);
			}
		}
	};
	
	/*
	 * 装载图片
	 */
	void loadImages(String imageIds){
		if(imageIds==null || imageIds.isEmpty()){
			return;
		}
		
		final String[] ids = imageIds.split(",");
		ImageDownloadListener imageDownloadListener = new ImageDownloadListener(){
			public void onImageDownloadResult(Bitmap[] result) {
				for(int i=0; i<result.length; i++){
					BitmapEx image = new BitmapEx(result[i], ids[i]);
					imageList.add(image);
				}
				txtGalleryPage.setText("");
				refreshGallery();
			}
		};
		
		this.txtGalleryPage.setText("图片加载中...");
		this.txtGalleryPage.setVisibility(View.VISIBLE);
		String[] urls = new String[ids.length];
		for(int i=0; i<ids.length; i++){
			urls[i] = Constant.URL_IMAGE + ids[i];
		}
		new ImageDownloadTask(imageDownloadListener).execute(urls);
	}
	

	
	/*
	 * 删除服务器上的图片
	 */
	void deletePhoto(String imageId){
		String url = Constant.URL_IMAGE_DELETE + imageId;
		new RestServiceTask(null).execute(url, "delete");
	}
	
	OnClickListener  onPhotoDelClickListener = new OnClickListener(){
		public void onClick(View v) {
			int photoIndex = gallery.getSelectedItemPosition();	
			String imageId = imageList.get(photoIndex).getId();
			imageList.remove(photoIndex);
			deletePhoto(imageId);
			refreshGallery();
			if(imageList.size() == 0){
				btnPhotoDel.setVisibility(View.GONE);
				txtPhotoUpload.setVisibility(View.GONE);
				txtGalleryPage.setVisibility(View.GONE);
			}
		}
	};
	
	OnClickListener  onPostClickListener = new OnClickListener(){
		public void onClick(View v) {
			post();			
		}
	};
	
	OnClickListener  onSaveClickListener = new OnClickListener(){
		public void onClick(View v) {
			save();			
		}
	};
	
	OnClickListener  onDepartClickListener = new OnClickListener(){
		public void onClick(View v) {
			Intent intent = new Intent(IllegalTicket.this, DepartSelectDialog.class);
			intent.putExtra(MessageKey.CATEGORY, "construction");
			startActivityForResult(intent, 0);			
		}
	};
	
	OnClickListener  onJlDepartClickListener = new OnClickListener(){
		public void onClick(View v) {
			Intent intent = new Intent(IllegalTicket.this, DepartSelectDialog.class);
			intent.putExtra(MessageKey.CATEGORY, "supervisor");
			startActivityForResult(intent, 1);			
		}
	};
	
	/*
	 * 选择下一步审批人
	 */
	void selectNextPerson(){
		Intent intent = new Intent(IllegalTicket.this, PersonSelectDialog.class);
		startActivityForResult(intent, 3);	
	}
	
	/*
	 * 设置相册页信息
	 */
	private void setPhotoPageInfo(int photoIndex){
		txtGalleryPage.setVisibility(View.VISIBLE);
		txtGalleryPage.setText(String.format("%d/%d", photoIndex+1, this.imageList.size()));
		txtPhotoUpload.setVisibility(View.GONE);
		this.btnPhotoDel.setVisibility(View.GONE);
		
		
		/*
		if(imageList.get(photoIndex).getId() == null){
			this.txtPhotoUpload.setVisibility(View.VISIBLE);
			this.txtPhotoUpload.setText(imageList.get(photoIndex).getStatusDescription());
		}else{
			this.txtPhotoUpload.setVisibility(View.GONE);
			
			if(isAddNew){
				btnPhotoDel.setVisibility(View.VISIBLE);
			}else{
				btnPhotoDel.setVisibility(View.GONE);
			}
		}
		*/
	}
	
	OnItemSelectedListener onPhotoItemSelectListener = new OnItemSelectedListener(){

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			setPhotoPageInfo(arg2);
		}

		public void onNothingSelected(AdapterView<?> arg0) {
	
		}
	};
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case 0:
			if(resultCode == Activity.RESULT_OK){
				String id = data.getStringExtra("clientId");
				String name = data.getStringExtra("clientName");
				setDepart(new Department(id, name));
			}
			break;
		case 1:
			if(resultCode == Activity.RESULT_OK){
				String id = data.getStringExtra("clientId");
				String name = data.getStringExtra("clientName");
				setJlDepart(new Department(id, name));
			}
			break;	
		case 2:
			if(resultCode == Activity.RESULT_OK){
				//Bitmap image = (Bitmap) data.getExtras().get("photo");
				
				Bitmap image = ((HseApplication)getApplication()).getBitmap();
				BitmapEx imageEx = new BitmapEx(image, null);
				imageList.add(imageEx);
				uploadImage(imageEx);		
				refreshGallery();
				gallery.setSelection(imageList.size()-1);
			}
			break;	
		case 3:
			if(resultCode == Activity.RESULT_OK){
				String id = data.getStringExtra("value");
				String name = data.getStringExtra("name");
				this.nextPerson = id;
				this.txtNextPerson.setText(name);
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	};
	
	/*
	 * 上传图片
	 */
	void uploadImage(final BitmapEx image){
		image.setStatus(BitmapEx.STATUS_UPLOADING);
		
		String url = Constant.URL_IMAGE;
		new RestServiceTask(new IRestServiceListener(){
			public void onRestServiceResult(JSONObject result) {
				try
				{
					if(result != null){
						HashMap<String, Object> resultData = JsonDataConvert.parseObject(result);
						String imageId = resultData.get("imageId").toString();
						Log.i("uploadImage success", imageId);
						
						image.setId(imageId);
						
						//更新状态
						image.setStatus(BitmapEx.STATUS_UPLOAD_SUCCESS);
					}else{
						image.setStatus(BitmapEx.STATUS_UPLOAD_FAIL);
					}
					
					if(imageList.get(gallery.getSelectedItemPosition()) == image){
						setPhotoPageInfo(gallery.getSelectedItemPosition());
					}
				}catch(Exception ex){
				}
			}
		})
		.execute(url, "post", BitmapUtil.Bitmap2Bytes(image.getBitmap()), "binary");
	}
		
	Bitmap[] getImageArray(){
		Bitmap[] images = new Bitmap[imageList.size()];
		for(int i=0; i<imageList.size(); i++){
			images[i] = imageList.get(i).getBitmap();
		}
		return images;
	}
	
	void refreshGallery(){
		GalleryAdapter adapter = new GalleryAdapter(this, getImageArray());
		gallery.setAdapter(adapter);
	}
	
	OnClickListener  onTakePhotoClickListener = new OnClickListener(){
		public void onClick(View v) {
			Intent intent = new Intent(IllegalTicket.this, TakePhoto.class);
			startActivityForResult(intent, 2);			
		}
	};
	
	
	
	/** 
	 * @Description: 校验输入数据
	 * @return
	 * boolean
	 * @throws 
	 * @author Administrator
	 */
	boolean validate(){
		if(txtName.getText().toString().isEmpty()){
			Toast.makeText(IllegalTicket.this, "请填写名称 ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(this.jlDepart == null){
			Toast.makeText(IllegalTicket.this, "请选择监理单位 ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(this.isIllegal){
			if(this.txtJlAmount.getText().toString().isEmpty()){
				String text = "请填写监理单位罚款";
				Toast.makeText(IllegalTicket.this, text, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		
		if(this.depart == null){
			Toast.makeText(IllegalTicket.this, "请选择施工单位 ", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(this.txtAmount.getText().toString().isEmpty()){
			String text = this.isIllegal? "请填写施工单位罚款" : "请填写金额 ";
			Toast.makeText(IllegalTicket.this, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(this.txtCause.getText().toString().isEmpty()){
			String text = this.isIllegal? "请填写详细罚款原因" : "请填写情况描叙";
			Toast.makeText(IllegalTicket.this, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		if(this.nextPerson == null){
			String text = "请选择下一步审批人";
			Toast.makeText(IllegalTicket.this, text, Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	/*
	 * 发送
	 */
	void post(){
		if(!this.validate()){
			return;
		}
		
		List<NameValuePair> params = getTicketParams();
		if(this.isIllegal){
			//罚单
			params.add(new BasicNameValuePair("ticket_flag", "1"));
		}else{
			//正激励
			params.add(new BasicNameValuePair("cat", "post"));
		}

		showProgressDialog("", "正在发送...");
		if(isAddNew){
			submitAdd(params);
		}else{
			submitModify(params);
		}
	}
	
	/*
	 * 保存
	 */
	void save(){
		List<NameValuePair> params = getTicketParams();
		if(this.isIllegal){
			//罚单
			params.add(new BasicNameValuePair("ticket_flag", "0"));
		}else{
			//正激励
			params.add(new BasicNameValuePair("cat", "save"));
		}
		showProgressDialog("", "正在保存...");
		if(isAddNew){
			submitAdd(params);
		}else{
			submitModify(params);
		}
	}
	
	/*
	 * 提交新增
	 */
	void submitAdd(List<NameValuePair> params){
		String url = Constant.URL_TICKET_SUBMIT + catetory;
		new RestServiceTask(submitTicketListener).execute(url, "post", params);
	}
	
	/*
	 * 提交修改
	 */
	void submitModify(List<NameValuePair> params){
		String url = String.format("%s%s/%s"
				, Constant.URL_TICKET_SUBMIT
				, catetory
				, uniqueId);
		new RestServiceTask(submitTicketListener).execute(url, "put", params);
	}
	
	/*
	 * 提交结果处理
	 */
	IRestServiceListener submitTicketListener = new IRestServiceListener(){
		public void onRestServiceResult(JSONObject result){
			hideProgressDialog();
			if(result != null && result.optString("status").equals("true")){
				Toast.makeText(IllegalTicket.this, "操作成功", Toast.LENGTH_SHORT).show();
				//back();
				
				((HseApplication)getApplication()).session.remove("images");
				//改成直接进入takephoto页面
				//Intent intent = new Intent(IllegalTicket.this, EasyTickets.class);
				//intent.putExtra(MessageKey.REUSEABLE, false);
				//navigate(intent, "随手拍", false);
				Intent intent = new Intent(IllegalTicket.this, TakePhoto.class);
				intent.putExtra(MessageKey.REUSEABLE, false);
				navigateRoot("工程施工随手拍", intent);
			}else{
				Toast.makeText(IllegalTicket.this, "操作失败", Toast.LENGTH_SHORT).show();
			}
		}
	};
	private void navigateRoot(String title, Intent intent){
		((HseApplication)this.getApplication()).navigateRoot(intent, title);
	}
	int getTicketTypeIndex(String type){
		for(int i=0; i< ticket_type_array.length; i++){
			if(ticket_type_array[i].equals(type)){
				return i;
			}
		}
		
		return -1;
	}
	
	void setTicket(HashMap<String, Object> ticketData){
		String type = ticketData.get("type").toString();
		int typeIndex = getTicketTypeIndex(type);
		if(typeIndex != -1){
			spnTicketType.setSelection(typeIndex);
		}
		this.txtName.setText(ticketData.get("ticketName").toString());
		this.txtAmount.setText(ticketData.get("amount").toString());
		this.txtCause.setText(ticketData.get("cause").toString());
		
		String ticketFlag = ticketData.get("ticket_flag").toString();
		
		/*
		if(ticketFlag.equals("0")){
			//草稿
			super.setTitleBarRightButton("发送", onPostClickListener);
		}else{
			super.setTitleBarRightButton("保存", onSaveClickListener);
		}*/
		
		if(this.isIllegal){
			//罚单
			this.txtJlAmount.setText(ticketData.get("amountJL").toString());
			this.jlDepart = new Department(ticketData.get("violateCompanyIdJL").toString(), null);
			this.depart = new Department(ticket.get("violateCompanyId").toString(), null);
		}else{
			//正激励
			this.jlDepart = new Department(ticketData.get("mulctDepartId").toString(), null);
			this.depart = new Department(ticket.get("violateCompanyId").toString(), null);
		}

		
		try{
			String value = ticket.get("ticketDate").toString();
			if(value.length() > 10){
				value = value.substring(0, 10);
			}
			Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value);
			this.setDate(date);

			if(ticketData.containsKey("imageIDs") && ticketData.get("imageIDs")!=null){
				List<HashMap<String, Object>> images = null;
				if(ticketData.get("imageIDs") instanceof JSONArray){
					images = JsonDataConvert.parseArray((JSONArray)ticketData.get("imageIDs"));
				}
				if(ticketData.get("imageIDs") instanceof JSONObject){
					images = new ArrayList<HashMap<String, Object>>();
					images.add(JsonDataConvert.parseObject((JSONObject)ticketData.get("imageIDs")));
				}
				String imageIds = "";
				for(int i=0; i<images.size(); i++){
					imageIds += images.get(i).get("attachId").toString();
					if(i <images.size()-1){
						imageIds += ",";
					}
				}
				this.loadImages(imageIds);
			}
		}catch(Exception ex){		
		}
	}
	
	List<NameValuePair> getTicketParams(){
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("ticketName", txtName.getText().toString()));
		params.add(new BasicNameValuePair("amount", txtAmount.getText().toString()));
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		params.add(new BasicNameValuePair("ticketDate", format.format(ticketDate)));
		params.add(new BasicNameValuePair("adderId", ((HseApplication)getApplicationContext()).getLoginUserId()));
		params.add(new BasicNameValuePair("nextPerson", this.nextPerson));
		
		if(this.isIllegal){
			//罚单
			params.add(new BasicNameValuePair("type", this.spnTicketType.getSelectedItem().toString()));
			params.add(new BasicNameValuePair("cause", txtCause.getText().toString()));
			params.add(new BasicNameValuePair("amountJL", txtJlAmount.getText().toString()));
			
			if(isAddNew){
				if(this.depart != null){
					params.add(new BasicNameValuePair("selectedClientId", depart.toSubmitString()));
				}
				if(this.jlDepart != null){
					params.add(new BasicNameValuePair("selectedClientIdJL", jlDepart.toSubmitString()));
				}
			}
		}else{
			//正激励
			params.add(new BasicNameValuePair("Finetype", this.spnTicketType.getSelectedItem().toString()));
			params.add(new BasicNameValuePair("describle", txtCause.getText().toString()));
			
			if(isAddNew){
				if(this.depart != null){
					params.add(new BasicNameValuePair("dw", depart.toSubmitString()));
				}
				if(this.jlDepart != null){
					params.add(new BasicNameValuePair("mulctDepartId", jlDepart.toSubmitString()));
				}
			}
		}
		
		if(isAddNew){		
			String imageIds = "";
			for(int i=0; i<imageList.size(); i++){
				imageIds += imageList.get(i).getId();
				if(i!=imageList.size()-1){
					imageIds += ",";
				}
			}
			params.add(new BasicNameValuePair("imageIDs", imageIds));
			//Toast.makeText(this, imageIds, Toast.LENGTH_SHORT).show();
		}
		
		return params;
	}
	
	public void onBackPressed() {
		super.back();
	}
}
