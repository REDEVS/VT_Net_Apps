package com.pxr.tutorial.xmltest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ListActivity implements OnItemSelectedListener,HttpCallback {
	/** Called when the activity is first created. */


	private TextView view2_ = null;
	private Spinner spinner_ = null;
	private Spinner spinner2_ = null;
	private ArrayAdapter<String> adapter_ = null;
	private String array_spinner[];
	ArrayAdapter<String> adapter2_ = null;


	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listplaceholder);

		view2_ = (TextView) findViewById(R.id.textView2);
		spinner_ = (Spinner) findViewById(R.id.spinner1);
		spinner2_ = (Spinner) findViewById(R.id.spinner2);
		array_spinner = new String[13];

		spinner2_.setClickable(false);

		// ArrayList<HashMap<String, String>> mylist = new ArrayList<HashMap<String, String>>();


		String xml = XMLfunctions.getXML("http://www.bt4u.org/BT4U_WebService.asmx/GetRoutes?stopCode=");

		Document doc = XMLfunctions.XMLfromString(xml);

		int numResults = XMLfunctions.numResults(doc);

		if((numResults <= 0)){
			Toast.makeText(Main.this, "Geen resultaten gevonden", Toast.LENGTH_LONG).show();  
			finish();
		}



		NodeList nodes = doc.getElementsByTagName("CurrentRoutes");



		for (int i = 0; i < nodes.getLength(); i++) {		

			Element e = (Element)nodes.item(i);


			//view_.append(XMLfunctions.getValue(e, "RouteShortName") + "\n");
			array_spinner[i] = XMLfunctions.getValue(e, "RouteShortName");


		}
		adapter_ = new ArrayAdapter(this,android.R.layout.simple_spinner_item, array_spinner);
		adapter_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner_.setAdapter(adapter_);  


		spinner_.setOnItemSelectedListener(this);
		spinner2_.setOnItemSelectedListener(this);

	}








	




	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub
		//view2_.setText("arg1: " + arg1.toString() + "\n\nspinner_: " + spinner_.toString() + "\n\nspinner2_: " + spinner2_.toString() + "\n\narg0: " + arg0.toString() );

		
		spinner2_.setClickable(true);
		if((arg0.toString()).equalsIgnoreCase(spinner2_.toString())) 
		{
			view2_.setText("\nBus: " + spinner_.getSelectedItem() + " Stop: " + spinner2_.getSelectedItem().toString() );

			

 			
			
			
			view2_.append("\nAbout to post");
			
		
			String url = "www.bt4u.org/BT4U_WebService.asmx/GetNextDepartures?routeShortName=msn&stopCode=1409";
			view2_.append("\n" + url);
			HttpUtils.get().doGet(url, this);
//			Document doc2 = XMLfunctions.XMLfromString(xml2);
//			NodeList nodes2 = doc2.getElementsByTagName("NextDepartures");
//			
//
//			for (int i = 0; i < nodes2.getLength() || i < 3; i++) {		
//
//
//				Element e2 = (Element)nodes2.item(i);
//
//				//view2_.append(XMLfunctions.getValue(e2, "StopCode")+ "\n");
//				view2_.setText( XMLfunctions.getValue(e2, "RouteShortName") + "\n");
//				view2_.append( XMLfunctions.getValue(e2, "PatternPointName") + "\n");
//				view2_.append( XMLfunctions.getValue(e2, "AdjustedDepartureTime") + "\n");
//
//
//			}
			
			
			return;
		}
		
		String xml2 = XMLfunctions.getXML("http://www.bt4u.org/BT4U_WebService.asmx/GetStopCodes?routeShortName=" + spinner_.getSelectedItem());
		Document doc2 = XMLfunctions.XMLfromString(xml2);
		NodeList nodes2 = doc2.getElementsByTagName("CurrentStop");
		String[] array_spinner2 = new String[nodes2.getLength() + 1];

		array_spinner2[0] = "   ";
		for (int i = 0; i < nodes2.getLength(); i++) {		


			Element e2 = (Element)nodes2.item(i);

			//view2_.append(XMLfunctions.getValue(e2, "StopCode")+ "\n");
			array_spinner2[i+1] = XMLfunctions.getValue(e2, "StopCode");

		}


		adapter2_ = new ArrayAdapter(this,android.R.layout.simple_spinner_item, array_spinner2);
		adapter2_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spinner2_.setAdapter(adapter2_);  
		//view2_.append(text);
		view2_.append("\nBus: " + spinner_.getSelectedItem() + " Stop: " + spinner2_.getSelectedItem() );
		spinner2_.setSelected(false);



	}








	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}








	@Override
	public void onResponse(HttpResponse resp) {
		// TODO Auto-generated method stub
		view2_.append("HHHHHHHJHJLSADLFJHASLDJFHASLDJFHALSJDFHLJ");

		try {
			view2_.append(HttpUtils.get().responseToString(resp));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}








	@Override
	public void onError(Exception e) {
		// TODO Auto-generated method stub
		Toast t = Toast.makeText(this,
				"Error fetching schedule! " + e.getLocalizedMessage(), 2000);
		t.show();
	}
}


