package io.versehub.infrastructure.common.utils;

import java.security.SecureRandom;

public class GenerateRandomString {
  static final String CHARACTERS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
  static SecureRandom rnd = new SecureRandom();

  public static String randomString(int len){
    StringBuilder sb = new StringBuilder(len);
    for(int i = 0; i < len; i++)
      sb.append(CHARACTERS.charAt(rnd.nextInt(CHARACTERS.length())));
    return sb.toString();
  }
}
