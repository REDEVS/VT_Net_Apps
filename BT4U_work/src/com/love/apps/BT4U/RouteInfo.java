package com.love.apps.BT4U;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
//import com.google.ads.*;


/*
 * class for the main bus times activity
 */
public class RouteInfo extends Activity {
	/** Called when the activity is first created. */

	private TextView view2_ = null;//where times are printed to
	private Spinner spinner_ = null;//bus names
	private Spinner spinner2_ = null;//bus stops
	private ArrayAdapter<String> adapter_ = null;
	private String array_spinner[];
	private Map<String, String> stops_ = new HashMap<String, String>();
	ArrayAdapter<String> adapter2_ = null;
	private String[] CurrentStops_ = null;
	private Map<String, String> routes_ = new HashMap<String, String>();
	private Favorites favorites_ = new Favorites();
	public static final String PREFS_NAME = "MyPrefsFile";
	public int timesToShow;
	int selection;


	//handles what happens when activity is started
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.listplaceholder);
		//setIntent(R.layout.listplaceholder);

		//AdView adView = new AdView(this, AdSize.)

		view2_ = (TextView) findViewById(R.id.textView2);
		spinner_ = (Spinner) findViewById(R.id.spinner1);									
		spinner2_ = (Spinner) findViewById(R.id.spinner2);
		view2_.setMovementMethod(new ScrollingMovementMethod());
		array_spinner = new String[15];

		// Restore preferences
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		timesToShow = settings.getInt("timesShown", 5);


		spinner2_.setClickable(false);

		FileRead reader = new FileRead();
		Resources myResource = getResources();
		reader.readFromFile(myResource);
		stops_ = reader.stops_;


		setUpRoutes();
		//gets all available busses and stores them in the first spinner
		HttpUtils.get().doGet("http://www.bt4u.org/BT4U_WebService.asmx/GetRoutes?stopCode=", new HttpCallback(){

			@Override
			public void onResponse(HttpResponse resp) {
				// TODO Auto-generated method stub
				String xml = null;
				try {
					xml = HttpUtils.get().responseToString(resp);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				Document doc = XMLfunctions.XMLfromString(xml);

				int numResults = XMLfunctions.numResults(doc);

				if((numResults <= 0)){
					Toast.makeText(RouteInfo.this, "Geen resultaten gevonden", Toast.LENGTH_LONG).show();  
					finish();
				}



				NodeList nodes = doc.getElementsByTagName("CurrentRoutes");

				array_spinner = new String[nodes.getLength() + 1]; 
				array_spinner[0] = "---Select---";

				String[] routesActual = new String[nodes.getLength() + 1]; 
				routesActual[0] = "---Select---";
				for (int i = 0; i < nodes.getLength(); i++) {		

					Element e = (Element)nodes.item(i);


					//view_.append(XMLfunctions.getValue(e, "RouteShortName") + "\n");
					array_spinner[i+1] = XMLfunctions.getValue(e, "RouteShortName");
					routesActual[i+1] =  routes_.get(array_spinner[i+1]);
					if(routes_.get(array_spinner[i+1]) == null) routesActual[i+1] = array_spinner[i+1];


				}
				if(array_spinner.length == 1) 
				{
					view2_.setText("The BT4U webservice seems to be down right now. Please try back later.");

				}
				else{
					adapter_ = new ArrayAdapter<String>(RouteInfo.this,android.R.layout.simple_spinner_item, routesActual);
					adapter_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					spinner_.setAdapter(adapter_);  


				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				view2_.setText("It appears that the BT4U website is not responding. Please try again shortly.");

			}

		});


		//sets up what happens if item is selected in spinner
		spinner_.setOnItemSelectedListener(new OnItemSelectedListener()  {


			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub



				if(spinner_.getSelectedItem().equals("---Select---"))
				{
					view2_.setText("");
					spinner2_.setClickable(false);
					String[] routes = new String[10];
					routes[0] = "---Select---";
					adapter2_ = new ArrayAdapter<String>(RouteInfo.this ,android.R.layout.simple_spinner_item, routes);
					adapter2_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

					spinner2_.setAdapter(adapter2_);  
					spinner2_.setSelection(0);

					return;
				}
				//spinner2_.setClickable(true);
				//spinner2_.setSelection(0);
				//view2_.setText("http://www.bt4u.org/BT4U_WebService.asmx/GetStopCodes?routeShortName=" + spinner_.getSelectedItem());
				String url = ("http://www.bt4u.org/BT4U_WebService.asmx/GetStopCodes?routeShortName=" + array_spinner[spinner_.getSelectedItemPosition()]);
				HttpUtils.get().doGet(url, new HttpCallback(){

					@Override
					public void onResponse(HttpResponse resp) {
						// TODO Auto-generated method stub

						Document doc2 = null;
						try {
							doc2 = XMLfunctions.XMLfromString(HttpUtils.get().responseToString(resp));
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}


						NodeList nodes2 = doc2.getElementsByTagName("CurrentStop");
						CurrentStops_ = null;
						CurrentStops_ = new String[nodes2.getLength() + 1];
						String[] routes = new String[nodes2.getLength() + 1];


						//view2_.setText("http://www.bt4u.org/BT4U_WebService.asmx/GetStopCodes?routeShortName=" + array_spinner[spinner_.getSelectedItemPosition()] + "\nthe length of nodes is: " + nodes2.getLength());

						CurrentStops_[0] = "---Select---";
						routes[0] = "---Select---";
						for (int i = 0; i < nodes2.getLength(); i++) {		


							Element e2 = (Element)nodes2.item(i);

							//view2_.append(XMLfunctions.getValue(e2, "StopCode")+ "\n");
							CurrentStops_[i+1] = XMLfunctions.getValue(e2, "StopCode");
							routes[i+1] = CurrentStops_[i+1] + " - " + stops_.get(CurrentStops_[i+1]);
							if(stops_.get(CurrentStops_[i+1]) == null) routes[i+1] = CurrentStops_[i+1];
							//view2_.append("Here is some data: " + routes[i+1] + " " + CurrentStops_[i+1] + " \n");

						}


						adapter2_ = new ArrayAdapter<String>(RouteInfo.this,android.R.layout.simple_spinner_item, routes);
						adapter2_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


						spinner2_.setAdapter(adapter2_);  
						//view2_.append(text);
						//view2_.append("\nBus: " + spinner_.getSelectedItem() + " Stop: " + spinner2_.getSelectedItem() );
						spinner2_.setSelected(false);


					}

					@Override
					public void onError(Exception e) {
						// TODO Auto-generated method stub

					}

				});




			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}

		});

		//sets up what happens if item is selected in spinner2
		spinner2_.setOnItemSelectedListener( new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if(!spinner_.getSelectedItem().equals("---Select---"))
				{
					spinner2_.setClickable(true);
					if(!spinner2_.getSelectedItem().equals("---Select---"))
					{
						//String url = "www.bt4u.org/BT4U_WebService.asmx/GetNextDepartures?routeShortName="+ spinner_.getSelectedItem() + "&stopCode=" + spinner2_.getSelectedItem();
						String url =  "http://www.bt4u.org//BT4U_WebService.asmx/GetNextDepartures";//BT4U_WebService.asmx/GetNextDepartures?routeShortName=" + array_spinner[spinner_.getSelectedItemPosition()] +  "&stopCode=" + CurrentStops_[spinner2_.getSelectedItemPosition()];  + spinner_.getSelectedItem();

						Map<String, String> args = new HashMap<String, String>();
						args.put("routeShortName", array_spinner[spinner_.getSelectedItemPosition()]);
						args.put("StopCode", CurrentStops_[spinner2_.getSelectedItemPosition()]);
						//view2_.append("\n" + url + "  " +  array_spinner[spinner_.getSelectedItemPosition()] + "\n");

						HttpUtils.get().doPost(url, args, new HttpCallback(){

							@Override
							public void onResponse(HttpResponse resp) {


								PrintXml printer = new PrintXml();

								printer.execute(resp);







							}



							@Override
							public void onError(Exception e) {
								// TODO Auto-generated method stub

							}


						});

						return;
					}


				} 

				if (spinner_.getSelectedItem().equals("---Select---") ) spinner2_.setClickable(false);
				view2_.setText("");



			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	protected void onStop(){
		super.onStop();

		// We need an Editor object to make preference changes.
		// All objects are from android.context.Context
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("timesShown", timesToShow);

		// Commit the edits!
		editor.commit();
	}

	//adds actual names for bus routes
	private void setUpRoutes() {
		// TODO Auto-generated method stub
		routes_.put("MSN", "Main Street Northbound");
		routes_.put("MSS", "Main Street Southbound");
		routes_.put("HDG", "Harding Ave");
		routes_.put("HWD", "Hethwood");
		routes_.put("TTT", "Two Town Trolley");
		routes_.put("UMS", "University Mall Shuttle");
		routes_.put("PRG", "Progress Street");
		routes_.put("PH", "Patrick Henry");
		routes_.put("HXP", "Hokie Express");
		routes_.put("TC", "Toms Creek");
		routes_.put("CRC", "Corporate Research Center");
		routes_.put("UCB", "University City Boulevard");

	}



	//returns all stops for given route
	public String[] getStops(String Route, String xml) throws XmlPullParserException, IOException
	{


		XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
		factory.setNamespaceAware(true);
		XmlPullParser xpp = factory.newPullParser();


		xpp.setInput( new StringReader ( xml ) );
		int eventType = xpp.getEventType();
		while(eventType != XmlPullParser.END_DOCUMENT){

			if(eventType == XmlPullParser.START_TAG) {

				String name = xpp.getName();
				if(name.equalsIgnoreCase("patternpointname"))
				{
					xpp.next();
				}

			} 


		}
		eventType = xpp.next();
		return array_spinner;
	}


	//creates options menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu, menu);
		return true;
	}

	public void makeToast(String message)
	{
		Toast.makeText(getApplicationContext(), message,
				Toast.LENGTH_SHORT).show();
	}


	//sets up what happens when different options are chosen 
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.about:
			//setContentView(R.layout.main);
			Intent i = new Intent(getApplicationContext(), aboutMe.class);
			startActivity(i);
			//view2_.setText("Success");
			return true;
		case R.id.favorites:
			//view2_.setText("Success even more");
			if (spinner_.getSelectedItem().equals("---Select---") || spinner2_.getSelectedItem().equals("---Select---"))
			{

				return true;				
			}
			try {
				String path =  Environment.getExternalStorageDirectory() + "/BT4U/";


				//view2_.setText("");
				File root = new File(path);
				//view2_.setText("I AM HERE ");


				root.mkdirs();


				File f = new File(root, "favorites.txt");
				if(!f.exists())
				{
					//view2_.setText("now I am here");
					FileOutputStream fos = new FileOutputStream(f);
					fos.close();

				}
				String content= "";

				FileReader r = new FileReader(f);
				BufferedReader br = new BufferedReader(r);

				String line = null;
				while((line = br.readLine()) != null){
					content += line;
					content += "\n";
				}
				//this is where I stopped for some reason the files are acting weird






				FileWriter writer;

				writer = new FileWriter(f);

				String yourdata = content + spinner_.getSelectedItem() + "," + array_spinner[spinner_.getSelectedItemPosition()] + "," + spinner2_.getSelectedItem() + "," + CurrentStops_[spinner2_.getSelectedItemPosition()] ;

				writer.write(yourdata);

				writer.close();

				Toast.makeText(getApplicationContext(), "Saved to Favorites", Toast.LENGTH_SHORT).show();
				favorites_.updateFavorites();
				//view2_.append("\n HELLO I AM CONTENT" + content);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return true;
		case R.id.changeTimes:
			final String[] items = {"1", "3", "5", "All"};


			String current = Integer.toString(timesToShow);
			if(current.equals("200")) current = "All";
			makeToast("Current: " + current);
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Times To Display:");
			builder.setSingleChoiceItems(items, 0,new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {

					//makeToast(Integer.toString(timesToShow));
					if(item == 3)
						selection = 200;
					else
						selection = Integer.parseInt(items[item]);
				}

			});
			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					// String value = input.getText().toString();
					// Do something with value!


				}
			});builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					timesToShow = selection;
					SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
					SharedPreferences.Editor editor = settings.edit();
					editor.putInt("timesShown", timesToShow);

					// Commit the edits!
					editor.commit();

				}
			});
			builder.show();

			return true;
		case R.id.refresh:
			//if(spinner2_.getCount() == 0) makeToast("The spinner seems to be empty");
			if(spinner2_.getCount() != 0) 
			{
				if(!spinner2_.getSelectedItem().equals("---Select---")){
					//makeToast("The spinner seems to be holding")
					String url =  "http://www.bt4u.org//BT4U_WebService.asmx/GetNextDepartures";//BT4U_WebService.asmx/GetNextDepartures?routeShortName=" + array_spinner[spinner_.getSelectedItemPosition()] +  "&stopCode=" + CurrentStops_[spinner2_.getSelectedItemPosition()];  + spinner_.getSelectedItem();

					Map<String, String> args = new HashMap<String, String>();
					args.put("routeShortName", array_spinner[spinner_.getSelectedItemPosition()]);
					args.put("StopCode", CurrentStops_[spinner2_.getSelectedItemPosition()]);
					//view2_.append("\n" + url + "  " +  array_spinner[spinner_.getSelectedItemPosition()] + "\n");

					HttpUtils.get().doPost(url, args, new HttpCallback(){

						@Override
						public void onResponse(HttpResponse resp) {


							PrintXml printer = new PrintXml();

							printer.execute(resp);







						}



						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub

						}


					});

					return true;
				}
			}

			setUpRoutes();
			//gets all available busses and stores them in the first spinner
			HttpUtils.get().doGet("http://www.bt4u.org/BT4U_WebService.asmx/GetRoutes?stopCode=", new HttpCallback(){

				@Override
				public void onResponse(HttpResponse resp) {
					// TODO Auto-generated method stub
					String xml = null;
					try {
						xml = HttpUtils.get().responseToString(resp);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
					Document doc = XMLfunctions.XMLfromString(xml);

					int numResults = XMLfunctions.numResults(doc);

					if((numResults <= 0)){
						Toast.makeText(RouteInfo.this, "Geen resultaten gevonden", Toast.LENGTH_LONG).show();  
						finish();
					}



					NodeList nodes = doc.getElementsByTagName("CurrentRoutes");

					array_spinner = new String[nodes.getLength() + 1]; 
					array_spinner[0] = "---Select---";

					String[] routesActual = new String[nodes.getLength() + 1]; 
					routesActual[0] = "---Select---";
					for (int i = 0; i < nodes.getLength(); i++) {		

						Element e = (Element)nodes.item(i);


						//view_.append(XMLfunctions.getValue(e, "RouteShortName") + "\n");
						array_spinner[i+1] = XMLfunctions.getValue(e, "RouteShortName");
						routesActual[i+1] =  routes_.get(array_spinner[i+1]);
						if(routes_.get(array_spinner[i+1]) == null) routesActual[i+1] = array_spinner[i+1];


					}
					if(array_spinner.length == 1) 
					{
						view2_.setText("The BT4U webservice seems to be down right now. Please try back later.");

					}
					else{
						adapter_ = new ArrayAdapter<String>(RouteInfo.this,android.R.layout.simple_spinner_item, routesActual);
						adapter_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spinner_.setAdapter(adapter_);  


					}
				}

				@Override
				public void onError(Exception e) {
					// TODO Auto-generated method stub
					view2_.setText("It appears that the BT4U website is not responding. Please try again shortly.");

				}

			});


			//sets up what happens if item is selected in spinner
			spinner_.setOnItemSelectedListener(new OnItemSelectedListener()  {


				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int arg2, long arg3) {
					// TODO Auto-generated method stub



					if(spinner_.getSelectedItem().equals("---Select---"))
					{
						view2_.setText("");
						spinner2_.setClickable(false);
						String[] routes = new String[10];
						routes[0] = "---Select---";
						adapter2_ = new ArrayAdapter<String>(RouteInfo.this ,android.R.layout.simple_spinner_item, routes);
						adapter2_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						spinner2_.setAdapter(adapter2_);  
						spinner2_.setSelection(0);

						return;
					}
					//spinner2_.setClickable(true);
					//spinner2_.setSelection(0);
					//view2_.setText("http://www.bt4u.org/BT4U_WebService.asmx/GetStopCodes?routeShortName=" + spinner_.getSelectedItem());
					String url = ("http://www.bt4u.org/BT4U_WebService.asmx/GetStopCodes?routeShortName=" + array_spinner[spinner_.getSelectedItemPosition()]);
					HttpUtils.get().doGet(url, new HttpCallback(){

						@Override
						public void onResponse(HttpResponse resp) {
							// TODO Auto-generated method stub

							Document doc2 = null;
							try {
								doc2 = XMLfunctions.XMLfromString(HttpUtils.get().responseToString(resp));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}


							NodeList nodes2 = doc2.getElementsByTagName("CurrentStop");
							CurrentStops_ = null;
							CurrentStops_ = new String[nodes2.getLength() + 1];
							String[] routes = new String[nodes2.getLength() + 1];


							//view2_.setText("http://www.bt4u.org/BT4U_WebService.asmx/GetStopCodes?routeShortName=" + array_spinner[spinner_.getSelectedItemPosition()] + "\nthe length of nodes is: " + nodes2.getLength());

							CurrentStops_[0] = "---Select---";
							routes[0] = "---Select---";
							for (int i = 0; i < nodes2.getLength(); i++) {		


								Element e2 = (Element)nodes2.item(i);

								//view2_.append(XMLfunctions.getValue(e2, "StopCode")+ "\n");
								CurrentStops_[i+1] = XMLfunctions.getValue(e2, "StopCode");
								routes[i+1] = CurrentStops_[i+1] + " - " + stops_.get(CurrentStops_[i+1]);
								if(stops_.get(CurrentStops_[i+1]) == null) routes[i+1] = CurrentStops_[i+1];
								//view2_.append("Here is some data: " + routes[i+1] + " " + CurrentStops_[i+1] + " \n");

							}


							adapter2_ = new ArrayAdapter<String>(RouteInfo.this,android.R.layout.simple_spinner_item, routes);
							adapter2_.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


							spinner2_.setAdapter(adapter2_);  
							//view2_.append(text);
							//view2_.append("\nBus: " + spinner_.getSelectedItem() + " Stop: " + spinner2_.getSelectedItem() );
							spinner2_.setSelected(false);


						}

						@Override
						public void onError(Exception e) {
							// TODO Auto-generated method stub

						}

					});




				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub

				}

			});

			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}



	//async class that is used to process all data from httputils in background threads
	class PrintXml extends AsyncTask<HttpResponse, Void, String>{



		@Override
		protected String doInBackground(HttpResponse... params) {
			// TODO Auto-generated method stub
			String data = null;
			try {
				data = printXml(HttpUtils.get().responseToString(params[0]));

			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return data;

		}

		@Override
		protected void onPostExecute(String result) {

			if(view2_ != null)
			{
				view2_.setTextSize(17);
				view2_.setText(result);
			}

		}


		public String printXml(String xml) throws XmlPullParserException, IOException, InterruptedException
		{

			//wait();
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser xpp = factory.newPullParser();


			xpp.setInput( new StringReader ( xml ) );
			int eventType = xpp.getEventType();

			String temp = "Arrival Times: \n";


			//while(eventType != XmlPullParser.END_DOCUMENT){
			int i = 0;
			while(i < timesToShow){

				if(eventType == XmlPullParser.START_DOCUMENT) {
					temp += "";
					//theView.setText("");//Start document");
				} else if(eventType == XmlPullParser.START_TAG) {

					String name = xpp.getName();
					if(name.equalsIgnoreCase("patternpointname"))
					{

						//temp += "Stop Location:    ";
						xpp.next();
						//temp += xpp.getText() + "\n";

					}
					if(name.equalsIgnoreCase("adjusteddeparturetime"))
					{

						//temp += "Arrival Time:    ";
						xpp.next();
						temp += "\t" + xpp.getText() + "\n";	
						i++;


					}
				} else if(eventType == XmlPullParser.END_DOCUMENT) {
					i = timesToShow;
				} else if(eventType == XmlPullParser.TEXT) {
					temp += ("");


				}
				eventType = xpp.next();
			}

			if(temp.equals("Arrival Times: \n"))
			{
				temp = ("No route information available.");
			}


			return temp;
		}


	}
}



