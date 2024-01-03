package com.webcrawler.backend.util;

import java.util.Random;

public class IdUtils {
  private static final Random random = new Random();

  public static String generateID() {
    int targetStringLength = 10;
    int rightLimit = 122;
    int leftLimit = 48;
    return random.ints(leftLimit, rightLimit + 1)
        .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
        .limit(targetStringLength)
        .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
        .toString();
  }
}
