package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.EmotionalLink;
import org.springframework.stereotype.Repository;

@Repository
public interface IEmotionalLinkRepository extends IDiscreteContentBasedLinkRepository<EmotionalLink> { }
