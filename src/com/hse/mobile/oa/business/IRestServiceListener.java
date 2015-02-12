package com.hse.mobile.oa.business;

import org.json.JSONObject;

/*
 * Rest服务结果监听器
 */
public interface IRestServiceListener {
	public void onRestServiceResult(JSONObject result);
}
