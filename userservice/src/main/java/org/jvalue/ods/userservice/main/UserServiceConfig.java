package org.jvalue.ods.userservice.main;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import org.jvalue.commons.couchdb.CouchDbConfig;
import org.jvalue.ods.userservice.auth.AuthConfig;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;


public final class UserServiceConfig extends Configuration {

	@NotNull @Valid private final CouchDbConfig couchDb;
	@NotNull @Valid private final AuthConfig auth;

	@JsonCreator
	public UserServiceConfig(
		@JsonProperty("couchDb") CouchDbConfig couchDb,
		@JsonProperty("auth") AuthConfig auth) {

		this.couchDb = couchDb;
		this.auth = auth;
	}

	public CouchDbConfig getCouchDb() {
		return couchDb;
	}

	public AuthConfig getAuth() {
		return auth;
	}
}