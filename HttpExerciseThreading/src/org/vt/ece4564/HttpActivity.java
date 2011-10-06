package org.vt.ece4564;

import org.apache.http.HttpResponse;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class HttpActivity extends Activity implements View.OnClickListener, HttpCallback{

	// A Handler is a special Android class that
	// allows you to pass Runnable objects to the
	// GUI thread to execute. 
	private Handler handler_ = new Handler();

	private HttpUtils utils_ = HttpUtils.get();
	private TextView responseValue_;
	private TextView requestStatus_;
	private Button goButton_;
	private EditText urlField_;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		urlField_ = (EditText) findViewById(R.id.urlField);
		goButton_ = (Button) findViewById(R.id.goButton);

		// Your code should connect the requestStatus_ and
		// responseValue_ variables to the appropriate GUI
		// elements

		requestStatus_ = (TextView)findViewById(R.id.requestStatus);
	    responseValue_ = (TextView)findViewById(R.id.responseValue);
		

		// Your code should set the onClickListener of the
		// goButton_ so that the onClick() method is invoked
		// when the button is clicked
	    goButton_.setOnClickListener(this);
	}

	public void onClick(View v) {
		String url = urlField_.getText().toString();
	
		
		// Your code goes here to execute an HTTP
		// request using the HttpUtils class.
        // You must pass in some form of listener
		// so that the appropriate onError or onResponse method
		// is called when the request completes
		utils_.doGet(url, this);
	}

	public void onError(Exception e) {
		requestStatus_.setText("Request failed: " + e.getMessage());
	}

	public void onResponse(final HttpResponse resp) {
		// The GUI runs in a separate thread. We
		// cannot directly modify the GUI from another
		// thread so we have to pass it code to execute.
		// This code segment creates a Runnable that
		// is then passed to the GUI thread by calling
		// handler_.post()
		handler_.post(new Runnable() {

			@Override
			public void run() {
				try {
					requestStatus_.setText("Request succeeded!");
					responseValue_.setText(utils_.responseToString(resp));
				} catch (Exception e) {
					requestStatus_.setText("Error processing response: "
							+ e.getMessage());
				}

			}
		});

	}

}