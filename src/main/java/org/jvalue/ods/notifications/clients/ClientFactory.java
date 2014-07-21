/*  Open Data Service
    Copyright (C) 2013  Tsysin Konstantin, Reischl Patrick

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
    
 */
package org.jvalue.ods.notifications.clients;

import java.util.UUID;

import org.jvalue.ods.utils.Assert;



public final class ClientFactory {

	private ClientFactory() { }


	public static GcmClient newGcmClient(
			String source, 
			String gcmClientId) {

		Assert.assertNotNull(source, gcmClientId);
		return new GcmClient(getClientId(), source, gcmClientId);
	}


	public static HttpClient newHttpClient(
			String source, 
			String restUrl,
			String sourceParam,
			boolean sendData) {

		Assert.assertNotNull(source, restUrl, sourceParam, sendData);
		return new HttpClient(getClientId(), source, restUrl, sourceParam, sendData);
	}


	private static String getClientId() {
		return UUID.randomUUID().toString();
	}

}