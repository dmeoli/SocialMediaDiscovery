package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.DiscreteContentBasedLink;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface IDiscreteContentBasedLinkRepository<T extends DiscreteContentBasedLink> extends IContentBasedLinkRepository<T> { }
