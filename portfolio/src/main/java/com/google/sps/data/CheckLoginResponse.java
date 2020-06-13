//Class for response objects from doGet /checklogin

package com.google.sps.data;

public final class CheckLoginResponse {
  private final Boolean loggedIn;
  private final String url; 
  private final String email;
  private final Boolean access;

  public CheckLoginResponse(Boolean loggedIn, String url, String email, Boolean access) {
    this.loggedIn = loggedIn;
    this.url = url;
    this.email = email; 
    this.access = access;
  }
} 
