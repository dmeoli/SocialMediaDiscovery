package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.relationship.CumulativeLink;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface ICumulativeLinkRepository<T extends CumulativeLink> extends Neo4jRepository<T, Long> {

    Optional<T> findByCumulativeUserFromAndCumulativeUserTo(CumulativeUser cumulativeUserFrom, CumulativeUser cumulativeUserTo);
}