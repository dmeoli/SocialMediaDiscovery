package edu.uniba.di.lacam.kdde.donato.meoli.discovery.mining;

import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongo.SocialMediaDiscoveryResult;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.mongo.domain.Result;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.CumulativeSocialMediaGraph;
import edu.uniba.di.lacam.kdde.donato.meoli.discovery.database.neo4j.domain.node.CumulativeUser;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.SocialMediaGraph;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.Link;
import edu.uniba.di.lacam.kdde.donato.meoli.util.SocialMediaDiscoveryConfiguration;
import org.dyminer.jminer.interfaces.ItemSet;
import org.dyminer.jminer.interfaces.Lattice;
import org.dyminer.jminer.joiners.FrequencyEvaluator;
import org.dyminer.jminer.providers.TidlistProvider;
import org.dyminer.jminer.structures.IncrementalStrategy;
import org.dyminer.jminer.structures.Pair;
import org.dyminer.jminer.structures.Strategies;
import org.dyminer.jminer.structures.TransactionException;
import org.dyminer.model.LabeledEdge;
import org.dyminer.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;

@Component
public class GraphMining {

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphMining.class);

    private static final int MAX_PATTERN_LENGTH = 3;
    private static final int NODE_INDICATORS_THRESHOLD = 5;
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
        cumulativeSocialMediaGraph.deleteCumulativeSocialMediaGraph();
        LocalDateTime firstUtc = socialMediaGraph.getFirstUtc();
        LocalDateTime lastUtc = socialMediaGraph.getLastUtc();
        cumulativeSocialMediaGraph.setStartUtc(firstUtc);
        LocalDateTime endUtc = firstUtc.plusMinutes(cumulativeTemporalGraphMinutes);
        int cumulativeTemporalGraphNumber = 1;
        cumulativeSocialMediaGraph.setCumulativeTemporalGraphNumber(cumulativeTemporalGraphNumber);
        int temporalSubGraphNumber = 1;
        cumulativeSocialMediaGraph.setTemporalSubGraphNumber(temporalSubGraphNumber);
        List<Transaction<LabeledEdge>> labeledEdges = new ArrayList<>();
        try {
            while (!endUtc.isAfter(lastUtc)) {
                while (firstUtc.isBefore(endUtc) && !firstUtc.plusMinutes(temporalSubGraphsMinutes).isAfter(endUtc)) {
                    Collection<Link> temporalSubGraphs = socialMediaGraph.getTemporalSubGraphs(firstUtc,
                            firstUtc.plusMinutes(temporalSubGraphsMinutes));
                    firstUtc = firstUtc.plusMinutes(temporalSubGraphsMinutes);
                    cumulativeSocialMediaGraph.cumulateTemporalSubGraphs(temporalSubGraphs);
                    labeledEdges.add(new TemporalSubGraph(temporalSubGraphNumber, temporalSubGraphs, firstUtc));
                    cumulativeSocialMediaGraph.setTemporalSubGraphNumber(++temporalSubGraphNumber);
                }
                cumulativeSocialMediaGraph.setEndUtc(endUtc);
                cumulativeSocialMediaGraph.setCumulativeTemporalGraphNumber(++cumulativeTemporalGraphNumber);
                endUtc = endUtc.plusMinutes(cumulativeTemporalGraphMinutes);
                analyzeTemporalSubGraph(labeledEdges, frequentPatternMinSupport);
                cumulativeSocialMediaGraph.normalizeCumulativeSocialMediaGraph();
            }
        } finally {
            cumulativeSocialMediaGraph.deleteCumulativeSocialMediaGraph();
        }
    }

    private void analyzeTemporalSubGraph(List<Transaction<LabeledEdge>> temporalSubGraphs, float frequentPatternMinSupport) {
        LOGGER.info("Starting of the computation of the Centralities on the Cumulative Temporal Graph from {} " +
                "to {}", cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc());
        cumulativeSocialMediaGraph.computeCentralities();
        LOGGER.info("The computation of the Centralities on the Cumulative Temporal Graph from {} to {} has " +
                "been finished", cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc());
        LOGGER.info("Starting of the computation of the Community Detection on Cumulative Temporal Graph from " +
                "{} to {}", cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc());
        cumulativeSocialMediaGraph.computeCommunityDetection();
        LOGGER.info("The computation of the Community Detection on the Cumulative Temporal Graph from {} to {} has " +
                "been finished", cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc());
        Map<String, CumulativeUser> cumulativeUsers = cumulativeSocialMediaGraph
                .getFilteredCumulativeUsers(NODE_INDICATORS_THRESHOLD);
        try {
            frequentPatternDiscovery(temporalSubGraphs, frequentPatternMinSupport).stream()
                    .filter(frequentPattern -> frequentPattern.getPatterns().getLength() >= FREQUENT_PATTERN_THRESHOLD)
                    .filter(frequentPattern -> cumulativeUsers.containsKey(frequentPattern.getPatterns().getSuffix().getStartNode()))
                    .forEach(frequentPattern -> cumulativeUsers.get(frequentPattern.getPatterns().getSuffix().getStartNode())
                            .getFrequentPatterns().add(frequentPattern));
        } catch (TransactionException e) {
            e.printStackTrace();
        }
        cumulativeUsers.values().removeIf(cumulativeUser -> cumulativeUser.getFrequentPatterns().isEmpty());
        LOGGER.info("Saving results of the Social Media Discovery from {} to {}",
                cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc());
        resultsDB.insertResult(new Result(cumulativeSocialMediaGraph.getEndUtc().toEpochSecond(ZoneOffset.UTC),
                        new ArrayList<>(cumulativeUsers.values())),
                cumulativeTemporalGraphMinutes, temporalSubGraphsMinutes, frequentPatternMinSupport);
        LOGGER.info("The results of the Social Media Discovery from {} to {} has been saved",
                cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc());
    }

    private List<FrequentPattern> frequentPatternDiscovery(
            List<? extends Transaction<LabeledEdge>> temporalSubGraphs, float minSupport) throws TransactionException {
        long t = System.currentTimeMillis();
        LOGGER.info("Starting Frequent Pattern Discovery on Temporal Sub Graphs from {} to {}",
                cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc());
        List<FrequentPattern> frequentPatterns = new ArrayList<>();
        TidlistProvider<LabeledEdge> provider = new TidlistProvider<>(true);
        IncrementalStrategy<LabeledEdge, Set<Integer>> strategy = Strategies.onlineSubgraphEclat(
                provider, MAX_PATTERN_LENGTH, minSupport);
        FrequencyEvaluator<Set<Integer>> evaluator = (FrequencyEvaluator<Set<Integer>>) strategy.getEvaluator();
        evaluator.setTransactionCount(temporalSubGraphs.size());
        temporalSubGraphs.forEach(transaction -> {
            transaction.forEach(strategy::setGenerator);
            provider.accept(transaction);
        });
        Lattice<ItemSet<LabeledEdge, Pair<Set<Integer>>>> patterns = strategy.execute();
        strategy.commit();
        patterns.forEach(pattern -> {
            if (Objects.nonNull(pattern.getSuffix())) frequentPatterns.add(
                    new FrequentPattern(evaluator.getRelativeFrequency(pattern.getEval().getIncrement()), pattern));
            else frequentPatterns.add(new FrequentPattern(FREQUENT_PATTERN_RELATIVE_SUPPORT, pattern));
        });
        LOGGER.info("The Frequent Pattern Discovery on Temporal Sub Graphs from {} to {} has been finished in {} min.",
                cumulativeSocialMediaGraph.getStartUtc(), cumulativeSocialMediaGraph.getEndUtc(),
                (System.currentTimeMillis() - t) / 60000L);
        return frequentPatterns;
    }
}