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

/**
 * A command is an automated action that can
 * be carried out on the phone. 
 *
 */
public interface Command {
	
	public static final String COMMAND_TEXT_EXTRA = "cmdtext";
	
	/**
	 * This method should implement the 
	 * action that the command performs.
	 */
	public void execute();
}
