package com.shadow.test.tabs;


import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class TabsActivity extends TabActivity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.main);

	    TabHost tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, randoActivity.class);

	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("artists").setIndicator("Artists").setContent(intent);
	    tabHost.addTab(spec);

	    // Do the same for the other tab
	    intent = new Intent().setClass(this, randoActivity.class);
	    spec = tabHost.newTabSpec("albums").setIndicator("Albums").setContent(intent);
	    tabHost.addTab(spec);


	    tabHost.setCurrentTab(1);
	}
	
}
