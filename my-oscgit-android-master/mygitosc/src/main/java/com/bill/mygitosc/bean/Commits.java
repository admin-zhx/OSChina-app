package com.bill.mygitosc.bean;

import java.io.Serializable;

/**
 * Created by liaobb on 2015/7/28.
 */
public class Commits implements Serializable {
    private String id;
    private String message;
    private String timestamp;
    private Author author;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }
}
