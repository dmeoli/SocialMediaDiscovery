package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship.content_based;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.content_based.EmotionalLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.content_based.IEmotionalLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EmotionalLinkService extends DiscreteContentBasedLinkService<EmotionalLink> {

    public EmotionalLinkService() { }

    @Autowired
    public EmotionalLinkService(IEmotionalLinkRepository emotionalLinkRepo) {
        super(emotionalLinkRepo);
    }

    @Override
    public void addLinks(Post post) {
        ((Discussion) post).getEmotions().forEach(similarity -> linkRepo.save(
                new EmotionalLink(new User(similarity.getAuthorFrom()), new User(similarity.getAuthorTo()),
                        similarity.getUTC(), similarity.getLabel())));
    }
}
