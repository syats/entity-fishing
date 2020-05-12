package com.scienceminer.nerd.kb;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class KBUtilitiesTest {
	
	//private LowerKnowledgeBase wikipedia = null;

	@Before
	public void setUp() {
		try {
        	UpperKnowledgeBase.getInstance(); 
        	//wikipedia = UpperKnowledgeBase.getInstance().getWikipediaConf("en");
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
   	
	@Test
	public void testImmediateTaxonParents() {
		try {
			List<String> parents = UpperKnowledgeBase.getInstance().getParentTaxons("Q7377");
System.out.println(parents);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testFullTaxonParents() {
		try {
			List<String> parents = UpperKnowledgeBase.getInstance().getFullParentTaxons("Q18498");
System.out.println("Q18498: " + parents);

			parents = UpperKnowledgeBase.getInstance().getFullParentTaxons("Q3200306");
System.out.println("Q3200306: " + parents);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testKingdomMethods() {
		try {
			boolean isAnimal = KBUtilities.isAnimal("Q18498");
			System.out.println("Q18498.isAnimal = " + isAnimal);

			isAnimal = KBUtilities.isAnimal("Q3200306");
			System.out.println("Q3200306.isAnimal = " + isAnimal);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	@Ignore ("For testing if the MeSH database is built")
	public void testMesh() {
		try {
			// Syndrome de Brown-Séquard (Q991037)
			Map<String, List<String>> meshUmls = UpperKnowledgeBase.getInstance().getMeshUmls("Q991037");
			for( Map.Entry<String, List<String>> entry : meshUmls.entrySet() ) {
				String meshId = entry.getKey();
				System.out.println("MeSH ID : " + meshId);

				for (String umls : entry.getValue()) {
					System.out.println("UMLS CUI : " + umls);
				}
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*@After
	public void testClose() {
		try {
			wikipedia.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}*/
}