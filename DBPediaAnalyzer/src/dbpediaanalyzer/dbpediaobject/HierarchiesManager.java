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
    private HashMap<String, Category> categories;
    private HashMap<String, OntologyClass> ontologyClasses;
    private HashMap<String, YagoClass> yagoClasses;

    public HierarchiesManager(HashMap<String, Category> categories, HashMap<String, OntologyClass> ontologyClasses,
                              HashMap<String, YagoClass> yagoClasses) {
        this.categories = categories;
        this.ontologyClasses = ontologyClasses;
        this.yagoClasses = yagoClasses;
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
