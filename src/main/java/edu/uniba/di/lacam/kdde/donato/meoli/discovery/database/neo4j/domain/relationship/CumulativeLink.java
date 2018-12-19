package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArray;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.utils.SparseArrayConverter;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;

@RelationshipEntity
public abstract class CumulativeLink {

    @Id @GeneratedValue
    private Long id;

    @StartNode
    private CumulativeUser cumulativeUserFrom;

    @EndNode
    private CumulativeUser cumulativeUserTo;

    @Convert(SparseArrayConverter.class)
    private SparseArray cumulativeTemporalSubGraphsCounter;

    private double weight;

    CumulativeLink() { }

    CumulativeLink(CumulativeUser cumulativeUserFrom, CumulativeUser cumulativeUserTo,
                   SparseArray cumulativeTemporalSubGraphsCounter) {
        this.cumulativeUserFrom = cumulativeUserFrom;
        this.cumulativeUserTo = cumulativeUserTo;
        this.cumulativeTemporalSubGraphsCounter = cumulativeTemporalSubGraphsCounter;
        weight = cumulativeTemporalSubGraphsCounter.getWeightedAvg();
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

    public void setCumulativeTemporalSubGraphsCounterSize(int size) {
        cumulativeTemporalSubGraphsCounter.setSize(size);
        weight = cumulativeTemporalSubGraphsCounter.getWeightedAvg();
    }

    public void incrementCumulativeTemporalSubGraphsCounter(int temporalSubGraphNumber) {
        cumulativeTemporalSubGraphsCounter.add(temporalSubGraphNumber,
                cumulativeTemporalSubGraphsCounter.get(temporalSubGraphNumber) + 1);
        weight = cumulativeTemporalSubGraphsCounter.getWeightedAvg();
    }

    public SparseArray getCumulativeTemporalSubGraphsCounter() {
        return cumulativeTemporalSubGraphsCounter;
    }

    public double getWeight() {
        return weight;
    }

    protected abstract void addCumulativeLink();
}