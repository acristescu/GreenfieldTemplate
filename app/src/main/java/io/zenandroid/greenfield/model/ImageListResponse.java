package io.zenandroid.greenfield.model;

import java.util.Date;
import java.util.List;

/**
 * Created by alex on 24/01/2018.
 */

public class ImageListResponse {
    private String title;
    private String link;
    private String description;
    private Date modified;
    private String generator;
    private List<Image> items;

    public List<Image> getItems() {
        return items;
    }

    public void setItems(List<Image> items) {
        this.items = items;
    }
}
