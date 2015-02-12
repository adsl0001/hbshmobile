package com.hse.mobile.extapp;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.hbsh.beta.R;
import com.hse.mobile.oa.business.Constant;
import com.hse.mobile.oa.business.MessageKey;
import com.hse.mobile.oa.util.CookieUtil;
import com.hse.mobile.oa.util.RestClient;
import com.hse.mobile.oapub.BaseActivity;
import com.hse.mobile.oapub.NewsDetail;
import com.hse.mobile.oapub.BaseActivity.RestService;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

/**
 * 美丽社区数据列表
 * 
 * @author dbz
 *
 */
public class MLAreaListActivity extends BaseActivity {
	// App Key：480314895
	// App Secret：012e25459c3947e797104b397d1a0c6b.
	/**
	 * 美丽社区数据
	 */
	List<Map<String, Object>> dataList = Collections
			.synchronizedList(new ArrayList<Map<String, Object>>());
	/**
	 * 展现数据的ListView
	 */
	PullToRefreshListView pullToRefreshView = null;
	/**
	 * pullToRefreshView的adapter
	 */
	SimpleAdapter adapter = null;
	/**
	 * 缓存this
	 */
	Activity ctx;
	private String cityId = "529";
	/**
	 * 大众点评数据页号
	 */
	private int start = 0;
	private int pageSize = 10;
	private String server = "http://service.mlarea.com/client/information/list.do";

	private String filterString = "{\"filterList\":[{\"property\":\"publish\",\"operator\":\"eq\",\"value\":true},{\"property\":\"businessId\",\"operator\":\"ne\",\"value\":\"none\"},{\"property\":\"areaId\",\"operator\":\"eq\",\"value\":\"07772db6-1b43-49d9-8e51-5658fe0f1f1c\"}]}";
	private String orderString = "{\"orderList\":[{\"property\":\"meanValue\",\"direction\":\"desc\"},{\"property\":\"createDate\",\"direction\":\"desc\"}]}";

