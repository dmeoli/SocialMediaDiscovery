package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.ContinuousContentBasedLink;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IContinuousContentBasedLinkRepository<T extends ContinuousContentBasedLink> extends IContentBasedLinkRepository<T> { }
