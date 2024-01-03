package com.webcrawler.backend;

import static spark.Spark.get;
import static spark.Spark.post;

import com.webcrawler.backend.dto.QueryInput;
import com.webcrawler.backend.repository.QueryRepository;
import com.webcrawler.backend.service.WebCrawlerService;
import com.webcrawler.backend.util.IdUtils;
import com.google.gson.Gson;
import java.util.Collections;
import java.util.HashMap;

public class Main {
  private static final WebCrawlerService crawlerService = new WebCrawlerService();
  private static final QueryRepository repository = new QueryRepository();

  public static void main(String[] args) {
    get("/crawl/:id", (request, response) -> {
      response.type("application/json");
      var id = request.params(":id");
      if (repository.getQuery(id) != null) {
        return new Gson().toJson(repository.getQuery(id));
      }
      response.status(404);
      var error = new HashMap<Integer, String>();
      error.put(404, "crawl not found: " + id);
      return new Gson().toJson(error);
    });

    post("/crawl", (request, response) -> {
      response.status(200);
      response.type("application/json");
      QueryInput input = new Gson().fromJson(request.body(), QueryInput.class);
      final var keyword = input.keyword();
      if (keyword.length() < 4 || keyword.length() > 32) {
        response.status(400);
        var error = new HashMap<Integer, String>();
        error.put(400, "field 'keyword' is required (from 4 up to 32 chars)");
        return new Gson().toJson(error);
      }
      var id = IdUtils.generateID();
      crawlerService.createQuery(id, keyword);
      return new Gson().toJson(Collections.singletonMap("id", id));
    });

  }
}
