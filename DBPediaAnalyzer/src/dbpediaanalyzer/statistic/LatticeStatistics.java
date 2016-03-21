package dbpediaanalyzer.statistic;

import dbpediaanalyzer.lattice.Concept;
import dbpediaanalyzer.lattice.Lattice;

/**
 * TODO JAVADOC
 *
 * @author Pierre Monnin
 *
 */
public class LatticeStatistics {
    private int depth;
    private int conceptsNumber;
    private int edgesNumber;
    private int conceptsWithoutCategoriesNumber;
    private int conceptsWithoutOntologyClassesNumber;
    private int conceptsWithoutYagoClassesNumber;
    private double averageCategoriesNumberPerConcept;
    private double averageOntologyClassesNumberPerConcept;
    private double averageYagoClassesNumberPerConcept;
    private double averagePageNumberPerConcept;
    private double averageRelationshipsNumberPerConcept;

    public LatticeStatistics(Lattice lattice) {
        this.conceptsNumber = lattice.getConcepts().size();

        for(Concept concept : lattice.getConcepts()) {
            this.edgesNumber += concept.getParents().size();

            if(concept.getCategories().size() == 0) {
                this.conceptsWithoutCategoriesNumber++;
            }

            if(concept.getOntologyClasses().size() == 0) {
                this.conceptsWithoutOntologyClassesNumber++;
            }

            if(concept.getYagoClasses().size() == 0) {
                this.conceptsWithoutYagoClassesNumber++;
            }
        }
    }

    public int getDepth() {
        return this.depth;
    }

    public int getConceptsNumber() {
        return this.conceptsNumber;
    }

    public int getEdgesNumber() {
        return this.edgesNumber;
    }

    public int getConceptsWithoutCategoriesNumber() {
        return this.conceptsWithoutCategoriesNumber;
    }

    public int getConceptsWithoutOntologyClassesNumber() {
        return this.conceptsWithoutOntologyClassesNumber;
    }

    public int getConceptsWithoutYagoClassesNumber() {
        return this.conceptsWithoutYagoClassesNumber;
    }

    public double getAverageCategoriesNumberPerConcept() {
        return this.averageCategoriesNumberPerConcept;
    }

    public double getAverageOntologyClassesNumberPerConcept() {
        return this.averageOntologyClassesNumberPerConcept;
    }

    public double getAverageYagoClassesNumberPerConcept() {
        return this.averageYagoClassesNumberPerConcept;
    }

    public double getAveragePageNumberPerConcept() {
        return this.averagePageNumberPerConcept;
    }

    public double getAverageRelationshipsNumberPerConcept() {
        return this.averageRelationshipsNumberPerConcept;
    }
}