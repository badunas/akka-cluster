package com.badun.akkaclusterdemo.message;

import java.util.Date;

/**
 * Created by Artsiom Badun.
 */
public class PieceOfWork {
    private final String sender;
    private final Date creationDate;
    private final String message;

    public PieceOfWork(String sender, Date creationDate, String message) {
        this.sender = sender;
        this.creationDate = creationDate;
        this.message = message;
    }

    public String getSender() {
        return sender;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public String getMessage() {
        return message;
    }
}
