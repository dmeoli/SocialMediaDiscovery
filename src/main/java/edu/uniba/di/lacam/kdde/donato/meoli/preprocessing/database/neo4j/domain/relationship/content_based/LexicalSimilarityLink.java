package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.RelationshipEntity;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.LexicalSimilarityLink.LEXICAL_SIMILARITY_LINK_LABEL;

@RelationshipEntity(type = LEXICAL_SIMILARITY_LINK_LABEL)
public class LexicalSimilarityLink extends ContinuousContentBasedLink {

    public static final String LEXICAL_SIMILARITY_LINK_LABEL = "LEXICAL_SIMILAR_TO";

    LexicalSimilarityLink() { }

    public LexicalSimilarityLink(User userFrom, User userTo, long utc, double score) {
        super(userFrom, userTo, utc, score);
    }

    @Override
    protected void addLink() {
        getUserFrom().writesLexicallySimilarTo(this);
    }

    @Override
    public String getLinkLabel() {
        return LEXICAL_SIMILARITY_LINK_LABEL;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LexicalSimilarityLink && ((LexicalSimilarityLink) obj).getUTC().equals(getUTC())
                && ((LexicalSimilarityLink) obj).getUserFrom().equals(getUserFrom())
                && ((LexicalSimilarityLink) obj).getUserTo().equals(getUserTo());
    }
}
