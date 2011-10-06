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

public interface CommandCreator {

	/**
	 * Returns the prefix code that is associated
	 * with this command creator. This prefix code
	 * is used to lookup the appropriate command
	 * creator to build a Command object for each
	 * incoming text message that includes command
	 * parameters.
	 * 
	 * @return the command prefix (e.g. "MyCmd")
	 */
	public String getCommandPrefix();
	
	/**
	 * Creates a command given a string representing the
	 * command text.
	 * 
	 * @param ctx - the context for the command
	 * @param cmdtxt - the text describing the command
	 * @return
	 */
	public Command createCommand(Context ctx, String cmdtxt);
}
