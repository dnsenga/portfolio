package com.google.sps.data;

/** An individual comment. */
public final class Comment {

  public final long id;
  public final String name;
  public final String comment;
  public final long timestamp;

  public Comment(long id, String name, String comment, long timestamp) {
    this.id = id;
    this.name = name;
    this.comment = comment;
    this.timestamp = timestamp;
  }
}