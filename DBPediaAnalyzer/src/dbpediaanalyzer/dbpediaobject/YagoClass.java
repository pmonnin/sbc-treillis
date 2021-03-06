package dbpediaanalyzer.dbpediaobject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a class of the ontology YAGO
 *
 * @author Pierre Monnin
 *
 */
public class YagoClass extends HierarchyElement {
    public static final String DBPEDIA_YAGO_CLASS_URI_PREFIX = "http://dbpedia.org/class/yago";

    public YagoClass(String uri) {
        super(uri);
    }

    @Override
    public void addParent(HierarchyElement parent) {
        if(parent instanceof YagoClass) {
            super.addParent(parent);
        }
    }

    @Override
    public void addChild(HierarchyElement child) {
        if(child instanceof YagoClass) {
            super.addChild(child);
        }
    }

    public static List<YagoClass> getAccessibleUpwardYagoClasses(Collection<YagoClass> fromSubset) {
        Collection<HierarchyElement> accessible = HierarchyElement.getAccessibleUpwardElements(fromSubset);
        ArrayList<YagoClass> retVal = new ArrayList<>();

        for(HierarchyElement he : accessible) {
            retVal.add((YagoClass) he);
        }

        return retVal;
    }
}
