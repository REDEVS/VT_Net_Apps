package com.shadow.test.list;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HelloListViewActivity extends ListActivity {
    /** Called when the activity is first created. */
	
	private String[] Items = {"Empty"};
	@Override
	public void onCreate(Bundle savedInstanceState) {
	  super.onCreate(savedInstanceState);

	  
	  setListAdapter(new ArrayAdapter<String>(this, R.layout.list_item, Items));

	  ListView lv = getListView();
	  lv.setTextFilterEnabled(true);
	  lv.setOnItemClickListener(new OnItemClickListener() {
		    

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Toast.makeText(getApplicationContext(), ((TextView) arg1).getText(),
				          Toast.LENGTH_SHORT).show();
				    				
			}
		  });
	}
   
}