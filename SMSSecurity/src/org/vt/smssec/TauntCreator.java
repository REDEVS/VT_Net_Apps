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

import android.content.Context;
import android.util.Log;

public class TauntCreator implements CommandCreator {

	private static final String PREFIX = "taunt";

	@Override
	public Command createCommand(Context ctx, String cmdtxt) {
		
		Log.v(PREFIX, "Creating echo command : '"+cmdtxt+"'");
		
		int start = cmdtxt.indexOf("\"");
		if(start <= 0)
			return null;
		
		int end = cmdtxt.indexOf("\"",start+1);
		if(end <= 0)
			return null;
		
		if(start == end)
			return null;
		
		String msg = cmdtxt.substring(start+1,end);
		
		String durtxt = cmdtxt.substring(end).trim();
		int duration = 1000;
		try{
			duration = Integer.parseInt(durtxt);
		}catch (Exception e) {}
		
		return new TauntCommand(ctx, msg, duration);
	}

	@Override
	public String getCommandPrefix() {
		return PREFIX;
	}

}
