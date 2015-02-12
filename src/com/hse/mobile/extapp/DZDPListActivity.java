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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
 * 大众点评数据列表
 * 
 * @author dbz
 *
 */
public class DZDPListActivity extends BaseActivity {
	// App Key：480314895
	// App Secret：012e25459c3947e797104b397d1a0c6b.
	/**
	 * 大众点评数据
	 */
	List<Map<String, Object>> dataList = Collections
			.synchronizedList(new ArrayList<Map<String, Object>>());
	/**
	 * 展现点评数据的ListView
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
	private int page = 1;
	private int count = 10;
	private String server = "http://api.t.dianping.com/n/napi.xml";
	String params = "http://api.t.dianping.com/n/napi.xml?cityId=%s&page=%d&count=%d";

	// private String url =
	// "http://api.t.dianping.com/n/napi.xml?cityId={cityId}&page={page}&count={count}";
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dianping);
		ctx = this;
		initDropMenu();
		// initData();
		initList();
		new GetDataTask().execute();
	}

	/**
	 * 图片缓存
	 */
	private Map<String, Drawable> imageCache = new HashMap<String, Drawable>(5);

	class XmlDataHandler extends DefaultHandler {
		List<Map<String, Object>> handList = new ArrayList<Map<String, Object>>();
		Map<String, Object> data = new HashMap<String, Object>();
		private StringBuilder sb=new StringBuilder();

		@Override
		public void startDocument() throws SAXException {
			data = new HashMap<String, Object>();
		}

		@Override
		public void startElement(String uri, String localName, String qName,
				Attributes attributes) throws SAXException {
			if ("url".equals(localName)) {
				data = new HashMap<String, Object>();
			} else {
				sb.delete(0, sb.length());
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException {
			String theString = new String(ch, start, length);
			sb.append(theString);
		}

		@Override
		public void endElement(String uri, String localName, String qName)
				throws SAXException {
			if ("url".equals(localName)) {
				handList.add(data);
			} else if ("shortTitle".equals(localName)) {
				data.put("shangjia", sb.toString());
			} else if ("price".equals(localName)) {
				data.put("jiage", sb.toString());
			} else if ("title".equals(localName)) {
				data.put("jieshao", sb.toString());
			} else if ("image".equals(localName)) {
				data.put("imgUrl", sb.toString());
			} else if ("value".equals(localName)) {
				data.put("yuanjia", sb.toString());
			} else if ("productInfo".equals(localName)) {
				data.put("xiangqing", sb.toString());
			} else if ("city".equals(localName)) {
				data.put("dizhi", sb.toString());
			} else if ("shops".equals(localName)) {
				data.put("dianhua", sb.toString());
			}

		}
	}

	private class GetDataTask extends
			AsyncTask<Void, Void, List<Map<String, Object>>> {

		// 子线程请求数据
		@Override
		protected List<Map<String, Object>> doInBackground(Void... params) {
			String url = String.format("%s?cityId=%s&page=%d&count=%d", server,
					cityId, page, count);
			HttpGet httpGet = new HttpGet(url);
			DefaultHttpClient httpclient = new DefaultHttpClient();
			CookieUtil.setCookieSpec(httpclient);
			CookieUtil.writeCookie(httpclient);
			HttpResponse httpResponse;
			try {
				httpResponse = httpclient.execute(httpGet);
				String respData;
				if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
					respData = EntityUtils.toString(httpResponse.getEntity(),
							"UTF-8");
					// DocumentBuilderFactory dbf = DocumentBuilderFactory
					// .newInstance();
					SAXParserFactory factory = SAXParserFactory.newInstance();
					try {
						SAXParser parser = factory.newSAXParser();
						XMLReader reader = parser.getXMLReader();
						XmlDataHandler handler = new XmlDataHandler();
						reader.setContentHandler(handler);
						InputSource is = new InputSource();
						is.setCharacterStream(new StringReader(respData));
						reader.parse(is);
						return handler.handList;
					} catch (SAXException e) {
						Log.d("test", e.getMessage());
					} catch (ParserConfigurationException e) {
						Log.d("test", e.getMessage());
					}
				} else {
					return null;
				}
			} catch (ClientProtocolException e) {
				Log.d("test", e.getMessage());
			} catch (IOException e) {
				Log.d("test", e.getMessage());
			}
			return null;
		}

		// 主线程更新UI
		@Override
		protected void onPostExecute(List<Map<String, Object>> result) {
			if (result != null && result.size() > 0) {
				if (page == 1) {
					dataList.clear();
				}
				page++;
				dataList.addAll(result);
				adapter.notifyDataSetChanged();
			} else {
				Toast.makeText(DZDPListActivity.this, "已经是最后一页了",
						Toast.LENGTH_SHORT).show();
			}
			// 通知RefreshListView 我们已经更新完成
			pullToRefreshView.onRefreshComplete();
		}
	}

	private void showDetail(int index) {
		HashMap<String, Object> data = (HashMap<String, Object>) dataList
				.get(index);
		data.put("img", this.imageCache.get(data.get("imgUrl")));
		Intent intent = new Intent(DZDPListActivity.this,
				DZDPDetailActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		Bundle bundle = this.getIntent().getExtras();
		bundle.putSerializable(MessageKey.DZDP_DETAIL, data);
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
						page = 1;
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

	private void initDropMenu() {

	}
}
