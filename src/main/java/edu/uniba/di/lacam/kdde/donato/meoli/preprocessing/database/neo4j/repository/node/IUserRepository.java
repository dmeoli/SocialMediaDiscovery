package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.node;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IUserRepository extends Neo4jRepository<User, String> { }
