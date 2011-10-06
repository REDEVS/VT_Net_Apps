package org.eece261.vsharetcpip;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.apache.commons.io.IOUtils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class VShare extends Activity implements OnInitListener{

	private EditText server_;
	private Button share_;

	private EditText port_;
	
	private int CODE = 1;
	private TextToSpeech textToSpeech_;

	/**
	 * This method is the entry point to the VShare code. The "onCreate" method
	 * is typically what you need to override to get your Activity started.
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		server_ = (EditText) findViewById(R.id.server);

		Intent checkIntent = new Intent();
		checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
		startActivityForResult(checkIntent, CODE);
		
		port_ = (EditText) findViewById(R.id.EditText01);


		// This is the button that the user will click
		// to submit the file.
		share_ = (Button) findViewById(R.id.shareIt);

		// We add a click listener to invoke the share
		// machinery when the user is done.
		share_.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// Temporarily disable the
				// share button.
				share_.setClickable(false);

				// Grab the destination network information
				// and invoke the sharing mechanism.
				String server = server_.getText().toString();
				doShare(server);

				// Enable the share button again.
				share_.setClickable(true);

				// Close the Activity
				VShare.this.finish();
			}
		});

		
	}
	
	protected void onActivityResult(
	        int requestCode, int resultCode, Intent data) {
	    if (requestCode == CODE) {
	        if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
	            // success, create the TTS instance
	        	textToSpeech_ = new TextToSpeech(this, this);
	        } else {
	            // missing data, install it
	            Intent installIntent = new Intent();
	            installIntent.setAction(
	                TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
	            startActivity(installIntent);
	        }
	    }
	}
	

	@Override
	public void onInit(int status) {
		
	}


	/**
	 * If the Activity was called via a SEND intent with an associated stream,
	 * this method resolves that stream and pipes the data across the network to
	 * the VShare server.
	 * 
	 * @param server
	 * @param port
	 */
	public void doShare(String server) {
		try {

			// First, we need to figure out who
			// called us.
			Intent intent = getIntent();

			// Now, we make sure that someone passed us
			// a stream to send to the server.
			if (Intent.ACTION_SEND.equals(intent.getAction())
					&& intent.hasExtra(Intent.EXTRA_STREAM)) {

				// Grab the stream for the image, video, or
				// other shareable object...
				String type = intent.getType();
				Uri stream = (Uri) intent
						.getParcelableExtra(Intent.EXTRA_STREAM);
				if (stream != null && type != null) {
					Log.i("VShare", "Got a stream for an image...");

					// The content resolver allows us to grab the data
					// that the URI refers to. We can also use it to
					// read some metadata about the file.
					ContentResolver contentResolver = getContentResolver();

					// This call tries to guess what type of stream
					// we have been passed.
					String contentType = contentResolver.getType(stream);

					String name = null;
					int size = -1;
					// Now, we index into the metadata for the stream and
					// figure out what we are dealing with...size/name.
					Cursor metadataCursor = contentResolver.query(stream,
							new String[] { OpenableColumns.DISPLAY_NAME,
									OpenableColumns.SIZE }, null, null, null);
					if (metadataCursor != null) {
						try {
							if (metadataCursor.moveToFirst()) {
								name = metadataCursor.getString(0);
								size = metadataCursor.getInt(1);

							}
						} finally {
							metadataCursor.close();
						}
					}

					
					int port = Integer.parseInt(port_.getText().toString());
					// If for some reason we couldn't get a name,
					// we just use the last path segment as the name.
					if (name == null)
						name = stream.getLastPathSegment();

					// Now, we try to resolve the URI to an actual InputStream
					// that we can read.
					InputStream in = contentResolver.openInputStream(stream);

					// Finally, we pipe the stream to the network.
					sendData(server, port, in, name);

				} else {
					Log.i("VShare", "Stream was null");
				}
			} else {
				Log.i("VShare", "Invoked outside of send....");
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * You must modify this method to add the parameters for the user name and
	 * comment to the POST.
	 * 
	 * @param server
	 *            - the url of remote server
	 * @param in
	 *            - the input stream to read
	 */
	public void sendData(String server, int port, InputStream in, String name)
			throws IOException {

		// Since we are going to asynchronously send the data to
		// the server, we have to create a complete copy of the
		// input stream. Otherwise, the stream may be gone when
		// we try to use it.
		byte[] data = IOUtils.toByteArray(in);
		ByteArrayInputStream bin = new ByteArrayInputStream(data);

		
		UploadTask t = new UploadTask(server, port);
		t.execute(bin);
	}


	private class UploadTask extends AsyncTask<InputStream, Integer, Object> {

		private static final String VSHARE = "VShare";
		private String server_;
		private int serverPort_;

		public UploadTask(String server, int port) {
			super();
			server_ = server;
			serverPort_ = port;
		}

		@Override
		protected Object doInBackground(InputStream... params) {
			try {
				/**
				 * Step 2:
				 * 
				 * Use a socket to connect to the server. Obtain
				 * an OutputStream from the socket and write the
				 * data contained in params[0] to the server.
				 * Finally, release resources when you are
				 * done.
				 */
				Socket s = new Socket(server_, serverPort_);
				
				OutputStream os = s.getOutputStream();
				InputStream is = params[0];
				byte[] buff = new byte[1024];
				int read = 0;
				
				while((read = is.read(buff)) != -1)
				{
					os.write(buff, 0, read);
				}
				
				os.flush();
				is.close();
				os.close();
				
				
				
				Log.d(VSHARE, "File sent.");
				textToSpeech_.speak("File sent", TextToSpeech.QUEUE_FLUSH, null);
				
				return Boolean.TRUE;
			} catch (Exception e) {
				e.printStackTrace();
				return Boolean.FALSE;
			}
		}

	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		textToSpeech_.shutdown();
		
	}

	
}