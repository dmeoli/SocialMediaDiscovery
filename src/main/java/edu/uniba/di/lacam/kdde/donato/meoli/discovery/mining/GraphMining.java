package edu.uniba.di.lacam.kdde.donato.meoli.discovery.mining;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongodb.SocialMediaDiscoveryResult;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongodb.domain.Result;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.CumulativeSocialMediaGraph;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.SocialMediaGraph;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.dyminer.jminer.joiners.FrequencyEvaluator;
import org.dyminer.jminer.providers.TidlistProvider;
import org.dyminer.jminer.structures.IncrementalStrategy;
import org.dyminer.jminer.structures.Strategies;
import org.dyminer.jminer.structures.TransactionException;
import org.dyminer.model.LabeledEdge;
import org.dyminer.model.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;

@Component
public class GraphMining {

    private static final int MAX_PATTERN_LENGTH = 3;
    private static final int NODE_INDICATORS_THRESHOLD = 10;
    private static final int FREQUENT_PATTERN_THRESHOLD = 2;

    private static final float FREQUENT_PATTERN_RELATIVE_SUPPORT = 1.0F;

    private SocialMediaGraph socialMediaGraph;
    private CumulativeSocialMediaGraph cumulativeSocialMediaGraph;

    private SocialMediaDiscoveryResult resultsDB;

    private int cumulativeTemporalGraphMinutes;
    private int temporalSubGraphsMinutes;
    private float frequentPatternMinSupport;

    @Autowired
    public GraphMining(SocialMediaGraph socialMediaGraph, CumulativeSocialMediaGraph cumulativeSocialMediaGraph) {
        this.cumulativeTemporalGraphMinutes = SocialMediaDiscoveryConfiguration.getInstance().getCumulativeTemporalGraphMinutes();
        this.temporalSubGraphsMinutes = SocialMediaDiscoveryConfiguration.getInstance().getTemporalSubGraphsMinutes();
        this.frequentPatternMinSupport = SocialMediaDiscoveryConfiguration.getInstance().getFrequentPatternMinSupport();
        this.socialMediaGraph = socialMediaGraph;
        this.cumulativeSocialMediaGraph = cumulativeSocialMediaGraph;
        resultsDB = new SocialMediaDiscoveryResult();
    }

    public void executeTemporalSocialMediaAnalysis() {
        checkArgument(temporalSubGraphsMinutes < cumulativeTemporalGraphMinutes);
        LocalDateTime firstUtc = socialMediaGraph.getFirstUTC();
        LocalDateTime lastUtc = socialMediaGraph.getLastUTC();
        LocalDateTime endUtc = firstUtc.plusMinutes(cumulativeTemporalGraphMinutes);
        int temporalSubGraphsMinutesCounter = temporalSubGraphsMinutes;
        cumulativeSocialMediaGraph.setTemporalSubGraphsMinutes(temporalSubGraphsMinutesCounter);
        int cumulativeTemporalGraphNumber = 1;
        cumulativeSocialMediaGraph.setCumulativeTemporalGraphNumber(cumulativeTemporalGraphNumber);
        int temporalSubGraphNumber = 0;
        cumulativeSocialMediaGraph.setTemporalSubGraphNumber(temporalSubGraphNumber);
        List<Transaction<LabeledEdge>> labeledEdges = new ArrayList<>();
        while (!endUtc.isAfter(lastUtc)) {
            while (firstUtc.isBefore(endUtc) && !firstUtc.plusMinutes(temporalSubGraphsMinutes).isAfter(endUtc)) {
                Collection<Link> temporalSubGraphs = socialMediaGraph.getTemporalSubGraphs(firstUtc,
                        firstUtc.plusMinutes(temporalSubGraphsMinutes));
                firstUtc = firstUtc.plusMinutes(temporalSubGraphsMinutes);
                cumulativeSocialMediaGraph.cumulateTemporalSubGraphs(temporalSubGraphs);
                labeledEdges.add(new TemporalSubGraph(temporalSubGraphNumber, temporalSubGraphs, firstUtc));
                cumulativeSocialMediaGraph.setTemporalSubGraphNumber(++temporalSubGraphNumber);
            }
            cumulativeSocialMediaGraph.setCumulativeTemporalGraphNumber(++cumulativeTemporalGraphNumber);
            endUtc = endUtc.plusMinutes(cumulativeTemporalGraphMinutes);
            temporalSubGraphsMinutesCounter += temporalSubGraphsMinutes;
            cumulativeSocialMediaGraph.setTemporalSubGraphsMinutes(temporalSubGraphsMinutesCounter);
            analyzeTemporalSubGraph(labeledEdges, endUtc, frequentPatternMinSupport);
            cumulativeSocialMediaGraph.normalizeCumulativeSocialMediaGraph();
        }
        cumulativeSocialMediaGraph.deleteCumulativeSocialMediaGraph();
    }

    private void analyzeTemporalSubGraph(List<Transaction<LabeledEdge>> temporalSubGraphs, LocalDateTime endUtc, float minSupport) {
        cumulativeSocialMediaGraph.computeCentralities();
        cumulativeSocialMediaGraph.computeCommunityDetection();
        Map<String, CumulativeUser> cumulativeUsers = cumulativeSocialMediaGraph.getFilteredCumulativeUsers(NODE_INDICATORS_THRESHOLD);
        try {
            frequentPatternDiscovery(temporalSubGraphs, minSupport).parallelStream()
                    .filter(frequentPattern -> frequentPattern.getPatterns().getLength() >= FREQUENT_PATTERN_THRESHOLD)
                    .filter(frequentPattern -> cumulativeUsers.containsKey(frequentPattern.getPatterns().getSuffix().getStartNode()))
                    .forEach(frequentPattern -> cumulativeUsers.get(frequentPattern.getPatterns().getSuffix().getStartNode())
                            .getFrequentPatterns().add(frequentPattern.toString()));
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        cumulativeUsers.values().removeIf(cumulativeUser -> cumulativeUser.getFrequentPatterns().isEmpty());
        resultsDB.insertResult(new Result(endUtc, new ArrayList<>(cumulativeUsers.values())),
                cumulativeTemporalGraphMinutes, temporalSubGraphsMinutes, minSupport);
    }

    private List<FrequentPattern> frequentPatternDiscovery(List<Transaction<LabeledEdge>> temporalSubGraphs,
                                                           float minSupport) throws TransactionException {
        TidlistProvider<LabeledEdge> provider = new TidlistProvider<>(true);
        IncrementalStrategy<LabeledEdge, Set<Integer>> strategy = Strategies.onlineSubgraphEclat(
                provider, MAX_PATTERN_LENGTH, minSupport);
        FrequencyEvaluator<Set<Integer>> evaluator = (FrequencyEvaluator<Set<Integer>>) strategy.getEvaluator();
        evaluator.setTransactionCount(temporalSubGraphs.size());
        temporalSubGraphs.parallelStream().forEach(transaction -> {
            StreamSupport.stream(transaction.spliterator(), true).forEach(strategy::setGenerator);
            provider.accept(transaction);
        });
        strategy.commit();
        return StreamSupport.stream(strategy.execute().spliterator(), true).map(pattern ->
                pattern.getSuffix() != null ?
                        new FrequentPattern(evaluator.getRelativeFrequency(pattern.getEval().getIncrement()), pattern) :
                        new FrequentPattern(FREQUENT_PATTERN_RELATIVE_SUPPORT, pattern)).collect(Collectors.toList());
    }
}