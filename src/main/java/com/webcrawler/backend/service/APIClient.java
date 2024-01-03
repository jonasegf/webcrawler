package com.webcrawler.backend.service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class APIClient {
  static final HttpClient httpClient = HttpClient.newHttpClient();

  public static String doRequest(final String url) {
    try {
      URI uri = new URI(url);
      HttpRequest request = HttpRequest.newBuilder()
          .uri(uri)
          .GET()
          .build();
      System.out.println("Requesting: " + uri);
      return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
    } catch (IOException | InterruptedException | URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

}
