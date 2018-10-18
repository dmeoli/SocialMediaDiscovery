package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink;
import org.springframework.stereotype.Repository;

@Repository
public interface ICommentLinkRepository extends ILinkRepository<CommentLink> { }
