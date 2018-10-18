package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.ContinuousContentBasedLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.IContinuousContentBasedLinkRepository;

abstract class ContinuousContentBasedLinkService<T extends ContinuousContentBasedLink> extends ContentBasedLinkService<T> {

    static final int CONTINUOUS_CONTENT_BASED_LINK_THRESHOLD = 80;

    ContinuousContentBasedLinkService() { }

    ContinuousContentBasedLinkService(IContinuousContentBasedLinkRepository<T> continuousContentBasedLinkRepo) {
        super(continuousContentBasedLinkRepo);
    }
}
