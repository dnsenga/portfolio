package com.google.sps.data;

/** An individual comment. */
public final class Comment {

  public final long id;
  public final String name;
  public final String email;
  public final String comment;
  public final float sentimentScore;
  public final long timestamp;

  public Comment(long id, String name, String email, String comment, float sentimentScore, long timestamp) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.comment = comment;
    this.sentimentScore = sentimentScore;
    this.timestamp = timestamp;
  }
}