package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.StreamSupport;

public abstract class CumulativeLinkService<T extends CumulativeLink> {

    ICumulativeLinkRepository<T> cumulativeLinkRepo;

    private int cumulativeTemporalGraphMinutes;
    private int temporalSubGraphsMinutes;

    private int cumulativeTemporalGraphNumber;
    int temporalSubGraphNumber;

    CumulativeLinkService() { }

    CumulativeLinkService(ICumulativeLinkRepository<T> cumulativeLinkRepo) {
        this.cumulativeLinkRepo = cumulativeLinkRepo;
    }

    public abstract void cumulateLinks(Collection<? extends Link> temporalSubGraph);

    @Transactional
    public void normalizeCumulativeLinks() {
        StreamSupport.stream(cumulativeLinkRepo.findAll().spliterator(), true).forEach(cumulativeLink ->
                cumulativeLink.setCumulativeTemporalSubGraphsCounter(
                        Arrays.copyOf(cumulativeLink.getCumulativeTemporalSubGraphsCounter(), getCumulativeGraphCounterArraySize())));
    }

    public void setCumulativeTemporalGraphMinutes(int cumulativeTemporalGraphMinutes) {
        this.cumulativeTemporalGraphMinutes = cumulativeTemporalGraphMinutes;
    }

    public void setTemporalSubGraphsMinutes(int temporalSubGraphMinutes) {
        this.temporalSubGraphsMinutes = temporalSubGraphMinutes;
    }

    public void setCumulativeTemporalGraphNumber(int cumulativeTemporalGraphNumber) {
        this.cumulativeTemporalGraphNumber = cumulativeTemporalGraphNumber;
    }

    public void setTemporalSubGraphNumber(int temporalSubGraphNumber) {
        this.temporalSubGraphNumber = temporalSubGraphNumber;
    }

    int getCumulativeGraphCounterArraySize() {
        return (cumulativeTemporalGraphMinutes / temporalSubGraphsMinutes) * cumulativeTemporalGraphNumber;
    }
}