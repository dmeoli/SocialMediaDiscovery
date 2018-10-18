package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.node;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.node.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private IUserRepository userRepo;

    public UserService() { }

    @Autowired
    public UserService(IUserRepository userRepo) {
        this.userRepo = userRepo;
    }
}
