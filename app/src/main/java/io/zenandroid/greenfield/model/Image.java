package io.zenandroid.greenfield.model;

import java.util.Date;

/**
 * Created by alex on 24/01/2018.
 */

public class Image {
    private String title;
    private String link;
    private MediaLink media;
    private Date dateTaken;
    private String description;
    private Date published;
    private String author;
    private String authorId;
    private String tags;

    public MediaLink getMedia() {
        return media;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public Date getPublishedDate() {
        return published;
    }

    public String getDescription() {
        return description;
    }

    public String getTags() {
        return tags;
    }

    public Date getDateTaken() {
        return dateTaken;
    }

    public String getLink() {
        return link;
    }

}
