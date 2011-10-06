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

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

	private static final String ENABLE_SMS_SEC = "EnableSMSSec";
	private static final String CMD = "cmd:";
	private static final String MESSAGES = "pdus";

	@Override
	public void onReceive(Context context, Intent intent) {

		// Step 1. Modify the AndroidManifest.xml so that
		// the SMSReceiver has an Intent Filter that will
		// match any intents with action=android.provider.Telephony.SMS_RECEIVED.
		// You should set the priority of your Intent Filter
		// to 100.
		
		
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(context);

		boolean enabled = prefs.getBoolean(ENABLE_SMS_SEC, true);

		if(!enabled)
			return;
		
		// make sure the sms app doesn't get the message
		// and create a notification that the thief might
		// see
		this.abortBroadcast();

		// get the data associated with the intent
		Bundle bundle = intent.getExtras();

		// extract the list of sms messages from the data
		Object messages[] = (Object[]) bundle.get(MESSAGES);
		List<String> cmds = new ArrayList<String>();

		// iterate through the sms messages and look for any
		// commands that need to be executed
		for (int i = 0; i < messages.length; i++) {
			SmsMessage msg = SmsMessage.createFromPdu((byte[]) messages[i]);

			// do some checking to make sure that the sender has
			// permission to execute commands
			if (isPrivelegedNumber(msg.getOriginatingAddress())) {
				String body = msg.getMessageBody();

				// finally extract and add the command from the sms
				// body to the list of commands
				if (isCommand(body)) {
					String cmd = getCommand(body);
					cmds.add(cmd);
				}
			}

		}

		// if we didn't find any commands, we need
		// to cancel the abort so that the sms app
		// receives the message and the user (thief)
		// doesn't know that we are silently monitoring
		// sms messages
		if (cmds.size() == 0) {
			this.clearAbortBroadcast();
		} else {
			// ok, we got some commands that we need
			// to execute!

			CommandManager cntr = CommandManager.get();
			for (String cmdtxt : cmds) {
				sendCommandIntent(context, cmdtxt);
			}
		}

	}

	// we run the commands in a separate service to ensure
	// that they all get to execute and that they all run
	// in the background
	private void sendCommandIntent(Context context, String cmdtxt) {
		
		// Step 2. You need to create an intent to start the 
		// CommandService. The Intent should be constructed using
		// the constructor that takes a Context and a Class.
		Intent i = new Intent(context, CommandService.class);
		
		// Step 3. Add data to the Intent that includes the
		// cmdtext. The data should be stored under the
		// key Command.COMMAND_TEXT_EXTRA.
		
		i.putExtra(Command.COMMAND_TEXT_EXTRA, cmdtxt);
		
		
		// Step 4. Tell the Context to start the service
		// using the Intent that you created.
		context.startService(i);
		
	}

	private boolean isCommand(String msg) {
		return ("" + msg).startsWith(CMD);
	}

	private String getCommand(String msg) {
		return ("" + msg).substring(CMD.length());
	}

	private boolean isPrivelegedNumber(String num) {
		
		// 5. Modify this method to read from a 
		// setting in the shared preferences that
		// has a list of priveleged numbers
		return true;
	}
}