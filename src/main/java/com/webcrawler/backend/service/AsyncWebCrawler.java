package com.webcrawler.backend.service;

import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.ForkJoinPool.commonPool;

import com.webcrawler.backend.dto.Query;
import com.webcrawler.backend.repository.QueryRepository;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;

public class AsyncWebCrawler {
  private final ConcurrentLinkedQueue<String> visitedURLs;
  private final QueryRepository repository;
  private final String domain = System.getenv("BASE_URL");

  public AsyncWebCrawler(final QueryRepository repository) {
    this.visitedURLs = new ConcurrentLinkedQueue<>();
    this.repository = repository;
  }

  public void crawl(String rootUrl, String id, String keyword) {
    var query = Query.newQuery(id);
    if (repository.getQuery(id) == null) {
      repository.save(query);
    }
    if (visitedURLs.contains(rootUrl)) {
      return;
    }
    visitedURLs.add(rootUrl);
    final CompletableFuture<String> pageFuture =
        CompletableFuture.supplyAsync(() -> APIClient.doRequest(rootUrl));

    CompletableFuture<Void> combinedFuture = pageFuture
        .thenComposeAsync(containsKeyword(rootUrl, keyword, id))
        .thenApply(getLinks())
        .thenApply(filterLinks())
        .thenApply(crawlEach(keyword, id))
        .thenApply(futures -> futures.toArray(CompletableFuture[]::new))
        .thenAccept(CompletableFuture::allOf);
    combinedFuture.join();
    if (commonPool().isTerminated()) {
      repository.getQuery(id).complete();
    }
  }

  private Function<String, CompletionStage<String>> containsKeyword(
      final String rootUrl, final String keyword, final String id) {
    return htmlString -> {
      if (containsIgnoreCase(htmlString, keyword)) {
        repository.addUrlToQuery(id, rootUrl);
      }
      return CompletableFuture.completedFuture(htmlString);
    };
  }

  private Function<Set<String>, Stream<CompletableFuture<Void>>> crawlEach(final String keyword,
                                                                           String id) {
    return urls -> urls.parallelStream().map(url -> {
      crawl(url, id, keyword);
      return completedFuture(null);
    });
  }

  private Function<String, Set<String>> getLinks() {
    return page -> {
      Reader reader = new StringReader(page);
      HTMLEditorKit.Parser parser = new ParserDelegator();
      final Set<String> links = new HashSet<>();

      try {
        parser.parse(reader, new HTMLEditorKit.ParserCallback() {
          public void handleStartTag(HTML.Tag t, MutableAttributeSet a, int pos) {
            if (t == HTML.Tag.A) {
              Object link = a.getAttribute(HTML.Attribute.HREF);
              if (link != null) {
                links.add(String.valueOf(link));
              }
            }
          }
        }, true);
        reader.close();
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return links;
    };
  }

  private Function<Set<String>, Set<String>> filterLinks() {
    return urls -> urls.stream()
        .map(link -> link.startsWith("http") ? link : domain + link)
        .filter(link -> link.contains(domain))
        .filter(link -> !(link.contains(".pdf") || link.contains("video") || link.contains("mail")))
        .collect(Collectors.toSet());
  }

  private static boolean containsIgnoreCase(String str, String searchStr) {
    if (str == null || searchStr == null) {
      return false;
    }

    final int length = searchStr.length();
    if (length == 0) {
      return true;
    }

    for (int i = str.length() - length; i >= 0; i--) {
      if (str.regionMatches(true, i, searchStr, 0, length)) {
        return true;
      }
    }
    return false;
  }

}
