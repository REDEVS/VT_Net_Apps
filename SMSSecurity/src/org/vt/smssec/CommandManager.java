/*************************************************************************
* Copyright 2010 Jules White
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at http://www.apache.org/licenses/
* LICENSE-2.0 Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an "AS IS"
* BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
* implied. See the License for the specific language governing permissions
* and limitations under the License.
**************************************************************************/

package org.vt.smssec;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.Log;

public class CommandManager {

	private static final String TAG = "CommandCreator";
	private static CommandManager instance_ = new CommandManager();
	
	public static CommandManager get(){
		return instance_;
	}
	
	private Map<String, CommandCreator> creators_ = new HashMap<String, CommandCreator>();
	
	
	public CommandManager(){
		installDefaultCreators();
	}
	
	// Step 6. Create a new command that does anything
	//  you want. You will need to create a class that
	// implements CommandCreator and install it below.
	// You will also need to create a class that implements
	// Command to execute your logic. The CommandCreator
	// should construct instances of your command.
	public void installDefaultCreators(){
		installCreator(new PhotoCreator());
		installCreator(new TalkCreator());
		installCreator(new TauntCreator());
	}
	
	public void installCreator(CommandCreator cc){
		creators_.put(cc.getCommandPrefix(), cc);
	}
	
	public Command createCommand(Context context, String cmdtxt){
		
		Log.v(TAG, "Creating command: '"+cmdtxt+"'");
		
		int start = cmdtxt.indexOf(":");
		if(start <= 0){
			start = cmdtxt.length();
		}
		
		String prefix = cmdtxt.substring(0,start).trim();
		CommandCreator cc = creators_.get(prefix);
		
		Command cmd = null;
		if(cc != null){
			cmd = cc.createCommand(context, cmdtxt);
		}
		
		return cmd;
	}
}
