package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import org.neo4j.ogm.annotation.*;

import static com.google.common.base.Preconditions.checkArgument;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous.ContinuousContentBasedFeatureExtraction.MAX_SCORE;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.continuous.ContinuousContentBasedFeatureExtraction.MIN_SCORE;

@RelationshipEntity
public abstract class CumulativeLink {

    @Id @GeneratedValue
    private Long id;

    @StartNode
    private CumulativeUser cumulativeUserFrom;

    @EndNode
    private CumulativeUser cumulativeUserTo;

    private int[] cumulativeTemporalSubGraphsCounter;

    private double weight;

    CumulativeLink() { }

    CumulativeLink(CumulativeUser cumulativeUserFrom, CumulativeUser cumulativeUserTo,
                   int[] cumulativeTemporalSubGraphsCounter) {
        this.cumulativeUserFrom = cumulativeUserFrom;
        this.cumulativeUserTo = cumulativeUserTo;
        this.cumulativeTemporalSubGraphsCounter = cumulativeTemporalSubGraphsCounter;
        weight = getWeightedAverage();
        addCumulativeLink();
    }

    public Long getId() {
        return id;
    }

    public CumulativeUser getCumulativeUserFrom() {
        return cumulativeUserFrom;
    }

    public CumulativeUser getCumulativeUserTo() {
        return cumulativeUserTo;
    }

    public void setCumulativeTemporalSubGraphsCounter(int[] cumulativeTemporalSubGraphsCounter) {
        this.cumulativeTemporalSubGraphsCounter = cumulativeTemporalSubGraphsCounter;
        weight = getWeightedAverage();
    }

    public void incrementCumulativeTemporalSubGraphsCounter(int temporalSubGraphNumber) {
        cumulativeTemporalSubGraphsCounter[temporalSubGraphNumber]++;
        weight = getWeightedAverage();
    }

    public int[] getCumulativeTemporalSubGraphsCounter() {
        return cumulativeTemporalSubGraphsCounter;
    }

    public double getWeight() {
        return weight;
    }

    private double getWeightedAverage() {
        int values = 0;
        int weights = 0;
        for (int i = 0; i < cumulativeTemporalSubGraphsCounter.length; i++) {
            values += cumulativeTemporalSubGraphsCounter[i] * (i + 1);
            weights += (i + 1);
        }
        double weightedAverage = ((double) values / weights);
        checkArgument(MIN_SCORE <= weightedAverage && weightedAverage <= MAX_SCORE);
        return weightedAverage;
    }

    protected abstract void addCumulativeLink();
}
