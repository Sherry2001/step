// Class for Recommendation Objects

package com.google.sps.data;

public final class Recommendation {
    private final long id;
    private final String name;
    private final String category;
    private final String content;
    private final String comment;

    public Recommendation(long id, String name, String category, String content,
                          String comment) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.content = content;
        this.comment = comment; 
    } 
}
