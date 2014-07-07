/*
    Open Data Service
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
package org.jvalue.ods.translator;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jvalue.ods.data.DataSource;
import org.jvalue.ods.data.generic.GenericEntity;
import org.jvalue.ods.data.objecttypes.ObjectType;
import org.jvalue.ods.logger.Logging;


abstract class HtmlTranslator extends Translator {

	@Override
	public GenericEntity translate(DataSource dataSource) {
		if (dataSource == null || dataSource.getUrl() == null)
			throw new IllegalArgumentException("source is null");

		try {
			return translateHelper(
					Jsoup.parse(new HttpReader(dataSource.getUrl()).read("UTF-8")),
					dataSource.getDataSourceSchema());

		} catch (IOException io) {
			String error = "Could not grab source.";
			Logging.error(this.getClass(), error);
			System.err.println(error);
			io.printStackTrace(System.err);
			return null;
		}
	}


	protected abstract GenericEntity translateHelper(Document doc, ObjectType objectType);

}
