package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.node;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface ICumulativeUserRepository extends Neo4jRepository<CumulativeUser, String> {

    @Query("MATCH (p:CumulativeUser)<-[]-(q:CumulativeUser) " +
           "WITH p, SIZE(COLLECT(q)) as inDegree " +
           "SET p.inDegree = inDegree")
    void computeInDegree();

    @Query("MATCH (p:CumulativeUser)-[]->(q:CumulativeUser) " +
           "WITH p, SIZE(COLLECT(p)) as outDegree " +
           "SET p.outDegree = outDegree")
    void computeOutDegree();

    @Query("CALL algo.betweenness('CumulativeUser', null, {writeProperty:'betweennessScore'}) YIELD computeMillis")
    long computeBetweennessCentrality();

    @Query("CALL algo.pageRank('CumulativeUser', null, {writeProperty:'pageRankScore'}) YIELD computeMillis")
    long computePageRank();

    @Query("CALL algo.louvain('CumulativeUser', null, {weightProperty:'weight', writeProperty:'louvainCommunityID'}) " +
           "YIELD computeMillis")
    long computeLouvain();

    @Query("MATCH (p:CumulativeUser)" +
           "WITH MAX(p.inDegree) as maxInDegree, " +
                "MAX(p.outDegree) as maxOutDegree, " +
                "MAX(p.betweennessScore) as maxBetweennessScore, " +
                "MAX(p.pageRankScore) as maxPageRankScore " +
           "MATCH (q:CumulativeUser) " +
           "WHERE q.inDegree >= ((maxInDegree * {nodeIndicatorsThreshold}) / 100) " +
              "OR q.outDegree >= ((maxOutDegree * {nodeIndicatorsThreshold}) / 100) " +
              "OR q.betweennessScore >= ((maxBetweennessScore * {nodeIndicatorsThreshold}) / 100) " +
              "OR q.pageRankScore >= ((maxPageRankScore * {nodeIndicatorsThreshold}) / 100) RETURN q")
    Collection<CumulativeUser> getFilteredCumulativeUsers(@Param("nodeIndicatorsThreshold") int nodeIndicatorsThreshold);
}
