package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.SemanticSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.ISemanticSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete.DiscreteContentBasedLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class SemanticSimilarityLinkService extends ContinuousContentBasedLinkService<SemanticSimilarityLink> {

    public SemanticSimilarityLinkService() { }

    @Autowired
    public SemanticSimilarityLinkService(ISemanticSimilarityLinkRepository semanticSimilarityLinkRepo) {
        super(semanticSimilarityLinkRepo);
    }

    @Override
    public void addLinks(Post post) {
        ((Discussion) post).getSemanticSimilarities().stream().max(
                Comparator.comparingDouble(DiscreteContentBasedLink::getScore)).ifPresent(maxSimilarity ->
                ((Discussion) post).getSemanticSimilarities().stream().filter(similarity ->
                        similarity.getScore() > (maxSimilarity.getScore() * CONTINUOUS_CONTENT_BASED_LINK_THRESHOLD) / 100)
                        .forEach(similarity -> linkRepo.save(new SemanticSimilarityLink(
                                new User(similarity.getAuthorFrom()), new User(similarity.getAuthorTo()),
                                similarity.getUTC(), similarity.getScore()))));
    }
}
