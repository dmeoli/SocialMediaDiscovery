package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLink;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship.ICumulativeLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;

import java.util.Arrays;
import java.util.Collection;

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
        cumulativeLinkRepo.findAll().forEach(cumulativeLink -> {
            cumulativeLink.setCumulativeTemporalSubGraphsCounter(Arrays.copyOf(
                    cumulativeLink.getCumulativeTemporalSubGraphsCounter(), getCumulativeGraphCounterArraySize()));
            cumulativeLinkRepo.save(cumulativeLink);
        });
    }

    public void setCumulativeTemporalGraphNumber(int cumulativeTemporalGraphNumber) {
        this.cumulativeTemporalGraphNumber = cumulativeTemporalGraphNumber;
    }

    public void setTemporalSubGraphNumber(int temporalSubGraphNumber) {
        this.temporalSubGraphNumber = temporalSubGraphNumber;
    }

    int getCumulativeGraphCounterArraySize() {
        return (SocialMediaDiscoveryConfiguration.getInstance().getCumulativeTemporalGraphMinutes() /
                SocialMediaDiscoveryConfiguration.getInstance().getTemporalSubGraphsMinutes()) * cumulativeTemporalGraphNumber;
    }
}