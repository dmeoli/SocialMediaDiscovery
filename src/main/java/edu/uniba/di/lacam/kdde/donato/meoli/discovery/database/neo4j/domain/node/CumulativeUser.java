package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node;

import com.google.gson.annotations.Expose;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.*;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.mining.FrequentPattern;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.*;

import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeCommentLink.CUMULATIVE_COMMENT_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeEmotionalLink.CUMULATIVE_EMOTION_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLexicalSimilarityLink.CUMULATIVE_LEXICAL_SIMILARITY_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeMentionLink.CUMULATIVE_MENTION_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeReplyLink.CUMULATIVE_REPLY_LINK_LABEL;
import static edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeSemanticSimilarityLink.CUMULATIVE_SEMANTIC_SIMILARITY_LINK_LABEL;

@NodeEntity
public class CumulativeUser {

    @Id @Expose
    private String name;

    @Relationship(type = CUMULATIVE_COMMENT_LINK_LABEL)
    private Set<CumulativeCommentLink> cumulativeCommentLinks = new HashSet<>();

    @Relationship(type = CUMULATIVE_REPLY_LINK_LABEL)
    private Set<CumulativeReplyLink> cumulativeReplyLinks = new HashSet<>();

    @Relationship(type = CUMULATIVE_MENTION_LINK_LABEL)
    private Set<CumulativeMentionLink> cumulativeMentionLinks = new HashSet<>();

    @Relationship(type = CUMULATIVE_LEXICAL_SIMILARITY_LINK_LABEL)
    private Set<CumulativeLexicalSimilarityLink> cumulativeLexicalSimilarityLinks = new HashSet<>();

    @Relationship(type = CUMULATIVE_SEMANTIC_SIMILARITY_LINK_LABEL)
    private Set<CumulativeSemanticSimilarityLink> cumulativeSemanticSimilarityLinks = new HashSet<>();

    @Relationship(type = CUMULATIVE_EMOTION_LINK_LABEL)
    private Set<CumulativeEmotionalLink> cumulativeEmotionalLinks = new HashSet<>();

    @Expose
    private int inDegree;

    @Expose
    private int outDegree;

    @Expose
    private double betweennessScore;

    @Expose
    private double pageRankScore;

    @Expose
    private int louvainCommunityID;

    @Expose
    private List<FrequentPattern> frequentPatterns = new ArrayList<>();

    CumulativeUser() { }

    public CumulativeUser(String name) {
        this.name = name;
    }

    public CumulativeUser(User user) {
        this.name = user.getName();
    }

    public String getName() {
        return name;
    }

    public Set<CumulativeCommentLink> getCumulativeCommentLinks() {
        return cumulativeCommentLinks;
    }

    public Set<CumulativeReplyLink> getCumulativeReplyLinks() {
        return cumulativeReplyLinks;
    }

    public Set<CumulativeMentionLink> getCumulativeMentionLinks() {
        return cumulativeMentionLinks;
    }

    public Set<CumulativeLexicalSimilarityLink> getCumulativeLexicalSimilarityLinks() {
        return cumulativeLexicalSimilarityLinks;
    }

    public Set<CumulativeSemanticSimilarityLink> getCumulativeSemanticSimilarityLinks() {
        return cumulativeSemanticSimilarityLinks;
    }

    public Set<CumulativeEmotionalLink> getCumulativeEmotionalLinks() {
        return cumulativeEmotionalLinks;
    }

    public void commentsCumulativeTo(CumulativeCommentLink cumulativeCommentLink) {
        cumulativeCommentLinks.add(cumulativeCommentLink);
    }

    public void repliesCumulativeTo(CumulativeReplyLink cumulativeReplyLink) {
        cumulativeReplyLinks.add(cumulativeReplyLink);
    }

    public void mentionsCumulativeTo(CumulativeMentionLink cumulativeMentionLink) {
        cumulativeMentionLinks.add(cumulativeMentionLink);
    }

    public void writesCumulativeLexicallySimilarTo(CumulativeLexicalSimilarityLink cumulativeLexicalSimilarityLink) {
        cumulativeLexicalSimilarityLinks.add(cumulativeLexicalSimilarityLink);
    }

    public void writesCumulativeSemanticallySimilarTo(CumulativeSemanticSimilarityLink cumulativeSemanticSimilarityLink) {
        cumulativeSemanticSimilarityLinks.add(cumulativeSemanticSimilarityLink);
    }

    public void writesCumulativeEmotionTo(CumulativeEmotionalLink cumulativeEmotionalLink) {
        cumulativeEmotionalLinks.add(cumulativeEmotionalLink);
    }

    public int getInDegree() {
        return inDegree;
    }

    public int getOutDegree() {
        return outDegree;
    }

    public double getBetweennessScore() {
        return betweennessScore;
    }

    public double getPageRankScore() {
        return pageRankScore;
    }

    public int getLouvainCommunityID() {
        return louvainCommunityID;
    }

    public List<FrequentPattern> getFrequentPatterns() {
        return frequentPatterns;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof CumulativeUser && ((CumulativeUser) obj).getName().equals(name);
    }
}
