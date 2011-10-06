package com.love.apps.BT4U;

//
//
//import org.apache.http.HttpResponse;
//import org.apache.http.client.HttpClient;
//import org.apache.http.impl.client.DefaultHttpClient;
//
//import android.os.AsyncTask;
//
//public class AsyncHttpTask extends
//		AsyncTask<HttpRequestInfo, Integer, HttpRequestInfo> {
//
//	@Override
//	protected HttpRequestInfo doInBackground(HttpRequestInfo... params) {
//		HttpRequestInfo rInfo = params[0];
//		try {
//			HttpClient client = new DefaultHttpClient();
//			HttpResponse resp = client.execute(rInfo.getRequest());
//			rInfo.setResponse(resp);
//		} catch (Exception e) {
//			rInfo.setException(e);
//		}
//
//		return rInfo;
//	}
//
//	@Override
//	protected void onPostExecute(HttpRequestInfo result) {
//		super.onPostExecute(result);
//		result.requestFinished();
//	}
//
//}


import org.apache.http.HttpResponse;


import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class AsyncHttpTask extends
		AsyncTask<HttpRequestInfo, Integer, HttpRequestInfo> {

	@Override
	protected HttpRequestInfo doInBackground(HttpRequestInfo... params) {
		HttpRequestInfo rInfo = params[0];
		try {
			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = client.execute(rInfo.getRequest());
			rInfo.setResponse(resp);
		} catch (Exception e) {
			rInfo.setException(e);
		}

		return rInfo;
	}

	@Override
	protected void onPostExecute(HttpRequestInfo result) {
		super.onPostExecute(result);
		result.requestFinished();
	}

}
