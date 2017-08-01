package com.junabbott;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Random;

/**
 * Created by junabbott on 7/10/17.
 */
public class Utils {
  private Random r = new Random();

  private String getRandomHexString(int numChars) {
    StringBuffer sb = new StringBuffer();
    while(sb.length() < numChars){
      sb.append(Integer.toHexString(r.nextInt()));
    }
    return sb.toString().substring(0, numChars);
  }

  private String getRandomMAID() {
    if (r.nextBoolean()) {
      return getRandomHexString(32).toLowerCase();
    } else {
      return getRandomHexString(32).toUpperCase();
    }
  }

  private String getRandomTimestamp() {
    LocalDateTime time = LocalDateTime.now();
    ZonedDateTime zTime = ZonedDateTime.of(time, ZoneId.of("America/Los_Angeles"));
    if (r.nextBoolean()) {
      return String.valueOf(zTime.toEpochSecond()) + "000";
    } else {
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
      return zTime.format(formatter);
    }
  }

  private String getRandomLocationMethod() {
    int number = r.nextInt(3);
    String locationMethod = "";
    switch (number) {
      case 0:
        locationMethod = "GPS";
        break;
      case 1:
        locationMethod = "IP";
        break;
      case 2:
        locationMethod = "CELL";
        break;
    }
    return locationMethod;
  }

  private String getRandomLocationMode() {
    if (r.nextBoolean()) {
      return "FOREGROUND";
    } else {
      return "BACKGROUND";
    }
  }

  private String getRandomCoordinate() {
    return String.valueOf(r.nextInt(200) * r.nextDouble() * (r.nextBoolean() ? 1 : -1));
  }

  private String getRandomIP() {
    return r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256) + "." + r.nextInt(256);
  }

  private String getRandomHorizontalAccuracy() {
    return String.valueOf(r.nextInt(100) + r.nextDouble());
  }

  private String getRandomCountryCode() {
    if (r.nextBoolean()) {
      return "US";
    } else {
      String[] countries = Locale.getISOCountries();
      return countries[r.nextInt(1000) % countries.length];
    }
  }

  public String getRandomRow() {
    StringBuilder sb = new StringBuilder();
    sb.append(getRandomMAID() + ",");
    sb.append(getRandomTimestamp() + ",");
    sb.append(getRandomCoordinate() + ",");
    sb.append(getRandomCoordinate() + ",");
    sb.append(getRandomLocationMethod() +  ",");
    sb.append(getRandomLocationMode() + ",");
    sb.append(getRandomIP() + ",");
    sb.append(getRandomHorizontalAccuracy() + ",");
    sb.append(getRandomCountryCode() + "\n");
    return sb.toString();
  }
}
