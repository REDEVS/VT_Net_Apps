package org.developerworks.android;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

public abstract class BaseFeedParser implements FeedParser {

	// names of the XML tags
	static final String CHANNEL = "CurrentRoutes";
	static final String PUB_DATE = "RouteShortName";
	static final  String DESCRIPTION = "RouteShortName";
	static final  String LINK = "RouteShortName";
	static final  String TITLE = "RouteShortName";
	static final  String ITEM = "RouteShortName";
	
	private final URL feedUrl;

	protected BaseFeedParser(String feedUrl){
		try {
			this.feedUrl = new URL(feedUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		}
	}

	protected InputStream getInputStream() {
		try {
			return feedUrl.openConnection().getInputStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}