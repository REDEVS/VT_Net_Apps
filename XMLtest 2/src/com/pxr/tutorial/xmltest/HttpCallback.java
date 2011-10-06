package com.pxr.tutorial.xmltest;

import org.apache.http.HttpResponse;

public interface HttpCallback {

	public void onResponse(HttpResponse resp);
	public void onError(Exception e);
	
}
