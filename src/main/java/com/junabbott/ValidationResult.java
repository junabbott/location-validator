package com.junabbott;

/**
 * Created by junabbott on 7/15/17.
 */
public class ValidationResult {
  private String row;
  private String maid;
  private boolean isValid;
  private InvalidReason[] reasons;

  enum InvalidReason {
    ROW_IS_NULL, INVALID_COL_COUNT, INVALID_MAID, INVALID_TIMESTAMP, INVALID_LATITUDE,
    INVALID_LONGITUDE, INVALID_LOC_METHOD, INVALID_LOC_MODE, INVALID_IP, INVALID_HOR_ACC, INVALID_COUNTRY_CODE
  }

  public ValidationResult(String row, String maid, boolean isValid, InvalidReason[] reasons) {
    this.row = row;
    this.maid = maid;
    this.isValid = isValid;
    this.reasons = reasons;
  }

  public String getRow() {
    return row;
  }

  public String getMaid() {
    return maid;
  }

  public boolean isIDFA() {
    if (maid == null) return false;
    return maid.matches("[0-9A-F]+");
  }

  public boolean isValid() {
    return isValid;
  }

  public InvalidReason[] getInvalidReasons() {
    return reasons;
  }
}
