package com.webcrawler.backend.repository;

import com.webcrawler.backend.dto.Query;
import java.util.ArrayList;
import java.util.List;

public class QueryRepository {
  private static final List<Query> queries = new ArrayList<>();

  public void save(Query query) {
    queries.add(query);
  }

  public void addUrlToQuery(String id, String url) {
    getQuery(id).addUrl(url);
  }

  public Query getQuery(String id) {
    return queries
        .stream()
        .filter(x -> x.getId().equals(id))
        .findAny()
        .orElse(null);
  }
}
