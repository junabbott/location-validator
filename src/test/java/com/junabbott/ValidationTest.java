package com.junabbott;

import com.junabbott.Validation;
import com.junabbott.ValidationResult;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by junabbott on 7/15/17.
 */
public class ValidationTest {

  @Test
  public void testIsValidMaid() {
    assertFalse("Failed negative test- null input", Validation.isValidMaid(null));
    assertFalse("Failed negative test- invalid length", Validation.isValidMaid("09859C6b49dfC6d319279270d66d124")); // 31 characters
    assertFalse("Failed negative test- valid length but invalid value", Validation.isValidMaid("TZNPT6MM19NNDA4YKVZJHMRBXXP85UT9"));
    assertFalse("Failed negative test- mixed casing test", Validation.isValidMaid("09859C6b49dfC6d319279270d66d1482"));
    assertTrue("Failed positive test- valid input all lowercase", Validation.isValidMaid("09859c6b49dfc6d319279270d66d1482"));
    assertTrue("Failed positive test- valid input all uppercase", Validation.isValidMaid("09859C6B49DFC6D319279270D66D1482"));
  }

  @Test
  public void testIsValidTimestamp() {
    assertFalse("Failed negative test- null input", Validation.isValidTimestamp(null));
    assertFalse("Failed negative test- invalid millisecond", Validation.isValidTimestamp("946080000000"));
    assertFalse("Failed negative test- invalid datetime", Validation.isValidTimestamp("4-12-2017 ab:cd:ef"));
    assertFalse("Failed negative test- invalid datetime", Validation.isValidTimestamp("4 12 2017 1:1:1"));
    assertTrue("Failed positive test- valid millisecond", Validation.isValidTimestamp("1500151984000"));
    assertTrue("Failed positive test- valid datetime", Validation.isValidTimestamp("2017-07-15 14:00:00"));
  }

  @Test
  public void testIsValidLatitude() {
    assertFalse("Failed negative test- null input", Validation.isValidLatitude(null));
    assertFalse("Failed negative test- invalid latitude", Validation.isValidLatitude("100"));
    assertFalse("Failed negative test- invalid latitude", Validation.isValidLatitude("BAD"));
    assertTrue("Failed positive test- valid latitude", Validation.isValidLatitude("7.08435"));
    assertTrue("Failed positive test- valid latitude", Validation.isValidLatitude("7"));
    assertTrue("Failed positive test- valid latitude", Validation.isValidLatitude("-56.39101"));
    assertTrue("Failed positive test- valid latitude", Validation.isValidLatitude("-9.473821"));
  }

  @Test
  public void testIsValidLongitude() {
    assertFalse("Failed negative test- null input", Validation.isValidLongitude(null));
    assertFalse("Failed negative test- invalid longitude", Validation.isValidLongitude("200"));
    assertFalse("Failed negative test- invalid longitude", Validation.isValidLongitude("BAD"));
    assertTrue("Failed positive test- valid longitude", Validation.isValidLongitude("7.08435"));
    assertTrue("Failed positive test- valid longitude", Validation.isValidLongitude("7"));
    assertTrue("Failed positive test- valid longitude", Validation.isValidLongitude("7"));
    assertTrue("Failed positive test- valid longitude", Validation.isValidLongitude("-143.87009"));
    assertTrue("Failed positive test- valid longitude", Validation.isValidLongitude("50.62946"));
  }

  @Test
  public void testIsValidLocationMethod() {
    assertFalse("Failed negative test- null input", Validation.isValidLocationMethod(null));
    assertFalse("Failed negative test- invalid input", Validation.isValidLocationMethod("BAD METHOD"));
    assertTrue("Failed positive test- valid input", Validation.isValidLocationMethod("GPS"));
    assertTrue("Failed positive test- valid input", Validation.isValidLocationMethod("IP"));
    assertTrue("Failed positive test- valid input", Validation.isValidLocationMethod("CELL"));
  }

  @Test
  public void testIsValidLocationMode() {
    assertFalse("Failed negative test- null input", Validation.isValidLocationMode(null));
    assertFalse("Failed negative test- invalid input", Validation.isValidLocationMode("BAD MODE"));
    assertTrue("Failed positive test- valid input", Validation.isValidLocationMode("FOREGROUND"));
    assertTrue("Failed positive test- valid input", Validation.isValidLocationMode("BACKGROUND"));
  }

