package com.webcrawler.backend.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.matchesPattern;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class IdUtilsTest {

  @Test
  void shouldCreateValidIds() {
    var idList = new ArrayList<String>();

    for (int i = 0; i < 5000; i++) {
      idList.add(IdUtils.generateID());
    }
    idList.forEach(x -> assertThat(x, matchesPattern("[a-zA-Z0-9]+$")));
  }

}