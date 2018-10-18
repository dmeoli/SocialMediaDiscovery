package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import org.neo4j.ogm.annotation.*;
import org.neo4j.ogm.annotation.typeconversion.Convert;
import org.neo4j.ogm.annotation.typeconversion.DateString;
import org.neo4j.ogm.typeconversion.LocalDateTimeStringConverter;

import java.time.Instant;
import java.time.LocalDateTime;

import static java.time.ZoneOffset.UTC;

@RelationshipEntity
public abstract class Link {

    @Id @GeneratedValue
    private Long id;

    @StartNode
    private User userFrom;

    @EndNode
    private User userTo;

    @DateString @Index @Convert(LocalDateTimeStringConverter.class)
    private LocalDateTime utc;

    protected Link() { }

    protected Link(User userFrom, User userTo, long utc) {
        this.userFrom = userFrom;
        this.userTo = userTo;
        this.utc = Instant.ofEpochSecond(utc).atOffset(UTC).toLocalDateTime();
        addLink();
    }

    public Long getId() {
        return id;
    }

    public User getUserFrom() {
        return userFrom;
    }

    public User getUserTo() {
        return userTo;
    }

    public LocalDateTime getUTC() {
        return utc;
    }

    abstract protected void addLink();

    public abstract String getLinkLabel();
}
