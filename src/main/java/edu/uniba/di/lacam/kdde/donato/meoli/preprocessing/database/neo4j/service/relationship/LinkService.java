package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.ILinkRepository;

public abstract class LinkService<T extends Link> {

    protected ILinkRepository<T> linkRepo;

    protected LinkService() { }

    public LinkService(ILinkRepository<T> linkRepo) {
        this.linkRepo = linkRepo;
    }

    public abstract void addLinks(Post post);
}
