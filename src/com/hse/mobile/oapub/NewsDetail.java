package com.hse.mobile.oapub;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.TypeVariable;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.business.MinusBadgeNumberService;
import com.hse.mobile.oa.business.MyScrollView;
import com.hse.mobile.oa.entity.NewsListItem;
import com.hse.mobile.oa.util.CookieUtil;
import com.hse.mobile.oa.util.ImageCache;
import com.hse.mobile.oa.util.NetImageGetter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.webkit.CookieSyncManager;
import android.webkit.WebView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;


/*
 * 新闻详细页
 */
public class NewsDetail extends BaseActivity {
	TextView txtTitle, txtDate, txtContent;
	WebView webView;
	String newsType;
	String newsId;
	String dataContent;
	ScrollView myScrollerview;
	String isPushNews;
	String downloadUrl;
	Context ctx;
	public static final int cache = 10 * 1024; 
	private int mode = 0;  
    float oldDist;  
    int oldY;
    private static ProgressDialog progressDlg = null;
	static final String HtmlTemplate  =
		    "<html>"
		    + "<head>"
		    + "  <title>新闻</title>"
		    + "  <meta charset=\"utf-8\"/>"
		    + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=0.95, maximum-scale=1\">"
		    + "</head>"
		    + "<body>"
		    + "  <article>"
		    + "    <header>"
		    + "      <h1>%s</h1>"
		    + "      <p>发布日期: <time>%s</time></p>"
		    + "    </header>"
		    + "    <div>"
		    + "      %s"
		    + "    </div>"
		    + "  </article>"
		    + "</body>"
		    + "</html>";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.newsdetail);
		ctx=this;
		isPushNews=this.getIntent().getStringExtra(MessageKey.ISPUSHNEWS);
		txtTitle = (TextView)this.findViewById(R.id.txt_newsdetail_title);
		txtDate = (TextView)this.findViewById(R.id.txt_newsdetail_date);
		txtContent = (TextView)this.findViewById(R.id.txt_newsdetail_content);
		netImageGetter = new NetImageGetter(handler, this.getWindowManager().getDefaultDisplay().getWidth());
		//webView = (WebView)this.findViewById(R.id.webView);
		myScrollerview=(ScrollView)findViewById(R.id.thisscroll);
		newsType = this.getIntent().getStringExtra(MessageKey.NEWS_TYPE);
		Log.i("test","newsType:"+newsType);
		newsId = this.getIntent().getStringExtra(MessageKey.NEWS_ID);
		if(newsType.equals("CompanyNotice")){
			new MinusBadgeTask().execute(newsId,newsType);
		}
		else if(newsType.equals("CompanyNews")){
			new MinusBadgeTask().execute(newsId,newsType);
		}
		loadNewsDetail(newsType, newsId);
	}
	public void loadNewsDetail(String newsType, String newsId){
		Log.i("test", "加载新闻");
		showProgressDialog("", "正在加载中...");
		String url="";
		//是否是PushNews
		if(isPushNews.equals("ok")){
			Log.i("test", "加载个人消息");
			url=String.format("%s?id=%s", 
					Constant.URl_MyMessageDetail
					,newsId);
		}
		else{
			url = String.format("%s%s/%s"
					, Constant.URL_NEWS_DETAIL
					, newsType
					, newsId
					);
			Log.i("test", "新闻url:"+url);
		}
//		String url = String.format("%s%s/%s"
//				, Constant.URL_NEWS_DETAIL
//				, newsType
//				, newsId
//				);
		new RestService().execute(url);
		
	}
	
	@Override
	public void onRestServiceResult(JSONObject result) {
		hideProgressDialog();
		
		if(result != null){
			NewsListItem newsItem = NewsListItem.parseJson(result);
			
			/*
			String html = String.format(HtmlTemplate
					, newsItem.getTitle()
					, newsItem.getDate()
					, newsItem.getContent());
			this.webView.loadData(html, "text/html", "UTF8");
			*/
			//设置字体大小
			SharedPreferences sp = getSharedPreferences("HseOAConfig", 0);
			String textsize=sp.getString("thetextsize", "middle");
			setTextSize(textsize);
			if(newsItem.containsKey("title")){
				txtTitle.setText(Html.fromHtml(newsItem.getTitle()));
			}
			if(newsItem.containsKey("content")){
				dataContent = newsItem.getContent();
				txtContent.setText(Html.fromHtml(dataContent,netImageGetter,null));
				txtContent.setMovementMethod(LinkMovementMethod.getInstance());
				CharSequence text=txtContent.getText();
				if(text instanceof Spannable){
					int end=text.length();
					Spannable sp1=(Spannable)txtContent.getText();
					URLSpan[] urls=sp1.getSpans(0, end, URLSpan.class);
					SpannableStringBuilder style=new SpannableStringBuilder(text);
//					style.clearSpans();
					for(URLSpan url:urls){
						style.removeSpan(url);
						MyURLSpan myURLSpan = new MyURLSpan(url.getURL());   
						style.setSpan(myURLSpan, sp1.getSpanStart(url),    
								sp1.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);  
					}
					txtContent.setText(style);
					Log.i("test", "clearSpans:"+txtContent.getText());
				}
				
				
//				txtContent.setText(Html.fromHtml(dataContent, netImageGetter, null));
//				txtContent.setMovementMethod(LinkMovementMethod.getInstance());
			}
			if(newsType.equals("PushNews")){
				if(newsItem.containsKey("pushtime")){
					txtDate.setText("发布日期:" + newsItem.getpushtime());
				}
			}
			else{
				if(newsItem.containsKey("date")){
					txtDate.setText("发布日期:" + newsItem.getDate());
				}
			}
			
		}else{
			Toast.makeText(this, "加载新闻失败!", Toast.LENGTH_SHORT).show();
		}
	}
	
    Handler handler = new Handler(new Handler.Callback(){
    	public boolean handleMessage(android.os.Message msg) {
    		switch(msg.what){
    			case ImageCache.IMAGE_DOWNLOAD_FINISH:
    				//图片下载完成，重新加载
    				txtContent.setText(Html.fromHtml(dataContent, netImageGetter, null));
    			break;
    		}
    		
    		return true;
    	}
    });
	private ImageGetter netImageGetter;
	public void setTextSize(String size){
		if(size.equals("big")){
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,35);
			txtDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
			txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
		}
		else if(size.equals("middle")){
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,30);
			txtDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
			txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
		}
		else if(size.equals("small")){
			txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP,25);
			txtDate.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
			txtContent.setTextSize(TypedValue.COMPLEX_UNIT_SP,15);
		}
		Log.i("test","size："+txtContent.getTextSize());
	}
	/*
	 * 提交请求改变badge计数
	 */
	public class MinusBadgeTask extends AsyncTask<String, Integer, Boolean>{
		protected Boolean doInBackground(String... params) {
			try {
				return MinusBadgeNumberService.minusBadge(params[0],params[1]);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	public  class MyURLSpan extends ClickableSpan {    
	    
        private String mUrl;    
    
        MyURLSpan(String url) {    
            mUrl = url;    
        }    
    
        @Override    
        public void onClick(View widget) {    
        	if(mUrl.endsWith(".doc") || mUrl.endsWith(".docx") 
    				||mUrl.endsWith(".xls") || mUrl.endsWith(".xlsx")|| mUrl.endsWith(".pdf")
    				||mUrl.endsWith(".jpg") || mUrl.endsWith(".jpeg")|| mUrl.endsWith(".png")
    				||mUrl.endsWith(".bmp") || mUrl.endsWith(".gif")||mUrl.endsWith(".zip")||mUrl.endsWith("rar")){
        		 processDownload(mUrl);
        	}
        }    
    }
	private void processDownload(String downloadurl){
		 Log.i("test", "download:  "+downloadurl);
		 downloadUrl=downloadurl;
		 progressDlg = ProgressDialog.show(ctx, null,"正在下载");
		 new Thread(runnable).start();
	 }
	public Runnable runnable=new Runnable() {
		public void run() {
			if(downloadUrl==null||downloadUrl.equals("")){
				return;
			}
			else{
				try{
					String str[]=downloadUrl.split("/");
					String name=str[str.length-1];
					String filename=new String(name);
					name=URLEncoder.encode(name, "UTF_8");
					String newurl = downloadUrl.replaceAll(filename, "");
					newurl =newurl+name;
					URL downurl=new URL(newurl);
					HttpURLConnection conn = (HttpURLConnection)downurl.openConnection();
					conn.connect();
					filename="/sdcard/download/"+filename;
					int length=conn.getContentLength();
					InputStream is=conn.getInputStream();
					
					File file=new File(filename);
					FileOutputStream fos=new FileOutputStream(file);
					byte []buf=new byte[1024];
					int count=0;
					do{
						int numread=is.read(buf);
						count+=numread;
			            //更新进度
				        if(numread <= 0){ 
				         break;
				         }
				        fos.write(buf,0,numread); 
					}
					while(true);
					fos.close();
					is.close();
					progressDlg.dismiss();
					progressDlg=null;
					Intent intent=new Intent();
					intent.setAction("downloadfile");
					intent.putExtra("file", file.toString());
					getApplicationContext().sendBroadcast(intent);
				}
				catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	};
	public static String getFilename(HttpResponse response){
		org.apache.http.Header contentHeader=response.getFirstHeader("Content-Disposition");
		Log.i("test", "head:"+contentHeader);
		String filename=null;
		if(contentHeader!=null){
			HeaderElement[] values=contentHeader.getElements();
			if(values.length==1){
				NameValuePair parm=values[0].getParameterByName("filename");
				if(parm!=null){
					try{
						filename=parm.getValue();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}
			}
		}
		return filename;
	}
}
