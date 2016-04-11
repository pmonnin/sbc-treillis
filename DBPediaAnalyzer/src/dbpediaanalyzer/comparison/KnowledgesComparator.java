package dbpediaanalyzer.comparison;

import dbpediaanalyzer.extraction.DataBasedKnowledgeManager;
import dbpediaanalyzer.extraction.DataBasedSubsumption;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO JAVADOC
 *
 * @author Pierre Monnin
 *
 */
public class KnowledgesComparator {

    public List<ComparisonResult> compareKnowledges(DataBasedKnowledgeManager dbkm,
                                                    EvaluationStrategy strategyConfirmedDirect,
                                                    EvaluationStrategy strategyProposedInferredToDirect,
                                                    EvaluationStrategy strategyProposedNew) {

        List<ComparisonResult> results = new ArrayList<>();

        for(DataBasedSubsumption dbs : dbkm.getDataBasedKnowledge()) {

            // Is this an already existing direct relationship?
            if(dbs.getBottom().getParents().contains(dbs.getTop())) {
                results.add(new ComparisonResult(ComparisonResultType.CONFIRMED_DIRECT,
                        dbs.getBottom(), dbs.getTop(), strategyConfirmedDirect.computeValue(dbs)));
            }

            // Is this an already existing inferred relationship?
            else if(dbs.getBottom().getAncestors().contains(dbs.getTop())) {
                results.add(new ComparisonResult(ComparisonResultType.PROPOSED_INFERRED_TO_DIRECT,
                        dbs.getBottom(), dbs.getTop(), strategyProposedInferredToDirect.computeValue(dbs)));
            }

            // Is this a new relationship?
            else {
                results.add(new ComparisonResult(ComparisonResultType.PROPOSED_NEW,
                        dbs.getBottom(), dbs.getTop(), strategyProposedNew.computeValue(dbs)));
            }

        }

        return results;

    }
}
