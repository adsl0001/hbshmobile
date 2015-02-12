package com.hse.mobile.oa.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class NetUtil {
	
	/*
	 * ����JS��encodeURI
	 */
	public static String encodeURI(String url){
		String result = url;
		try {
			if(!url.contains("%2F") && !url.contains("%2f")){
				//���û����urlencode, ����һ��
				result = URLEncoder.encode(url,"utf-8");
			}
			
			//�ָ������Ĳ���
			result = result.replaceAll("%2F","/");  
			result = result.replaceAll("%3A",":");  
			result = result.replaceAll("%3F","?");  
			result = result.replaceAll("%3D","="); 	
			result = result.replaceAll("%26","&"); 		
			result = result.replaceAll("\\+","%20"); 
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}

}
