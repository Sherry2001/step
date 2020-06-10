// Class for Recommendation Objects

package com.google.sps.data;

public final class Recommendation {
  private final long id;
  private final String name;
  private final String category;
  private final String content;
  private final String comment;
  private final String imageUrl; 

  public Recommendation(long id, String name, String category, String content,
                          String comment, String imageUrl) {
    this.id = id;
    this.name = name;
    this.category = category;
    this.content = content;
    this.comment = comment; 
    this.imageUrl = imageUrl;
  } 
}
