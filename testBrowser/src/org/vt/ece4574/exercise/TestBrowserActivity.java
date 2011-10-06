package org.vt.ece4574.exercise;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TestBrowserActivity extends Activity {
    
    private EditText url_ = null;
    

	public EditText getUrl_() {
		return url_;
	}


	public void setUrl_(EditText url_) {
		this.url_ = url_;
	}


	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        EditText url = (EditText)findViewById(R.id.editText1);
        url_ = url;
        Button b = (Button)findViewById(R.id.goButton);
        b.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				url_.setText("Foo");
			}
		});
       
    }
}