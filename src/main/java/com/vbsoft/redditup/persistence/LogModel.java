package com.vbsoft.redditup.persistence;

import lombok.Data;

import java.util.Date;

@Data
public class LogModel {

    private String post;
    private boolean success;
    private String logRef;
    private String description;
    private String upCount = "0";
    private Date creationDate = new Date();

}
