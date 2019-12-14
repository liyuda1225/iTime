package com.example.itime;

import java.io.Serializable;

public class Item_list_view {
    private int CoverResourceId;
    private String title;
    private String message;

    Item_list_view(String title, String message, int coverResourceId) {
        setTitle(title);
        setMessage (message);
        setCoverResourceId(coverResourceId);
    }

    int getCoverResourceId() {
        return CoverResourceId;
    }

    void setCoverResourceId(int coverResourceId) {
        CoverResourceId = coverResourceId;
    }

    String getTitle() {
        return title;
    }

    void setTitle(String title) {
        this.title = title;
    }

    String getMessage() {
        return message;
    }

    void setMessage(String message) {
        this.message = message;
    }
}
