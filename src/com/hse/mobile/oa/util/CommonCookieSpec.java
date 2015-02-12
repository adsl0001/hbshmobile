package com.hse.mobile.oa.util;

import org.apache.http.cookie.ClientCookie;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.cookie.SetCookie;
import org.apache.http.impl.cookie.BasicExpiresHandler;
import org.apache.http.impl.cookie.BrowserCompatSpec;

class CommonCookieSpec extends BrowserCompatSpec {
	public CommonCookieSpec() {
		super();
		registerAttribHandler(ClientCookie.EXPIRES_ATTR,
				new BasicExpiresHandler(DATE_PATTERNS) {
			
					@Override
					public void parse(SetCookie cookie, String value)
							throws MalformedCookieException {
						value = value.replace("\"", "");
						super.parse(cookie, value);
					}
				});
	}
}
