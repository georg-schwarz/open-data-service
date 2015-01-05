package org.jvalue.ods.rest;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import org.jvalue.ods.notifications.clients.Client;
import org.jvalue.ods.processor.plugin.PluginMetaData;
import org.jvalue.ods.processor.reference.ProcessorChainReference;

import java.util.HashMap;
import java.util.Map;


public class JsonMixins {

	@JsonIgnoreProperties({"_id", "_rev"})
	private static interface ProcessorChainReferenceMixin {
		@JsonProperty("id") String getProcessorChainId();
	}

	@JsonIgnoreProperties({"_id", "_rev"})
	private static interface NotificationClientMixin{
		@JsonProperty("id") String getClientId();
	}

	@JsonIgnoreProperties({"_id", "_rev", "_attachments"})
	private static interface PluginMixin {
		@JsonProperty("id") String getPluginId();
	}


	private final Map<Class<?>, Class<?>> mixins = new HashMap<>();

	public JsonMixins() {
		mixins.put(ProcessorChainReference.class, ProcessorChainReferenceMixin.class);
		mixins.put(Client.class, NotificationClientMixin.class);
		mixins.put(PluginMetaData.class, PluginMixin.class);
	}


	public Map<Class<?>, Class<?>> getMixins() {
		return mixins;
	}

}
