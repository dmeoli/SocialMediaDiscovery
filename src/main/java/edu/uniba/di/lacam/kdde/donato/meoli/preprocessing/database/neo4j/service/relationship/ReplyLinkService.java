package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Comment;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.ReplyLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.IReplyLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReplyLinkService extends LinkService<ReplyLink> {

    public ReplyLinkService() { }

    @Autowired
    public ReplyLinkService(IReplyLinkRepository replyLinkRepo) {
        super(replyLinkRepo);
    }

    @Override
    public void addLinks(Post post) {
        if (post instanceof Discussion) ((Discussion) post).getComments().forEach(comment ->
                comment.getReplies().stream().filter(reply -> !reply.getAuthor().equals(comment.getAuthor()))
                        .forEach(reply -> {
                    linkRepo.save(new ReplyLink(new User(reply.getAuthor()), new User(comment.getAuthor()), reply.getUTC()));
                    addLinks(reply);
                }));
        else ((Comment) post).getReplies().stream().filter(reply -> !reply.getAuthor().equals(post.getAuthor()))
                .forEach(reply -> {
                    linkRepo.save(new ReplyLink(new User(reply.getAuthor()), new User(post.getAuthor()), reply.getUTC()));
                    addLinks(reply);
                });
    }
}
