package pedialattice;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;

import org.json.simple.parser.ParseException;

import serverlink.ChildAndParent;
import serverlink.JSONReader;
import colibri.lib.Concept;
import colibri.lib.Edge;
import colibri.lib.HybridLattice;
import colibri.lib.Relation;
import colibri.lib.Traversal;
import colibri.lib.TreeRelation;
import dbpediaobjects.DBPage;

/**
 * Lattice's construction
 * 
 * @author Damien Flament
 * @author Soline Blanc
 * @author Pierre Monnin
 *
 */
public class PediaLatticeFactory {

    private HashMap<String, DBPage> dbPages;
    private ArrayList<PediaConcept> dbLattice;
    private PediaConcept top;
    private PediaConcept bottom;

    public PediaLatticeFactory() throws ParseException, IOException {
        this.dbPages = new HashMap<>();
        this.dbLattice = null;
        this.top = null;
        this.bottom = null;
        this.createLattice();
    }

    /**
     * Lattice creation
     */
    public void createLattice() {
    	// Lattice objects from colibri
    	Relation rel = new TreeRelation();
    	
    	// Ask for pages related to dbo:Person
    	System.out.println("Crawling pages related to dbo:Person hierarchy...");
        List<ChildAndParent> pages = null;
        		
        try {
        	pages = JSONReader.getChildrenAndParents(URLEncoder.encode(
                "PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                + "PREFIX rdfs:<http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX owl:<http://www.w3.org/2002/07/owl#> "
                + "PREFIX skos:<http://www.w3.org/2004/02/skos/core#> "
                + "PREFIX dbo:<http://dbpedia.org/ontology/>  "
                + "select distinct ?child where {"
                + "?child dbo:wikiPageID ?pageId ."
                + "?child rdf:type/rdfs:subClassOf* dbo:Person ."
                + "?child dbo:deathDate ?deathDate."
                + "} LIMIT 500", "UTF-8"));
        }
        
        catch(IOException e) {
        	System.err.println("Error while trying to get the subset pages");
        }
        
        // For each page we get
        System.out.println("Getting information for each page... ");
        int rate = -1;
        int i = 0;
        while(i < pages.size()) {
        	if((int) ((double) i / (double) pages.size() * 100 % 100) > rate) {
        		rate = (int) ((double) i / (double) pages.size() * 100 % 100);
        		System.out.print(rate + " % ... ");
        	}
        	
        	try {
	        	DBPage page = new DBPage(pages.get(i).getChild().getValue());
	        	
	        	// Relationships + addition to lattice relation
	        	List<ChildAndParent> relationships = JSONReader.getChildrenAndParents(URLEncoder.encode(
	        			"select distinct ?child where {"
	        			+ "<" + page.getURI() + "> ?child ?other . "
	        			+ "}", "UTF-8"));
	        	
	        	for(ChildAndParent r : relationships) {
	        		page.addRelationship(r.getChild().getValue());
	        		rel.add(page.getURI(), r.getChild().getValue());
	        	}
	        	
	        	// Categories
	        	List<ChildAndParent> categories = JSONReader.getChildrenAndParents(URLEncoder.encode(
	        			"PREFIX dcterms:<http://purl.org/dc/terms/> "
	        			+ "select distinct ?child where {"
	        			+ "<" + page.getURI() + "> dcterms:subject ?child . "
	        			+ "}", "UTF-8"));
	        	
	        	for(ChildAndParent c : categories) {
	        		page.addCategory(c.getChild().getValue());
	        	}
	        	
	        	// Ontologies
	        	List<ChildAndParent> ontologies = JSONReader.getChildrenAndParents(URLEncoder.encode(
	        			"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	        			+ "select distinct ?child where {"
	        			+ "<" + page.getURI() + "> rdf:type ?child . "
    					+ "FILTER(REGEX(STR(?child), \"http://dbpedia.org/ontology\", \"i\")) . "
	        			+ "}", "UTF-8"));
	        	
	        	for(ChildAndParent o : ontologies) {
	        		page.addOntology(o.getChild().getValue());
	        	}
	        	
	        	// Yago classes
	        	List<ChildAndParent> yagoClasses = JSONReader.getChildrenAndParents(URLEncoder.encode(
	        			"PREFIX rdf:<http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
	        			+ "select distinct ?child where {"
	        			+ "<" + page.getURI() + "> rdf:type ?child . "
    					+ "FILTER(REGEX(STR(?child), \"http://dbpedia.org/class/yago\", \"i\")) . "
	        			+ "}", "UTF-8"));
	        	
	        	for(ChildAndParent y : yagoClasses) {
	        		page.addYagoClass(y.getChild().getValue());
	        	}
	        	
	        	i++;
	        	this.dbPages.put(page.getURI(), page);
        	}
        	
        	catch(IOException e) {
        		System.err.println("Error while trying to get info on: " + pages.get(i).getChild().getValue() + ". New try...");
        	}
        }

        System.out.print("\n");

        // Lattice construction
        System.out.println("Constructing lattice... Colibri Hybrid lattice... ");
        HybridLattice lattice = new HybridLattice(rel);
        int edgesNumber = 0;
        
        	// Edges number computation
        Iterator<Edge> it = lattice.edgeIterator(Traversal.BOTTOM_ATTRSIZE);
        while(it.hasNext()) {
           edgesNumber++;
           it.next();
        }
       
       		// Working over edges
        System.out.println("Constructing lattice... PediaLattice... ");
        it = lattice.edgeIterator(Traversal.BOTTOM_ATTRSIZE);
        rate = -1;
        i = 0;
        HashMap<Concept, PediaConcept> processed = new HashMap<>();
        this.dbLattice = new ArrayList<>();

        while(it.hasNext()) {
            if((int) ((double) i / (double) edgesNumber * 100 % 100) > rate) {
                rate = (int) ((double) i / (double) edgesNumber * 100 % 100);
                System.out.print(rate + " % ... ");
            }

            Edge currentEdge = it.next();
            i++;

            // Create upper / lower concepts relation
            Concept upperConcept = currentEdge.getUpper();
            PediaConcept upperPediaConcept = processed.get(upperConcept);

            if(upperPediaConcept == null) {
                upperPediaConcept = new PediaConcept(upperConcept, this.dbPages);
                processed.put(upperConcept, upperPediaConcept);
                this.dbLattice.add(upperPediaConcept);
            }

            Concept lowerConcept = currentEdge.getLower();
            PediaConcept lowerPediaConcept = processed.get(lowerConcept);

            if(lowerPediaConcept == null) {
                lowerPediaConcept = new PediaConcept(lowerConcept, this.dbPages);
                processed.put(lowerConcept, lowerPediaConcept);
                this.dbLattice.add(lowerPediaConcept);
            }

            lowerPediaConcept.addParent(upperPediaConcept);
            upperPediaConcept.addChild(lowerPediaConcept);
        }

        System.out.print("\n");

        // Looking for top and bottom concepts
        System.out.println("Top and bottom detection...");
        for(PediaConcept p : this.dbLattice) {
            if(p.getParents().size() == 0) {
                this.top = p;
            }

            if(p.getChildren().size() == 0) {
                this.bottom = p;
            }
        }

        // Multiple categories / ontologies / yago classes cleaning uphill
        System.out.println("Cleaning concepts categories, ontologies, yago classes by lattice uphill traversal...");
        Queue<PediaConcept> queue = new LinkedList<>();
        queue.add(this.bottom);
        i = 0;
        rate = -1;

        while(!queue.isEmpty()) {
            PediaConcept concept = queue.poll();
            i++;

            if((int) ((double) i / (double) this.dbLattice.size() * 100 % 100) > rate) {
                rate = (int) ((double) i / (double) this.dbLattice.size() * 100 % 100);
                System.out.print(rate + " % ... ");
            }

            for(PediaConcept parent : concept.getParents()) {
                if(!queue.contains(parent)) {
                    queue.add(parent);
                }

                for(String category : parent.getCategories()) {
                    concept.getCategories().remove(category);
                }

                for(String ontology : parent.getOntologies()) {
                    concept.getOntologies().remove(ontology);
                }

                for(String yagoClass : parent.getYagoClasses()) {
                    concept.getYagoClasses().remove(yagoClass);
                }
            }
        }

        System.out.print("\n");
        
        // Statistics
        System.out.println("=== LATTICE STATISTICS ===");
        System.out.println("Selected pages number: " + pages.size());
        System.out.println("Lattice edges number: " + edgesNumber);
        System.out.println("Lattice concepts number: " + this.dbLattice.size());
        System.out.println("==========================");
    }

    public ArrayList<PediaConcept> getDBLattice() {
    	return this.dbLattice;
    }
}