  @Test
  public void testIsValidHorizontalAccuracy() {
    assertFalse("Failed negative test- null input", Validation.isValidHorizontalAccuracy(null));
    assertFalse("Failed negative test- invalid input", Validation.isValidHorizontalAccuracy("-99"));
    assertTrue("Failed positive test- valid input", Validation.isValidHorizontalAccuracy("5.123"));
  }

  @Test
  public void testIsValidCountryCode() {
    assertFalse("Failed negative test- null input", Validation.isValidCountryCode(null));
    assertFalse("Failed negative test- invalid input", Validation.isValidCountryCode("USA"));
    assertTrue("Failed positive test- valid input", Validation.isValidCountryCode("US"));
  }

  @Test
  public void testIsValid() {
    String invalidData1 = null;
    String invalidData2 = "09859c6b49dfc6d319279270d66d1482,1500151984000,-56.39101,-143.87009,CELL,BACKGROUND";
    String invalidData3 = "09859c6b49dfc6d319279270d66d1482,1500151984000,-56.39101,-143.87009,CELL,BACKGROUND,127.0.0.1,5.123,US,OK";
    String invalidData4 = "09859c6b49dfc6d319279270d66d1482,bad_datetime,-56.39101,-143.87009,CELL,BACKGROUND,127.0.0.1";
    String invalidData5 = "bad_maid,bad_datetime,-56.39101,-143.87009,CELL,BACKGROUND,127.0.0.1";
    ValidationResult invalidResult1 = Validation.isValidData(invalidData1);
    ValidationResult invalidResult2 = Validation.isValidData(invalidData2);
    ValidationResult invalidResult3 = Validation.isValidData(invalidData3);
    ValidationResult invalidResult4 = Validation.isValidData(invalidData4);
    ValidationResult invalidResult5 = Validation.isValidData(invalidData5);
    assertFalse("Failed negative test- null input", invalidResult1.isValid());
    assertFalse("Failed negative test- invalid input length 6", invalidResult2.isValid());
    assertFalse("Failed negative test- invalid input length 10", invalidResult3.isValid());
    assertFalse("Failed negative test- invalid timestamp", invalidResult4.isValid());
    assertFalse("Failed negative test- invalid maid and timestamp", invalidResult5.isValid());

    String validData1 = "09859c6b49dfc6d319279270d66d1482,1500151984000,-56.39101,-143.87009,CELL,BACKGROUND,127.0.0.1,5.123,US";
    String validData2 = "09859c6b49dfc6d319279270d66d1482,1500151984000,-56.39101,-143.87009,CELL,BACKGROUND,127.0.0.1,US";
    String validData3 = "09859c6b49dfc6d319279270d66d1482,1500151984000,-56.39101,-143.87009,CELL,BACKGROUND,127.0.0.1,5.123";
    String validData4 = "09859c6b49dfc6d319279270d66d1482,1500151984000,-56.39101,-143.87009,CELL,BACKGROUND,127.0.0.1";
//    String validData5 = "C6BE6F325581EF622785B38B58FACB2D,1500162969,35.39142383902429,-168.48300306750485,GPS,foreground,58.219.142.108,4.8411568136098975,US";
    ValidationResult validResult1 = Validation.isValidData(validData1);
    ValidationResult validResult2 = Validation.isValidData(validData2);
    ValidationResult validResult3 = Validation.isValidData(validData3);
    ValidationResult validResult4 = Validation.isValidData(validData4);
//    com.junabbott.ValidationResult validResult5 = com.junabbott.Validation.isValidData(validData5);
    assertTrue("Failed positive test- valid data length 9", validResult1.isValid());
    assertTrue("Failed positive test- valid data length 8 with country code", validResult2.isValid());
    assertTrue("Failed positive test- valid data length 8 with horizontal accuracy", validResult3.isValid());
    assertTrue("Failed positive test- valid data length 7", validResult4.isValid());
//    assertTrue("Failed positive test- valid data", validResult5.isValid());
  }
}
