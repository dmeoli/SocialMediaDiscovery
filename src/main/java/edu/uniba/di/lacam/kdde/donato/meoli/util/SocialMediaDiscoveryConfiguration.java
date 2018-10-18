package edu.uniba.di.lacam.kdde.donato.meoli.util;

import edu.uniba.di.lacam.kdde.donato.meoli.database.Dataset;

final public class SocialMediaDiscoveryConfiguration {

    private Dataset dataset;
    private int year;
    private int month;

    private boolean lexicalSimilarityLinks;
    private boolean semanticSimilarityLinks;
    private boolean emotionalLinks;

    private int cumulativeTemporalGraphMinutes;
    private int temporalSubGraphsMinutes;
    private float frequentPatternMinSupport;

    private static final SocialMediaDiscoveryConfiguration SOCIAL_MEDIA_DISCOVERY_CONFIGURATION =
            new SocialMediaDiscoveryConfiguration();

    public static SocialMediaDiscoveryConfiguration getInstance(){
        return SOCIAL_MEDIA_DISCOVERY_CONFIGURATION;
    }

    public boolean useSemanticSimilarityLinks() {
        return semanticSimilarityLinks;
    }

    public void setSemanticSimilarityLinks(boolean semanticSimilarityLinks) {
        this.semanticSimilarityLinks = semanticSimilarityLinks;
    }

    public boolean useEmotionalLinks() {
        return emotionalLinks;
    }

    public void setEmotionalLinks(boolean emotionalSimilarityLinks) {
        this.emotionalLinks = emotionalSimilarityLinks;
    }

    public boolean useLexicalSimilarityLinks() {
        return lexicalSimilarityLinks;
    }

    public void setLexicalSimilarityLinks(boolean lexicalSimilarityLinks) {
        this.lexicalSimilarityLinks = lexicalSimilarityLinks;
    }

    public void setDataset(Dataset dataset) {
        this.dataset = dataset;
    }

    public Dataset getDataset() {
        return dataset;
    }


    public void setYear(int year) {
        this.year = year;
    }

    public int getYear() {
        return year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getMonth() {
        return month;
    }

    public void setCumulativeTemporalGraphMinutes(int cumulativeTemporalGraphMinutes) {
        this.cumulativeTemporalGraphMinutes = cumulativeTemporalGraphMinutes;
    }

    public int getCumulativeTemporalGraphMinutes() {
        return cumulativeTemporalGraphMinutes;
    }

    public void setTemporalSubGraphsMinutes(int temporalSubGraphsMinutes) {
        this.temporalSubGraphsMinutes = temporalSubGraphsMinutes;
    }

    public int getTemporalSubGraphsMinutes() {
        return temporalSubGraphsMinutes;
    }

    public void setFrequentPatternMinSupport(float minSupport) {
        this.frequentPatternMinSupport = minSupport;
    }

    public float getFrequentPatternMinSupport() {
        return frequentPatternMinSupport;
    }
}