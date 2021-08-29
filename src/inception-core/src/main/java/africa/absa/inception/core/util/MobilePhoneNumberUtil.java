/*
 * Copyright 2021 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package africa.absa.inception.core.util;

import java.util.regex.Pattern;

/**
 * The <b>MobilePhoneNumberUtil</b> class is a utility class which provides methods for working with
 * mobile phone numbers and MSISDNs.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class MobilePhoneNumberUtil {

  /** The regular expression used to validate an MobilePhoneNumberUtil. */
  public static final String MSISDN_VALIDATION_REGEX =
      "(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|2[98654321]\\d|9[8543210]"
          + "|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1)\\d{1,14}$";

  private static final Pattern msisdnPattern = Pattern.compile(MSISDN_VALIDATION_REGEX);

  /**
   * Check whether the specified MobilePhoneNumberUtil is valid.
   *
   * @param msisdn the MobilePhoneNumberUtil
   * @return <b>true</b> if the MobilePhoneNumberUtil is valid or <b>false</b> otherwise
   */
  public static boolean isValidMSISDN(String msisdn) {
    java.util.regex.Matcher matcher = msisdnPattern.matcher(msisdn);

    return matcher.matches();
  }

  //  /**
  //   * Main.
  //   *
  //   * @param args the command line arguments
  //   */
  //  public static void main(String[] args)
  //  {
  //    System.out.println("+27 (0) 83 276 3107 = " + mobilePhoneNumberToMSISDN("+27 (0) 83 276
  // 3107",
  //        "27"));
  //
  //    System.out.println("0832763107          = " + mobilePhoneNumberToMSISDN("0832763107",
  // "27"));
  //
  //    System.out.println("083 276 3107        = " + mobilePhoneNumberToMSISDN("083 276 3107",
  // "27"));
  //
  //    System.out.println("083-276-3107        = " + mobilePhoneNumberToMSISDN("083-276-3107",
  // "27"));
  //
  //    System.out.println("+27832763107        = " + mobilePhoneNumberToMSISDN("+27832763107",
  // "27"));
  //
  //    System.out.println("+27 83 276 3107     = " + mobilePhoneNumberToMSISDN("+27 83 276 3107",
  //        "27"));
  //
  //    System.out.println("27832763107         = " + mobilePhoneNumberToMSISDN("27832763107",
  // "27"));
  //
  //    System.out.println("+36 55 002 709      = " + mobilePhoneNumberToMSISDN("+36 55 002 709",
  //        "27"));
  //
  //    System.out.println("+353 20 910 6402    = " + mobilePhoneNumberToMSISDN("+353 20 910 6402",
  //        "27"));
  //
  //    System.out.println("+1-202-555-0197     = " + mobilePhoneNumberToMSISDN("+1-202-555-0197",
  //        "27"));
  //
  //    System.out.println("+1-202-555-0197     = " + mobilePhoneNumberToMSISDN("+1-202-555-0197",
  //        "27"));
  //
  //    System.out.println("+27 (0) 83 276 3107 = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "+27 (0) 83 276 3107", "27")));
  //
  //    System.out.println("0832763107          = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "0832763107", "27")));
  //
  //    System.out.println("083 276 3107        = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "083 276 3107", "27")));
  //
  //    System.out.println("083-276-3107        = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "083-276-3107", "27")));
  //
  //    System.out.println("+27832763107        = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "+27832763107", "27")));
  //
  //    System.out.println("+27 83 276 3107     = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "+27 83 276 3107", "27")));
  //
  //    System.out.println("27832763107         = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "27832763107", "27")));
  //
  //    System.out.println("+36 55 002 709      = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "+36 55 002 709", "27")));
  //
  //    System.out.println("+353 20 910 6402    = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "+353 20 910 6402", "27")));
  //
  //    System.out.println("+1-202-555-0197     = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "+1-202-555-0197", "27")));
  //
  //    System.out.println("+1-202-555-0197     = " + isValidMSISDN(mobilePhoneNumberToMSISDN(
  //        "+1-202-555-0197", "27")));
  //  }

  /**
   * Returns the MobilePhoneNumberUtil for the specified mobile phone number.
   *
   * @param mobilePhoneNumber the mobile phone number
   * @param defaultInternationalPrefix the default international prefix
   * @return the MobilePhoneNumberUtil for the mobile phone number
   */
  public static String mobilePhoneNumberToMSISDN(
      String mobilePhoneNumber, String defaultInternationalPrefix) {
    // Remove any plus signs, white space (tabs and spaces) and dashes
    mobilePhoneNumber = mobilePhoneNumber.replaceAll("([+\\t -])", "");

    // Remove area code prefix e.g. the '(0)' in  +27(0)832763107
    mobilePhoneNumber = mobilePhoneNumber.replaceFirst("(\\([0-9]*\\))", "");

    // Ensure that the default international prefix is applied
    mobilePhoneNumber =
        mobilePhoneNumber.replaceFirst(
            "^(?:" + defaultInternationalPrefix + "|\\+" + defaultInternationalPrefix + "|0)",
            defaultInternationalPrefix);

    return mobilePhoneNumber;
  }
}
