package dbpediaanalyzer.dbpediaobject;

import dbpediaanalyzer.statistic.HierarchyStatistics;

import java.util.*;

/**
 * TODO JAVADOC
 *
 * @author Pierre Monnin
 *
 */
public class HierarchiesManager {
    private Map<String, Category> categories;
    private Map<String, OntologyClass> ontologyClasses;
    private Map<String, YagoClass> yagoClasses;

    public HierarchiesManager(Map<String, Category> categories, Map<String, OntologyClass> ontologyClasses,
                              Map<String, YagoClass> yagoClasses) {

        this.categories = new HashMap<>(categories);
        this.ontologyClasses = new HashMap<>(ontologyClasses);
        this.yagoClasses = new HashMap<>(yagoClasses);

    }

    public Category getCategoryFromUri(String uri) {
        return this.categories.get(uri);
    }

    public OntologyClass getOntologyClassFromUri(String uri) {
        return this.ontologyClasses.get(uri);
    }

    public YagoClass getYagoClassFromUri(String uri) {
        return this.yagoClasses.get(uri);
    }

    public HierarchyStatistics getCategoriesStatistics() {
        return new HierarchyStatistics(this.categories);
    }

    public HierarchyStatistics getOntologyClassesStatistics() {
        return new HierarchyStatistics(this.ontologyClasses);
    }

    public HierarchyStatistics getYagoClassesStatistics() {
        return new HierarchyStatistics(this.yagoClasses);
    }

}
