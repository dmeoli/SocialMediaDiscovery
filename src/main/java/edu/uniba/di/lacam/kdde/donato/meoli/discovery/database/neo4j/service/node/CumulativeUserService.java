package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.service.node;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.repository.node.ICumulativeUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CumulativeUserService {

    private ICumulativeUserRepository cumulativeUserRepo;

    public CumulativeUserService() { }

    @Autowired
    public CumulativeUserService(ICumulativeUserRepository cumulativeUserRepo) {
        this.cumulativeUserRepo = cumulativeUserRepo;
    }
}
