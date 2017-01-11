package io.github.pmonnin.kcomparison;

import io.github.pmonnin.io.ServerQuerier;
import io.github.pmonnin.io.SparqlResponse;
import io.github.pmonnin.fca.FormalConcept;
import io.github.pmonnin.fca.FormalLattice;
import io.github.pmonnin.settings.OntologySettings;

import java.io.IOException;
import java.util.*;

/**
 * Mine an annotated lattice w.r.t an ontology to compare their subsumption axioms
 * @author Pierre Monnin
 */
public class LatticeOntologyMiner {

    public List<Subsumption> mineLatticeWrtOntology(FormalLattice lattice, OntologySettings ontologySettings) {
        Map<String, Subsumption> results = new HashMap<>();

        // Compare lattice and ontologies
        for (int i = 0 ; i < lattice.getConceptsNumber() ; i++) {
            FormalConcept current = lattice.getConcept(i);

            for (FormalConcept parent : current.getParents()) {
                for (Integer currentClass : current.getAnnotation("proper-annotation")) {
                    for (Integer parentClass : parent.getAnnotation("proper-annotation")) {
                        compareClasses(lattice.getAnnotationObject(currentClass),
                                lattice.getAnnotationObject(parentClass), current, parent, results,
                                ontologySettings);
                    }
                }
            }
        }

        ArrayList<Subsumption> retVal = new ArrayList<>(results.values());
        for(Subsumption s : retVal)
            s.computeAverages();

        return retVal;
    }

    private void compareClasses(String bottomClass, String topClass, FormalConcept concept, FormalConcept parent,
                                Map<String, Subsumption> results, OntologySettings ontologySettings) {

        double extentRatio = (double) concept.getExtentSize() / (double) parent.getExtentSize();
        double intentRatio = (double) parent.getIntentSize() / (double) concept.getIntentSize();

        if (results.containsKey(bottomClass + " C " + topClass)) {
            Subsumption s = results.get(bottomClass + " C " + topClass);
            s.found(extentRatio, intentRatio, concept.getDepth());
        }

        else {
            Subsumption.Type type = determineSubsumptionType(bottomClass, topClass, ontologySettings);
            Subsumption s = new Subsumption(type, topClass, bottomClass, extentRatio, intentRatio, concept.getDepth());
            results.put(bottomClass + " C " + topClass, s);
        }

    }

    private Subsumption.Type determineSubsumptionType(String bottomClass, String topClass,
                                                      OntologySettings ontologySettings) {

        ServerQuerier querier = new ServerQuerier();

        try {
            SparqlResponse r = querier.runQuery(ontologySettings.getParentsPrefixes() + " ASK { " +
                    "<" + bottomClass + "> " + ontologySettings.getParentsPredicate() + " <" + topClass + "> }");

            if (r.getAskResult())
                return Subsumption.Type.DIRECT;

            r = querier.runQuery(ontologySettings.getParentsPrefixes() + " ASK { " +
                    "<" + bottomClass + "> " + ontologySettings.getParentsPredicate() + "+ <" + topClass + "> }");

            if (r.getAskResult())
                return Subsumption.Type.INFERRED;

            r = querier.runQuery(ontologySettings.getParentsPrefixes() + " ASK { " +
                    "<" + topClass + "> " + ontologySettings.getParentsPredicate() + "+ <" + bottomClass + "> }");

            if (r.getAskResult())
                return Subsumption.Type.REVERSED;
        }

        catch (IOException e) {
            System.err.println("[ERROR] Error while getting the subsumption type of " + bottomClass + " and " + topClass
                    + " (" + e.getMessage() + ")");
        }

        return Subsumption.Type.NEW;
    }

}
