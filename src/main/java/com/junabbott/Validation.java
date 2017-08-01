package com.junabbott;

import org.apache.commons.validator.routines.InetAddressValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by junabbott on 7/15/17.
 */
public class Validation {

  public static boolean isValidMaid(String maid) {
    if (maid == null) return false;
    return maid != null && maid.length() == 32 && maid.matches("[0-9A-F]+|[0-9a-f]+");
  }

  public static boolean isValidTimestamp(String timestamp) {
    if (timestamp == null) return false;
    if (timestamp.matches("\\d+")) {
      return isValidMilliTimestamp(timestamp);
    } else {
      return isValidStringTimestamp(timestamp);
    }
  }

  public static boolean isValidMilliTimestamp(String timestamp) {
    long t;
    try {
      t = Long.parseLong(timestamp);
    } catch (NumberFormatException e) {
      return false;
    }
    return t >= 946684800000l; // has to be greater than 1/1/2000 00:00:00 GMT which is well before maid
  }

  //todo optimize validation (slowest validation)
  public static boolean isValidStringTimestamp(String timestamp) {
    if (timestamp == null) return false;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");

    try {
      Date date = sdf.parse(timestamp);
    } catch (ParseException e) {
      return false;
    }
    return true;
  }

  public static boolean isValidLatitude(String latitude) {
    return latitude != null && latitude.matches("^[-+]?([1-8]?\\d(\\.\\d+)?|90(\\.0+)?)$");
  }

  public static boolean isValidLongitude(String longitude) {
    return longitude != null && longitude.matches("^[-+]?(180(\\.0+)?|((1[0-7]\\d)|([1-9]?\\d))(\\.\\d+)?)$");
  }

  //todo make it case insensitive configurable
  public static boolean isValidLocationMethod(String locationMethod) {
    if (locationMethod == null) return false;

    String upperMethod = locationMethod.toUpperCase();
    return upperMethod.equals("GPS") || upperMethod.equals("IP") || upperMethod.equals("CELL");
  }

  //todo make it case insensitive configurable
  public static boolean isValidLocationMode(String locationMode) {
    if (locationMode == null) return false;

    String upperMode = locationMode.toUpperCase();
    return locationMode.equals("FOREGROUND") || locationMode.equals("BACKGROUND");
  }

  public static boolean isValidIpAddress(String ip) {
    return InetAddressValidator.getInstance().isValid(ip);
  }

  public static boolean isValidHorizontalAccuracy(String accuracy) {
    if (accuracy == null) return false;
    double acc;
    try {
      acc = Double.parseDouble(accuracy);
    } catch (NumberFormatException e) {
      return false;
    }
    return acc >= 0; // make sure accuracy is positive or zero
  }

  //todo make it case insensitive configurable
  public static boolean isValidCountryCode(String code) {
    if (code == null) return false;

    String upperCode = code.toUpperCase();
    return upperCode.equals("US");
  }

  // todo configurable delimiter
  public static ValidationResult isValidData(String row) {
    boolean isValid = true;
    String maid = null;
    ArrayList<ValidationResult.InvalidReason> invalidReasons = new ArrayList<>();
    if (row == null) {
      isValid = false;
      invalidReasons.add(ValidationResult.InvalidReason.ROW_IS_NULL);
    } else {
      String[] splits = row.split(",");
      if (splits.length >= 7 && splits.length <= 9) {
        if (!isValidMaid(splits[0])) {
          isValid = false;
          invalidReasons.add(ValidationResult.InvalidReason.INVALID_MAID);
        } else {
          maid = splits[0];
        }
        if (!isValidTimestamp(splits[1])) {
          isValid = false;
          invalidReasons.add(ValidationResult.InvalidReason.INVALID_TIMESTAMP);
        }
        if (!isValidLatitude(splits[2])) {
          isValid = false;
          invalidReasons.add(ValidationResult.InvalidReason.INVALID_LATITUDE);
        }
        if (!isValidLongitude(splits[3])) {
          isValid = false;
          invalidReasons.add(ValidationResult.InvalidReason.INVALID_LONGITUDE);
        }
        if (!isValidLocationMethod(splits[4])) {
          isValid = false;
          invalidReasons.add(ValidationResult.InvalidReason.INVALID_LOC_METHOD);
        }
        if (!isValidLocationMode(splits[5])) {
          isValid = false;
          invalidReasons.add(ValidationResult.InvalidReason.INVALID_LOC_MODE);
        }
        if (!isValidIpAddress(splits[6])) {
          isValid = false;
          invalidReasons.add(ValidationResult.InvalidReason.INVALID_IP);
        }
        if (splits.length == 8) {
          if (splits[7].matches("[a-zA-Z]+")) {
            if (!isValidCountryCode(splits[7])) {
              isValid = false;
              invalidReasons.add(ValidationResult.InvalidReason.INVALID_COUNTRY_CODE);
            }
          } else {
            if (!isValidHorizontalAccuracy(splits[7])) {
              isValid = false;
              invalidReasons.add(ValidationResult.InvalidReason.INVALID_HOR_ACC);
            }
          }
        }
        if (splits.length == 9) {
          if (!isValidHorizontalAccuracy(splits[7])) {
            isValid = false;
            invalidReasons.add(ValidationResult.InvalidReason.INVALID_HOR_ACC);
          }
          if (!isValidCountryCode(splits[8])) {
            isValid = false;
            invalidReasons.add(ValidationResult.InvalidReason.INVALID_COUNTRY_CODE);
          }
        }
      } else {
        isValid = false;
        invalidReasons.add(ValidationResult.InvalidReason.INVALID_COL_COUNT);
      }
    }
    return new ValidationResult(row, maid, isValid, invalidReasons.toArray(new ValidationResult.InvalidReason[invalidReasons.size()]));
  }
}
