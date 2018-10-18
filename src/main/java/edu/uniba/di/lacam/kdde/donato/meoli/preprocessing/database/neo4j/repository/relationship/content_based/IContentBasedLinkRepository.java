package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.ContentBasedLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.ILinkRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IContentBasedLinkRepository<T extends ContentBasedLink> extends ILinkRepository<T> { }
