package edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongo.domain;

import com.google.gson.annotations.Expose;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;

import java.util.List;

public class Result {

    @Expose
    private long utc;

    @Expose
    private List<CumulativeUser> users;

    public Result(long utc, List<CumulativeUser> users) {
        this.utc = utc;
        this.users = users;
    }

    public long getUTC() {
        return utc;
    }

    public List<CumulativeUser> getUsers() {
        return users;
    }
}
