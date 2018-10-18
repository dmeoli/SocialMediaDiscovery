package edu.uniba.di.lacam.kdde.donato.meoli.discovery.mining;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import org.dyminer.model.LabeledEdge;
import org.dyminer.model.Transaction;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.ZoneOffset.UTC;

public class TemporalSubGraph implements Transaction<LabeledEdge> {

    private int id;
    private LocalDateTime utc;
    private Set<LabeledEdge> labeledEdges;

    TemporalSubGraph(int id, Collection<Link> temporalSubGraph, LocalDateTime utc) {
        this.id = id;
        this.utc = utc;
        this.labeledEdges = temporalSubGraph.parallelStream().map(link ->
                new LabeledEdge(link.getUserFrom().getName(), link.getUserTo().getName(), link.getLinkLabel()))
                .collect(Collectors.toSet());
    }

    @Override
    public Instant getTimestamp() {
        return utc.toInstant(UTC);
    }

    @Override
    public Collection<LabeledEdge> getItems() {
        return labeledEdges;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override @NotNull
    public Iterator<LabeledEdge> iterator() {
        return getItems().iterator();
    }
}
