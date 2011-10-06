package org.vt.ece4564;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class AsyncHttpTask implements Runnable {

	private HttpRequestInfo rInfo_;
	
	public AsyncHttpTask(HttpRequestInfo r) {
		super();
		rInfo_ = r;
	}

	public void run() {
		try{
			HttpClient client = new DefaultHttpClient();
			HttpResponse resp = client.execute(rInfo_.getRequest());
			rInfo_.setResponse(resp);
		}
		catch (Exception e) {
			rInfo_.setException(e);
		}
		
		rInfo_.requestFinished();
	}
	
	public void start(){
		// Your code goes here to create and start a thread
		// to execute the AsynHttpTask 
		Thread t = new Thread(this);
		t.start();
	}
}
