package org.eece261.vsharetcpip;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;

public class VShareServer {

	private int port_;
	private File dataDir_;
	private boolean running_ = true;
	
	public VShareServer(int port, File datadir){
		port_ = port;
		dataDir_ = datadir;
	}
	
	public void run() throws Exception{
		
		System.out.println("Starting server");
		
		ServerSocket ssock = new ServerSocket(port_);
		
		System.out.println("Server listening");
		
		while(running_){
			/**
			 * Step 1:
			 * 
			 * The ServerSocket has been created for you
			 * above. Use the socket to accept connections.
			 * After receiving a connection, you should
			 * create a file to save any images received from
			 * the connection. Next, obtain an InputStream
			 * from the socket and write any data read
			 * from it to the file (see exampleOfWritingAFile()).
			 * 
			 * Advanced Version of Step 1:
			 * 
			 * Do the same thing you did in Step 1 but spawn
			 * a thread to process each connection.
			 * 
			 */
			//ssock.accept();
			Socket somedatatowrite = ssock.accept();
			
			InputStream is = somedatatowrite.getInputStream();
			
			//This will create a new file in the dataDir_
			File f = new File(dataDir_,Integer.toString(new Random().nextInt()) + "_File.jpg");
			
			FileOutputStream fout = new FileOutputStream(f);
			
			byte[] buff = new byte[1024];
			int read = 0;
			
			while((read = is.read(buff)) != -1){
				fout.write(buff,0,read);
			}
			
			fout.flush();
			fout.close();
			is.close();
			
			
			System.out.println("Received a file");
		}
	}
	
	public void exampleOfReadingDataFromAStream(InputStream in) throws IOException{
		
		byte[] buff = new byte[1024];
		int read = 0;
		while((read = in.read(buff)) != -1){
			//in here, read will contain the number
			//of bytes read and the buff will be
			//filled with the binary data
			
			//do something with buff and read
		}
		
		//the while loop exits when we are done reading
		//from the stream
	}
	
	public void exampleOfWritingAFile() throws IOException{
		//You don't really want this stream to
		//be null. In a real example, you would
		//wan to get a stream from somewhere
		//(e.g. a socket) and then write its
		//contents to the file.
		InputStream somedatatowrite = null;
		
		//This will create a new file in the dataDir_
		File f = new File(dataDir_,"some_file_name");
		
		FileOutputStream fout = new FileOutputStream(f);
		
		byte[] buff = new byte[1024];
		int read = 0;
		
		while((read = somedatatowrite.read(buff)) != -1){
			fout.write(buff,0,read);
		}
		
		fout.flush();
		fout.close();
	}
	
	
	public static void main(String[] args){
		VShareServer server = new VShareServer(9000,new File("./"));
		
		try{
			server.run();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
