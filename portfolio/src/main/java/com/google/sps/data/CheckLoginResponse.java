//Class for response objects from doGet /checklogin

package com.google.sps.data;

public final class CheckLoginResponse {
  private final boolean loggedIn;
  private final String url; 
  private final String email;
  private final boolean access;

  public CheckLoginResponse(boolean loggedIn, String url, String email, boolean access) {
    this.loggedIn = loggedIn;
    this.url = url;
    this.email = email; 
    this.access = access;
  }
} 
