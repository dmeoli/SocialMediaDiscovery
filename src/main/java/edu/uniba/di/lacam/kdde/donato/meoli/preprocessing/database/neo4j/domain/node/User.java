package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.MentionLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.ReplyLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.EmotionalLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.LexicalSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.SemanticSimilarityLink;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.HashSet;
import java.util.Set;

import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink.COMMENT_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.MentionLink.MENTION_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.ReplyLink.REPLY_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.EmotionalLink.EMOTIONAL_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.LexicalSimilarityLink.LEXICAL_SIMILARITY_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.SemanticSimilarityLink.SEMANTIC_SIMILARITY_LINK_LABEL;

@NodeEntity
public class User {

    @Id
    private String name;

    @Relationship(type = COMMENT_LINK_LABEL)
    private Set<CommentLink> commentLinks = new HashSet<>();

    @Relationship(type = REPLY_LINK_LABEL)
    private Set<ReplyLink> replyLinks = new HashSet<>();

    @Relationship(type = MENTION_LINK_LABEL)
    private Set<MentionLink> mentionLinks = new HashSet<>();

    @Relationship(type = LEXICAL_SIMILARITY_LINK_LABEL)
    private Set<LexicalSimilarityLink> lexicalSimilarityLinks = new HashSet<>();

    @Relationship(type = SEMANTIC_SIMILARITY_LINK_LABEL)
    private Set<SemanticSimilarityLink> semanticSimilarityLinks = new HashSet<>();

    @Relationship(type = EMOTIONAL_LINK_LABEL)
    private Set<EmotionalLink> emotionalLinks = new HashSet<>();

    private User() { }

    public User(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Set<CommentLink> getCommentLinks() {
        return commentLinks;
    }

    public Set<ReplyLink> getReplyLinks() {
        return replyLinks;
    }

    public Set<MentionLink> getMentionLinks() {
        return mentionLinks;
    }

    public Set<LexicalSimilarityLink> getLexicalSimilarityLinks() {
        return lexicalSimilarityLinks;
    }

    public Set<SemanticSimilarityLink> getSemanticSimilarityLinks() {
        return semanticSimilarityLinks;
    }

    public Set<EmotionalLink> getEmotionalLinks() {
        return emotionalLinks;
    }

    public void commentsTo(CommentLink commentLink) {
        commentLinks.add(commentLink);
    }

    public void repliesTo(ReplyLink replyLink) {
        replyLinks.add(replyLink);
    }

    public void mentionsTo(MentionLink mentionLink) {
        mentionLinks.add(mentionLink);
    }

    public void writesLexicallySimilarTo(LexicalSimilarityLink lexicalSimilarityLink) {
        lexicalSimilarityLinks.add(lexicalSimilarityLink);
    }

    public void writesSemanticallySimilarTo(SemanticSimilarityLink semanticSimilarityLink) {
        semanticSimilarityLinks.add(semanticSimilarityLink);
    }

    public void writesEmotionTo(EmotionalLink emotionalLink) {
        emotionalLinks.add(emotionalLink);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof User && ((User) obj).getName().equals(name);
    }
}
