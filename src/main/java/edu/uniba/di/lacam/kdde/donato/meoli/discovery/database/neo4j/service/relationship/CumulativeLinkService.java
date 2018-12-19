package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;

import java.util.Collection;
import java.util.stream.StreamSupport;

public abstract class CumulativeLinkService<T extends CumulativeLink> {

    ICumulativeLinkRepository<T> cumulativeLinkRepo;

    private int cumulativeTemporalGraphNumber;
    int temporalSubGraphNumber;

    CumulativeLinkService() { }

    CumulativeLinkService(ICumulativeLinkRepository<T> cumulativeLinkRepo) {
        this.cumulativeLinkRepo = cumulativeLinkRepo;
    }

    public abstract void cumulateLinks(Collection<? extends Link> temporalSubGraph);

    public void normalizeCumulativeLinks() {
        int newSize = getCumulativeTemporalSubGraphsCounterArraySize();
        Iterable<T> links = cumulativeLinkRepo.findAll();
        StreamSupport.stream(links.spliterator(), true).forEach(cumulativeLink ->
                cumulativeLink.setCumulativeTemporalSubGraphsCounterSize(newSize));
        cumulativeLinkRepo.saveAll(links);
    }

    public void setCumulativeTemporalGraphNumber(int cumulativeTemporalGraphNumber) {
        this.cumulativeTemporalGraphNumber = cumulativeTemporalGraphNumber;
    }

    public void setTemporalSubGraphNumber(int temporalSubGraphNumber) {
        this.temporalSubGraphNumber = temporalSubGraphNumber;
    }

    int getCumulativeTemporalSubGraphsCounterArraySize() {
        return (SocialMediaDiscoveryConfiguration.getInstance().getCumulativeTemporalGraphMinutes() /
                SocialMediaDiscoveryConfiguration.getInstance().getTemporalSubGraphsMinutes()) * cumulativeTemporalGraphNumber;
    }
}