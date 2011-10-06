package com.androidpeople.xml.parsing;

import java.net.URL;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import android.app.Activity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class XMLParsingExample extends Activity {

	/** Create Object For SiteList Class */
	SitesList sitesList = null;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		/** Create a new layout to display the view */
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(1);

		/** Create a new textview array to display the results */
		TextView name[];
		TextView website[];
		TextView category[];

		try {
			
			/** Handling XML */
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();

			/** Send URL to parse XML Tags */
			URL sourceUrl = new URL(
					"http://www.androidpeople.com/wp-content/uploads/2010/06/example.xml");

			/** Create handler to handle XML Tags ( extends DefaultHandler ) */
			MyXMLHandler myXMLHandler = new MyXMLHandler();
			xr.setContentHandler(myXMLHandler);
			xr.parse(new InputSource(sourceUrl.openStream()));
			
		} catch (Exception e) {
			System.out.println("XML Pasing Excpetion = " + e);
		}

		/** Get result from MyXMLHandler SitlesList Object */
		sitesList = MyXMLHandler.sitesList;

		/** Assign textview array lenght by arraylist size */
		name = new TextView[sitesList.getName().size()];
		website = new TextView[sitesList.getName().size()];
		category = new TextView[sitesList.getName().size()];

		/** Set the result text in textview and add it to layout */
		for (int i = 0; i < sitesList.getName().size(); i++) {
			name[i] = new TextView(this);
			name[i].setText("Name = "+sitesList.getName().get(i));
			website[i] = new TextView(this);
			website[i].setText("Website = "+sitesList.getWebsite().get(i));
			category[i] = new TextView(this);
			category[i].setText("Website Category = "+sitesList.getCategory().get(i));

			layout.addView(name[i]);
			layout.addView(website[i]);
			layout.addView(category[i]);
		}

		/** Set the layout view to display */
		setContentView(layout);

	}
}