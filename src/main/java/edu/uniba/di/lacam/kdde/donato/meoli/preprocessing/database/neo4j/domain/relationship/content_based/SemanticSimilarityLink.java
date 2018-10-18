package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.SemanticSimilarityLink.SEMANTIC_SIMILARITY_LINK_LABEL;

@RelationshipEntity(type = SEMANTIC_SIMILARITY_LINK_LABEL)
public class SemanticSimilarityLink extends ContinuousContentBasedLink {

    public static final String SEMANTIC_SIMILARITY_LINK_LABEL = "SEMANTIC_SIMILAR_TO";

    SemanticSimilarityLink() { }

    public SemanticSimilarityLink(User userFrom, User userTo, long utc, double score) {
        super(userFrom, userTo, utc, score);
    }

    @Override
    protected void addLink() {
        getUserFrom().writesSemanticallySimilarTo(this);
    }

    @Override
    public String getLinkLabel() {
        return SEMANTIC_SIMILARITY_LINK_LABEL;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof SemanticSimilarityLink && ((SemanticSimilarityLink) obj).getUTC().equals(getUTC())
                && ((SemanticSimilarityLink) obj).getUserFrom().equals(getUserFrom())
                && ((SemanticSimilarityLink) obj).getUserTo().equals(getUserTo());
    }
}
