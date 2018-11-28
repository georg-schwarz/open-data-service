# Open Data Service

The Open Data Service (ODS) is Java server application which can collect data from multiple sources simulataneously, process that data and then offer an improved (or "cleaned") version to its clients.

It is currently actively being used for gathering water level information about rivers and Germany and by the Android application [Pegel Alarm](http://pegelalarm.de) (available also on [GitHub](https://github.com/jvalue/hochwasser-app)).


## Modules

The ODS is divided into a number of gradle modules:

- `server`: the main server application, built using [Dropwizard](http://www.dropwizard.io/)
- `userservice`: the microservice responsible for user management, built using [Dropwizard](http://www.dropwizard.io/)
- `models`: domain models
- `client-retrofit`: a Java client implementation for the REST API of the ODS


## Setup

The ODS uses [Apache CouchDb](https://couchdb.apache.org/) as its underlying data storage. In oder to run the ODS you will need to have CouchDb setup.

To configure the ODS 
- copy `server/ods-configuration.yml.template` to `server/ods-configuration.yml` 
- copy `userservice/ods-configuration.yml.template` to `userservice/ods-configuration.yml` 
- and supply the missing values:

- `gcmApiKey`: the ODS allows clients to receive updates about new data via Google Cloud Messaging (GCM). To enable this feature you will need to enter your GCM Api key here
- `couchdb username` and `couchdb password`: admin credentials for CouchDb
- auth: the ODS supports two types of authentication mechanisms: basic (username + password) and via Google's OAuth
    - basic: to add users which use basic auth simply add the corresponding details to the `users` section under `auth`
    - OAuth: to setup OAuth you will need to create a new project in the [Google Developer Console](https://console.developers.google.com/) and one client id for a web application (that is the ODS) and additional client ids for each client that should communicate with the ODS. Then copy the created client ids to `googleOAuthWebClientId` and `googleOAuthClientIds` respectively.

Once done you should be able to run

- `./gradlew run` or `./gradlew server:run` (Windows users use the `.bat` version)  to run the `server` project
- `./gradlew userservice:run` to run the `userservice` project

which first downloads all required dependencies and then starts the ODS. The server should be be running at [http://localhost:8080/ods/api/v1](http://localhost:8080/ods/api/v1).

## Docker

An easy way to use the ODS is via a pre build Docker image.  Therefore use docker-compose to start the ODS Docker container and its dependent  services.

`docker-compose -f docker/docker-compose.yml up`

### Building a Docker Image
You can build a local Docker images from source files using the Gradle task `./gradlew dockerBuild`.
To run the local image with docker-compose call:

`docker-compose -f docker/docker-compose.yml -f docker/docker-compose.local.yml up`

With this command all required docker containers are started up. This includes
- the `ods` server and its `database`
- the `userservice` server and its `database`
- the message broker `rabbitmq` as async communcation between the micro-services
- the reverse-proxy `traefik` that bundles all endpoints to one common entry point


## Usage

The ODS uses a REST API for communicating with clients of all kinds, be it end users or admins. The basic usage of the api follows the template `/someObjects/{objectId}/someProperty` and is always relative to the base url you have defined in the `ods-configuration.yml` file.

The two 'concepts' that are most important in the context of the ODS are sources and filter chains. 

Sources are available at `/datasources` and represent a collection of data, meta data, filter chains and update listeners.

Filter chains are located 'below' sources at `/datasources/{sourceId}/filterChains` and represent processing chains which fetch, manipulate and finally store data on the ODS.

To get a feeling for how this API works and which parameters are supported, you can

- take a look at the implementation of the [REST api](https://github.com/jvalue/open-data-service/tree/master/server/src/main/java/org/jvalue/ods/rest)
- have a look at this [Postman collection](https://www.getpostman.com/collections/25d694d4ba21348c5530)
- or start working with the Java client implementation (see below)

In order for the Postman collection to work, Postman needs to know a couple of things (e.g. passwords, urls, etc.), which can be set by importing a so called _environment_ file that looks like this:

```json
{
	"id": "b9d8ff70-9540-e7c6-e423-d8cd74d9c6e3",
	"name": "localhost",
	"values": [
		{
			"key": "ods_base_url",
			"value": "http://localhost:8080/ods/api/v1",
			"type": "text",
			"name": "ods_base_url",
			"enabled": true
		},
		{
			"key": "ods_admin_username",
			"value": "admin@adminland.com",
			"type": "text",
			"name": "ods_admin_username",
			"enabled": true
		},
		{
			"key": "ods_admin_password",
			"value": "admin123",
			"type": "text",
			"name": "ods_admin_password",
			"enabled": true
		}
	],
	"timestamp": 1429550360212,
	"synced": false,
	"syncedFilename": ""
}
```
To import those variables, save the above JSON in a file and click the _environment_ button at the top of Postman.

## Client implementation

If you want to start working the ODS and don't mind Java, you can use the client lib that comes with the ODS. The library was created using [JaxRs2Retrofit](https://github.com/Maddoc42/JaxRs2Retrofit) and is available on [bintray](https://bintray.com/maddoc42/maven/ods/view). For gradle builds:

```groovy
repositories {
  jcenter()
}

dependencies {
  compile 'org.jvalue.ods:client-retrofit:0.1.3'
}
```

For an example usage please refer to the [test cases](https://github.com/jvalue/open-data-service/tree/master/client-retrofit/src/integrationtest/java/org/jvalue/ods/api) or take a look at [Pegel Alarm](https://github.com/jvalue/hochwasser-app).


## Test cases

The are two types of tests in this repo: unit tests and integration tests. The unit tests are available under `{module}/src/test` and can be run via `./gradlew test` and the integration tests can be found at `{module}/src/integrationtest` and can be run via `./gradlew integrationTest`. Please note that the integration tests assume that CouchDB and the ODS are running locally with default settings.


## Health checks

The ODS has a number of [health checks](https://dropwizard.github.io/dropwizard/getting-started.html#creating-a-health-check) which ensure that a running instance of the ODS conforms to the requirements of the Pegel Alarm application. To see the output of those health checks simply go to `/{adminContextPath}/healthcheck`. The default config for localhost is 
- `ods`: [http://localhost:8071/healthcheck](http://localhost:8071/healthcheck).
- `userservice`: [http://localhost:8091/healthcheck](http://localhost:8091/healthcheck).

## License
Copyright 2014-2018 Friedrich-Alexander Universität Erlangen-Nürnberg

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Affero General Public License as
published by the Free Software Foundation, either version 3 of the
License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Affero General Public License for more details.

You should have received a copy of the GNU Affero General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
