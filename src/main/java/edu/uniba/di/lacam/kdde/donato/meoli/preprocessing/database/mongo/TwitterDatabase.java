package edu.uniba.di.lacam.kdde.donato.meoli.preprocessing.database.mongo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Component;

@Component
public class TwitterDatabase extends SocialMediaDatabase {

    public TwitterDatabase() { }

    @Autowired
    public TwitterDatabase(GridFsTemplate socialMediaCollection) {
        super(socialMediaCollection);
    }

    @Override
    public void createSocialMediaDatabase() { }
}
