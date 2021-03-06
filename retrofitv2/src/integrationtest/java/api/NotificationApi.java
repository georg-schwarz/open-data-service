/*
 * Copyright (c) 2019 Friedrich-Alexander University Erlangen-Nuernberg (FAU)
 *
 * SPDX-License-Identifier: AGPL-3.0-only
 */
package api;

import jsonapi.ResponseBody;
import org.jvalue.ods.rest.v2.jsonapi.request.JsonApiRequest;
import org.jvalue.ods.rest.v2.jsonapi.response.JsonApiResponse;
import retrofit.client.Response;
import retrofit.http.*;

public interface NotificationApi {
	String BASE_URL = "/v2/datasources/{sourceId}/notifications";

	@GET(BASE_URL + "/{clientId}")
	@Headers("Content-type: " + JsonApiResponse.JSONAPI_TYPE)
	ResponseBody getClient(@Path("sourceId") String sourceId, @Path("clientId") String clientId);

	@GET(BASE_URL)
	@Headers("Content-type: " + JsonApiResponse.JSONAPI_TYPE)
	ResponseBody getAllClients(@Path("sourceId") String sourceId);

	@POST(BASE_URL)
	@Headers("Content-type: " + JsonApiResponse.JSONAPI_TYPE)
	ResponseBody registerClient(@Path("sourceId") String sourceId, @Body JsonApiRequest clientDescription);

	@DELETE(BASE_URL + "/{clientId}")
	@Headers("Content-type: " + JsonApiResponse.JSONAPI_TYPE)
	Response unregisterClient(@Path("sourceId") String sourceId, @Path("clientId") String clientId);
}
