package dbpediaanalyzer.dbpediaobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a class of the ontology DBpedia ontology
 *
 * @author Pierre Monnin
 *
 */
public class OntologyClass extends HierarchyElement {
    public static final String DBPEDIA_ONTOLOGY_CLASS_URI_PREFIX = "http://dbpedia.org/ontology";

    public OntologyClass(String uri) {
        super(uri);
    }

    @Override
    public void addParent(HierarchyElement parent) {
        if(parent instanceof OntologyClass) {
            super.addParent(parent);
        }
    }

    @Override
    public void addChild(HierarchyElement child) {
        if(child instanceof OntologyClass) {
            super.addChild(child);
        }
    }

    public static List<OntologyClass> getAccessibleUpwardOntologyClasses(Collection<OntologyClass> fromSubset) {
        Collection<HierarchyElement> accessible = HierarchyElement.getAccessibleUpwardElements(fromSubset);
        ArrayList<OntologyClass> retVal = new ArrayList<>();

        for(HierarchyElement he : accessible) {
            retVal.add((OntologyClass) he);
        }

        return retVal;
    }
}
