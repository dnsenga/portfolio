package com.google.sps.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/** An individual comment. */
public final class Comment {

  public final long id;
  public final String name;
  public final String comment;
  public final String sentimentScore;
  public final long timestamp;
  public final String convertedTime;

  public Comment(long id, String name, String comment, String sentimentScore, long timestamp) {
    this.id = id;
    this.name = name;
    this.comment = comment;
    this.sentimentScore = sentimentScore;
    this.timestamp = timestamp;

    Date dateTime = new Date(timestamp);
    DateFormat df = new SimpleDateFormat("dd:MM:yy:HH:mm:ss");
    this.convertedTime = df.format(dateTime);

  }
}