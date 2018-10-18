package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongodb.domain;

import com.google.gson.annotations.Expose;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;

import java.time.LocalDateTime;
import java.util.List;

public class Result {

    @Expose
    private LocalDateTime utc;

    @Expose
    private List<CumulativeUser> users;

    public Result(LocalDateTime utc, List<CumulativeUser> users) {
        this.utc = utc;
        this.users = users;
    }

    public LocalDateTime getUTC() {
        return utc;
    }

    public List<CumulativeUser> getUsers() {
        return users;
    }
}
