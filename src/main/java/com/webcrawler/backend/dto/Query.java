package com.webcrawler.backend.dto;

import com.webcrawler.backend.Status;
import java.util.ArrayList;
import java.util.List;

public class Query {
  private String id;
  private Status status;
  private List<String> urls;

  public static Query newQuery(String id) {
    final var query = new Query();
    query.setId(id);
    query.setStatus(Status.active);
    query.urls = new ArrayList<>();
    return query;
  }

  public void complete() {
    this.setStatus(Status.done);
  }

  public void addUrl(String url) {
    this.urls.add(url);
  }

  public String getId() {
    return id;
  }

  public void setId(final String id) {
    this.id = id;
  }


  public void setStatus(final Status status) {
    this.status = status;
  }
}