	// private String url =
	// "http://api.t.dianping.com/n/napi.xml?cityId={cityId}&page={page}&count={count}";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 暂时与大众点评共用一个layout
		setContentView(R.layout.activity_dianping);
		TextView t = (TextView) this.findViewById(R.id.txt_data_source);
		t.setText("数据来自美丽社区");
		ctx = this;
		initList();
		new GetDataTask().execute();
	}

	/**
	 * 图片缓存
	 */
	private Map<String, Drawable> imageCache = new HashMap<String, Drawable>(5);

	private class GetDataTask extends AsyncTask<Void, Void, Object> {

		// 子线程请求数据
		@Override
		protected Object doInBackground(Void... params) {
			String url = server;
			HttpPost httpPost = new HttpPost(url);
			
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair("filterString", filterString));
			paramList.add(new BasicNameValuePair("orderString", orderString));
			paramList
					.add(new BasicNameValuePair("start", String.valueOf(start)));
			paramList.add(new BasicNameValuePair("pageSize", String
					.valueOf(pageSize)));
			DefaultHttpClient httpclient = new DefaultHttpClient();
			CookieUtil.setCookieSpec(httpclient);
			CookieUtil.writeCookie(httpclient);
			HttpResponse httpResponse;
			try {
				 
				httpPost.setEntity(new UrlEncodedFormEntity(paramList,
						HTTP.UTF_8));
				httpResponse = httpclient.execute(httpPost);
				String respData;
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					CookieUtil.readCookie(httpclient);
					respData = EntityUtils.toString(httpResponse.getEntity(),
							"UTF-8");
					JSONTokener jsonParser = new JSONTokener(respData);
					return jsonParser.nextValue();
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				Log.d(MLAreaListActivity.class.toString(), e.getMessage(), e);
			} catch (IOException e) {
				Log.d(MLAreaListActivity.class.toString(), e.getMessage(), e);
			} catch (JSONException e) {
				Log.d(MLAreaListActivity.class.toString(), e.getMessage(), e);
			}
			return null;
		}


		// 主线程更新UI
		@Override
		protected void onPostExecute(Object result) {
			if (result instanceof JSONObject) {
				JSONObject temp = (JSONObject) result;
				try {
					JSONArray rows = (JSONArray) temp.get("rows");
					if (rows.length() > 0) {
						if (start == 0) {
							dataList.clear();
						}
						start += rows.length();
						JSONObject row;
						Map<String, Object> data;
						for (int i = 0; i < rows.length(); i++) {
							row = rows.getJSONObject(i);
							data = new HashMap<String, Object>();
							data.put("shangjia", row.getString("source"));
							data.put("jieshao", row.getString("title"));
							data.put("jiage", "￥"+row.getString("discount"));
							data.put("id", row.getString("id"));
							String a = "http://service.mlarea.com/client/trade_leads_pic/showInfoPic.do?id="
									+ row.getString("id")
									+ "&name=discount_sale";
							data.put("imgUrl", a);
							dataList.add(data);
						}
						adapter.notifyDataSetChanged();
					} else {
						Toast.makeText(MLAreaListActivity.this, "已经是最后一页了",
								Toast.LENGTH_SHORT).show();
					}
				} catch (JSONException e) {
					Log.d(MLAreaListActivity.class.toString(), e.getMessage(),
							e);
				}
			}

			// 通知RefreshListView 我们已经更新完成
			pullToRefreshView.onRefreshComplete();
		}
	}

	private void showDetail(int index) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataList
				.get(index);
		String id = (String) data.get("id");
		Intent intent = new Intent(MLAreaListActivity.this,
				MLAreaDetailActivity.class);
		Bundle bundle = this.getIntent().getExtras();
		bundle.putString(MessageKey.MLAREA_DETAIL_ID, id);
		bundle.putBoolean(MessageKey.REUSEABLE, false);
		intent.putExtras(bundle);
		navigate(intent, "详情", true);
	}

	private void initList() {
		pullToRefreshView = (PullToRefreshListView) this
				.findViewById(R.id.pull_refresh_list);
		pullToRefreshView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showDetail(position - 1);
			}
		});
		pullToRefreshView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {
					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// Toast.makeText(DZDPListActivity.this,
						// "正在加载数据", Toast.LENGTH_SHORT)
						// .show();
						start = 0;
						new GetDataTask().execute();
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// Toast.makeText(DZDPListActivity.this,
						// "正在刷新数据", Toast.LENGTH_SHORT).show();
						new GetDataTask().execute();
					}

				});
		adapter = new SimpleAdapter(this, dataList,
				R.layout.activity_dianping_list_item, new String[] { "imgUrl",
						"shangjia", "jieshao", "jiage" }, new int[] {
						R.id.dianping_list_imageView,
						R.id.dianping_list_shangjia,
						R.id.dianping_list_jieshao, R.id.dianping_list_jiage }) {
			private LayoutInflater mInflater = LayoutInflater.from(ctx);

			class MyImageAsyncTask extends AsyncTask<String, Void, Drawable> {
				private ImageView imageView;

				public MyImageAsyncTask(ImageView imageView) {
					this.imageView = imageView;
				}

				@Override
				protected Drawable doInBackground(String... params) {
					Drawable drawable = null;
					try {
						drawable = Drawable.createFromStream(
								new URL(params[0]).openStream(), "image.jpg");
					} catch (IOException e) {
						Log.d("test", e.getMessage());
					}
					if (drawable == null) {
						Resources res = getResources();
						drawable = res.getDrawable(R.drawable.a_30);
						imageCache.put(params[0], drawable);
					} else {
						imageCache.put(params[0], drawable);
					}

					return drawable;
				};

				@Override
				protected void onPostExecute(Drawable result) {
					imageView.setImageDrawable(result);
				}
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				View v;
				if (convertView == null) {
					v = mInflater.inflate(R.layout.activity_dianping_list_item,
							parent, false);
				} else {
					v = convertView;
				}
				TextView shangjia = (TextView) v
						.findViewById(R.id.dianping_list_shangjia);
				TextView jiage = (TextView) v
						.findViewById(R.id.dianping_list_jiage);
				TextView jieshao = (TextView) v
						.findViewById(R.id.dianping_list_jieshao);
				ImageView imageView = (ImageView) v
						.findViewById(R.id.dianping_list_imageView);
				imageView.setImageDrawable(null);
				imageView.setTag(position);
				shangjia.setText((String) dataList.get(position)
						.get("shangjia"));
				jiage.setText((String) dataList.get(position).get("jiage"));
				jieshao.setText((String) dataList.get(position).get("jieshao"));
				final String imgUrl = (String) dataList.get(position).get(
						"imgUrl");
				if (imageCache.get(imgUrl) != null) {
					imageView.setImageDrawable(imageCache.get(imgUrl));
				} else {
					new MyImageAsyncTask(imageView).execute(imgUrl);
				}
				return v;
			}

		};
		pullToRefreshView.setAdapter(adapter);

	}

}
