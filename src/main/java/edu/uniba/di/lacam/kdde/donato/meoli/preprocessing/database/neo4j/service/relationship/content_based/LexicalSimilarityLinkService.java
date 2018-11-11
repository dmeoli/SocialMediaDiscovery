package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.LexicalSimilarityLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.ILexicalSimilarityLinkRepository;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.nlp.feature.discrete.DiscreteContentBasedLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;

@Service
public class LexicalSimilarityLinkService extends ContinuousContentBasedLinkService<LexicalSimilarityLink> {

    public LexicalSimilarityLinkService() { }

    @Autowired
    public LexicalSimilarityLinkService(ILexicalSimilarityLinkRepository lexicalSimilarityLinkRepo) {
        super(lexicalSimilarityLinkRepo);
    }

    @Override
    public void addLinks(Post post) {
        ((Discussion) post).getLexicalSimilarities().stream().max(
                Comparator.comparingDouble(DiscreteContentBasedLink::getScore)).ifPresent(maxSimilarity ->
                ((Discussion) post).getLexicalSimilarities().stream().filter(similarity ->
                        similarity.getScore() > (maxSimilarity.getScore() * CONTINUOUS_CONTENT_BASED_LINK_THRESHOLD) / 100)
                        .forEach(similarity -> linkRepo.save(new LexicalSimilarityLink(
                                new User(similarity.getAuthorFrom()), new User(similarity.getAuthorTo()),
                                similarity.getUTC(), similarity.getScore()))));
    }
}
