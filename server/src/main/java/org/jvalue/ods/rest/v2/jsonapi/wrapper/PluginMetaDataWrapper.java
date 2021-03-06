/*
 * Copyright (c) 2019 Friedrich-Alexander University Erlangen-Nuernberg (FAU)
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package org.jvalue.ods.rest.v2.jsonapi.wrapper;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import io.swagger.v3.oas.annotations.media.Schema;
import org.jvalue.ods.api.processors.PluginMetaData;

import java.util.Collection;
import java.util.stream.Collectors;

@Schema(name = "pluginData")
public class PluginMetaDataWrapper implements JsonApiIdentifiable{

	private final PluginMetaData pluginMetaData;

	private PluginMetaDataWrapper(String id, String author) {
		this.pluginMetaData = new PluginMetaData(id, author);
	}


	@Schema(name = "attributes", required = true)
	@JsonUnwrapped
	@JsonIgnoreProperties({"id", "type"})
	public PluginMetaData getPluginMetaData() {
		return pluginMetaData;
	}


	@Override
	@Schema(example = "myPlugin", required = true)
	public String getId() {
		return pluginMetaData.getId();
	}


	@Schema(allowableValues = "PluginMetaData", required = true)
	@Override
	public String getType() {
		return PluginMetaData.class.getSimpleName();
	}


	public static PluginMetaDataWrapper from(PluginMetaData pluginMetaData) {
		return new PluginMetaDataWrapper(pluginMetaData.getId(), pluginMetaData.getAuthor());
	}


	public static Collection<PluginMetaDataWrapper> fromCollection(Collection<PluginMetaData> pluginMetaData) {
		return pluginMetaData.stream()
			.map(PluginMetaDataWrapper::from)
			.collect(Collectors.toList());
	}
}
