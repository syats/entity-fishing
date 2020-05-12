package com.scienceminer.nerd.kb.db;

import com.scienceminer.nerd.exceptions.NerdResourceException;
import com.scienceminer.nerd.kb.Statement;
import com.scienceminer.nerd.kb.UpperKnowledgeBase;
import org.apache.hadoop.record.CsvRecordInput;
import org.fusesource.lmdbjni.Entry;
import org.fusesource.lmdbjni.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A factory for creating the LMDB databases used in (N)ERD Knowlegde Base for extracting MeSH data (Wikidata ID, meshID, UmlsID).
 * 	Every Wikidata instance have unique meshID but can have 0-N of UMLS CUI
 * 	P486 (MeSH descriptor ID)
 * 	P2892 (UMLS CUI)
 */

public class MeshDatabase extends StringRecordDatabase<Map<String, List<String>>> {
	private static final Logger logger = LoggerFactory.getLogger(MeshDatabase.class);

	public MeshDatabase(KBEnvironment env) {
		super(env, DatabaseType.mesh);
	}

	@Override
	public KBEntry<String, Map<String, List<String>>> deserialiseCsvRecord(
			CsvRecordInput record) throws IOException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Load or build the MeSH data
	 */
	public void buildMeshDatabase(ConceptDatabase conceptDb, StatementDatabase statementDb,
			boolean overwrite) throws Exception {
		if (isLoaded && !overwrite)
			return;
		System.out.println("Loading " + name + " database");

		if (conceptDb == null)
			throw new NerdResourceException("conceptDb not found");

		// iterate through concepts
		KBIterator iter = new KBIterator(conceptDb);
		Transaction tx = environment.createWriteTransaction();
		try {
			int nbToAdd = 0;
			int n = 0; // total entities
			int nbMesh = 0; // total MeSH ID found
			while(iter.hasNext()) {
				if (nbToAdd > 10000) {
					tx.commit();
					tx.close();
					tx = environment.createWriteTransaction();
					nbToAdd = 0;
				}
				Entry entry = iter.next();
				byte[] keyData = entry.getKey();
				byte[] valueData = entry.getValue();
				try {
					String entityId = (String)KBEnvironment.deserialize(keyData);
					/* To make sure that only biomedical terminologies collected, we can filter the instances gathered from P486
					   by further filters, for example a property P31 (instanceOf) with value Q12136 (disease) */

					List<Statement> statements = statementDb.retrieve(entityId);
					{
						Map<String, List<String>> meshUmls = new HashMap<>();
						String meshId = "";
						List<String> umlsList = null;

						for(Statement statement : statements) {
							// check the statements for a property P486 (MeSH descriptor ID)
							if (statement.getPropertyId().equals("P486")) {
								meshId = statement.getValue();
							}

							// check the statements for a property P2892 (UMLS CUI)
							if (statement.getPropertyId().equals("P2892")) {
								String umls = statement.getValue();
								if (umlsList == null)
									umlsList = new ArrayList<String>();
								if (!umlsList.contains(umls))
									umlsList.add(umls);
							}
						}

						if (meshId.length() > 0) {
							// we have a MeSH ID with the UMLS CUI
							if (umlsList == null) {
								meshUmls.put(meshId, new ArrayList<>());
							} else{
								meshUmls.put(meshId, umlsList);
							}

							nbMesh++;

							// store the information to the database
							db.put(tx, KBEnvironment.serialize(entityId),
								KBEnvironment.serialize(meshUmls));
							nbToAdd++;

						}
					}
				} catch(Exception e) {
					logger.error("Fail to write entity description...", e);
				}
				n++;
			}
			logger.info("Total nb entities visited: " + n);
			logger.info("Total nb MeSH found: " + nbMesh);
		} catch(Exception e) {
			logger.error("Error when filling MeSH database...", e);
 		} finally {
			if (iter != null)
				iter.close();
			tx.commit();
			tx.close();
			isLoaded = true;
		}
	}

	public static void main(String[] args) {
		// for building MeSH database if it doesn't exist yet (data/db/db-kb/mesh)
		UpperKnowledgeBase upperKnowledgeBase = UpperKnowledgeBase.getInstance();
		upperKnowledgeBase.close();
	}
}
