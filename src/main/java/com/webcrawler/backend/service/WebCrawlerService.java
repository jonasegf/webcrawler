package com.webcrawler.backend.service;

import com.webcrawler.backend.repository.QueryRepository;
import java.util.concurrent.CompletableFuture;

public class WebCrawlerService {
  private static final QueryRepository repository = new QueryRepository();
  private static final String rootUrl = System.getenv("BASE_URL");

  public void createQuery(String id, String keyword) {
    CompletableFuture.runAsync(() -> {
      try {
        new AsyncWebCrawler(repository).crawl(rootUrl, id, keyword);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    });
  }
}
