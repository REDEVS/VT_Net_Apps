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

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class CommandService extends Service {

	// This is the object that receives interactions from clients.
	private final IBinder mBinder = new LocalBinder();

	/**
	 * Class for clients to access. Because we know this service always runs in
	 * the same process as its clients, we don't need to deal with IPC.
	 */
	public class LocalBinder extends Binder {
		CommandService getService() {
			return CommandService.this;
		}
	}

	@Override
	public void onCreate() {
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// Step 5. Extract the cmdtxt from the Intent
		// using the COMMAND_TEXT_EXTRA key.
		String cmdtxt = null;
		
		cmdtxt = intent.getStringExtra(Command.COMMAND_TEXT_EXTRA);
		
		Command c = CommandManager.get().createCommand(this, cmdtxt);
		if(c != null){
			c.execute();
		}
		else {
			showToast("Invalid command: '"+cmdtxt+"'");
		}
	}

	private void showToast(String msg) {
		Toast t = Toast.makeText(this, msg,
				1000);
		t.show();
	}

	@Override
	public void onDestroy() {
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

}
