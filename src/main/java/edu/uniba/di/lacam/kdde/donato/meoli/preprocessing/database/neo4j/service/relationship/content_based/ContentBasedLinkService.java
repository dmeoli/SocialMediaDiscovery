package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.ContentBasedLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.IContentBasedLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.LinkService;

abstract class ContentBasedLinkService<T extends ContentBasedLink> extends LinkService<T> {

    ContentBasedLinkService() { }

    ContentBasedLinkService(IContentBasedLinkRepository<T> contentBasedLinkRepo) {
        super(contentBasedLinkRepo);
    }
}
