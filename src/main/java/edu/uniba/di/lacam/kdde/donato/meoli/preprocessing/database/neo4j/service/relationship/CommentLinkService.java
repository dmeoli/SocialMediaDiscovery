package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.service.relationship;

import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Discussion;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongodb.domain.Post;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.node.User;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.domain.relationship.CommentLink;
import edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.neo4j.repository.relationship.ICommentLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentLinkService extends LinkService<CommentLink> {

    public CommentLinkService() { }

    @Autowired
    public CommentLinkService(ICommentLinkRepository commentLinkRepo) {
        super(commentLinkRepo);
    }

    @Override
    public void addLinks(Post post) {
        ((Discussion) post).getComments().stream().filter(comment ->
                !comment.getAuthor().equals(post.getAuthor())).forEach(comment -> linkRepo.save(
                        new CommentLink(new User(comment.getAuthor()), new User(post.getAuthor()), comment.getUTC())));
    }
}
