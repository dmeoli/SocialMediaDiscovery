package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.DiscreteContentBasedLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.IDiscreteContentBasedLinkRepository;

abstract class DiscreteContentBasedLinkService<T extends DiscreteContentBasedLink> extends ContentBasedLinkService<T> {

    DiscreteContentBasedLinkService() { }

    DiscreteContentBasedLinkService(IDiscreteContentBasedLinkRepository<T> emotionalSimilarityLinkRepo) {
        super(emotionalSimilarityLinkRepo);
    }
}
