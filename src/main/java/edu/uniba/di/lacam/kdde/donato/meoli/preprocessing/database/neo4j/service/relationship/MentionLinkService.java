package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Comment;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.MentionLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.IMentionLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MentionLinkService extends LinkService<MentionLink> {

    public MentionLinkService() { }

    @Autowired
    public MentionLinkService(IMentionLinkRepository mentionLinkRepo) {
        super(mentionLinkRepo);
    }

    @Override
    public void addLinks(Post post) {
        post.getMentions().stream().filter(author -> !post.getAuthor().equals(author)).forEach(author ->
                linkRepo.save(new MentionLink(new User(post.getAuthor()), new User(author), post.getUTC())));
        ((post instanceof Discussion) ? ((Discussion) post).getComments() : ((Comment) post).getReplies()).forEach(this::addLinks);
    }
}
