package org.jvalue.ods.db;


import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.ektorp.CouchDbConnector;
import org.ektorp.CouchDbInstance;
import org.ektorp.UpdateConflictException;
import org.ektorp.support.CouchDbRepositorySupport;
import org.ektorp.support.DesignDocument;
import org.ektorp.support.DesignDocumentFactory;
import org.ektorp.support.StdDesignDocumentFactory;
import org.ektorp.support.View;
import org.jvalue.ods.utils.Assert;
import org.jvalue.ods.utils.JsonPropertyKey;
import org.jvalue.ods.utils.Log;

import java.util.List;

@View( name = "all", map = "function(doc) { emit(null, doc) }")
public final class SourceDataRepository extends CouchDbRepositorySupport<JsonNode> {

	private static final String DESIGN_DOCUMENT_ID = "_design/" + JsonNode.class.getSimpleName();
	private static final DesignDocumentFactory designFactory = new StdDesignDocumentFactory();

	private final CouchDbConnector connector;
	private final OdsView domainIdView;

	@Inject
	SourceDataRepository(CouchDbInstance couchDbInstance, @Assisted String databaseName, @Assisted JsonPropertyKey domainIdKey) {
		super(JsonNode.class, couchDbInstance.createConnector(databaseName, true));
		this.connector = couchDbInstance.createConnector(databaseName, true);
		initStandardDesignDocument();

		String domainIdViewName = "findByDomainId";
		StringBuilder mapBuilder = new StringBuilder();
		mapBuilder.append("function(doc, id) { if (doc");
		for (JsonPropertyKey.Entry entry : domainIdKey.getEntries()) {
			if (entry.isString()) {
				mapBuilder.append(".");
				mapBuilder.append(entry.getString());
			} else {
				mapBuilder.append("[");
				mapBuilder.append(entry.getInt());
				mapBuilder.append("]");
			}
		}
		mapBuilder.append(" == id) emit(null, doc) }");
		this.domainIdView = new OdsView(domainIdViewName, mapBuilder.toString());
		if (!containsView(domainIdView)) addView(domainIdView);
	}


	public JsonNode findByDomainId(String domainId) {
		List<JsonNode> resultList = executeQuery(domainIdView, domainId);
		if (resultList.isEmpty()) return null;
		else if (resultList.size() == 1) return resultList.get(0);
		else throw new IllegalStateException("found more than one element for given domain id");
	}


	public List<JsonNode> executeQuery(OdsView view, String param) {
		return queryView(view.getViewName(), param);
	}


	public List<JsonNode> executeQuery(OdsView view) {
		return queryView(view.getViewName());
	}


	public void addView(OdsView odsView) {
		Assert.assertNotNull(odsView);

		DesignDocument designDocument;
		boolean update = false;

		if (connector.contains(DESIGN_DOCUMENT_ID)) {
			designDocument = connector.get(DesignDocument.class, DESIGN_DOCUMENT_ID);
			update = true;
		} else {
			designDocument = designFactory.newDesignDocumentInstance();
			designDocument.setId(DESIGN_DOCUMENT_ID);
		}

		DesignDocument.View view = new DesignDocument.View(odsView.getMapFunction());
		designDocument.addView(odsView.getViewName(), view);

		try {
			if (update) connector.update(designDocument);
			else connector.create(designDocument);
		} catch (UpdateConflictException uce) {
			Log.error("failed to add view", uce);
		}
	}


	public boolean containsView(OdsView odsView) {
		Assert.assertNotNull(odsView);

		if (!connector.contains(DESIGN_DOCUMENT_ID)) return false;
		DesignDocument designDocument = connector.get(DesignDocument.class, DESIGN_DOCUMENT_ID);
		return designDocument.containsView(odsView.getViewName());
	}

}