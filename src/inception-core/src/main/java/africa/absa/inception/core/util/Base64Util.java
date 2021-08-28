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

import java.util.Optional;

/**
 * The <b>Base64Util</b> class provides a number of Base-64 related utility functions.
 *
 * @author Robert Harder
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class Base64Util {

  /** Indicates error in encoding. */
  public static final byte BAD_ENCODING;

  /** Specify that data should be compressed (value is <tt>true</tt>). */
  public static final boolean COMPRESS = true;

  /** Specify decoding (value is <tt>false</tt>). */
  public static final boolean DECODE = false;

  /** Specify that data should not be compressed (value is <tt>false</tt>). */
  public static final boolean DONT_COMPRESS = false;

  /** Specify encoding (value is <tt>true</tt>). */
  public static final boolean ENCODE = true;

  /** Indicates equals sign in encoding. */
  public static final byte EQUALS_SIGN_ENC;

  /** Indicates white space in encoding. */
  public static final byte WHITE_SPACE_ENC;

  /** The 64 valid Base64Util values. */
  private static final byte[] ALPHABET = {
    (byte) 'A',
    (byte) 'B',
    (byte) 'C',
    (byte) 'D',
    (byte) 'E',
    (byte) 'F',
    (byte) 'G',
    (byte) 'H',
    (byte) 'I',
    (byte) 'J',
    (byte) 'K',
    (byte) 'L',
    (byte) 'M',
    (byte) 'N',
    (byte) 'O',
    (byte) 'P',
    (byte) 'Q',
    (byte) 'R',
    (byte) 'S',
    (byte) 'T',
    (byte) 'U',
    (byte) 'V',
    (byte) 'W',
    (byte) 'X',
    (byte) 'Y',
    (byte) 'Z',
    (byte) 'a',
    (byte) 'b',
    (byte) 'c',
    (byte) 'd',
    (byte) 'e',
    (byte) 'f',
    (byte) 'g',
    (byte) 'h',
    (byte) 'i',
    (byte) 'j',
    (byte) 'k',
    (byte) 'l',
    (byte) 'm',
    (byte) 'n',
    (byte) 'o',
    (byte) 'p',
    (byte) 'q',
    (byte) 'r',
    (byte) 's',
    (byte) 't',
    (byte) 'u',
    (byte) 'v',
    (byte) 'w',
    (byte) 'x',
    (byte) 'y',
    (byte) 'z',
    (byte) '0',
    (byte) '1',
    (byte) '2',
    (byte) '3',
    (byte) '4',
    (byte) '5',
    (byte) '6',
    (byte) '7',
    (byte) '8',
    (byte) '9',
    (byte) '+',
    (byte) '/'
  };

  /**
   * Translates a Base64Util value to either its 6-bit reconstruction value or a negative number
   * indicating some other meaning.
   */
  private static final byte[] DECODABET = {
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,

    // Decimal  0 -  8
    -5,
    -5, // Whitespace: Tab and Linefeed
    -9,
    -9, // Decimal 11 - 12
    -5, // Whitespace: Carriage Return
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9, // Decimal 14 - 26
    -9,
    -9,
    -9,
    -9,
    -9, // Decimal 27 - 31
    -5, // Whitespace: Space
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9,
    -9, // Decimal 33 - 42
    62, // Plus sign at decimal 43
    -9,
    -9,
    -9, // Decimal 44 - 46
    63, // Slash at decimal 47
    52,
    53,
    54,
    55,
    56,
    57,
    58,
    59,
    60,
    61, // Numbers zero through nine
    -9,
    -9,
    -9, // Decimal 58 - 60
    -1, // Equals sign at decimal 61
    -9,
    -9,
    -9, // Decimal 62 - 64
    0,
    1,
    2,
    3,
    4,
    5,
    6,
    7,
    8,
    9,
    10,
    11,
    12,
    13, // Letters 'A' through 'N'
    14,
    15,
    16,
    17,
    18,
    19,
    20,
    21,
    22,
    23,
    24,
    25, // Letters 'O' through 'Z'
    -9,
    -9,
    -9,
    -9,
    -9,
    -9, // Decimal 91 - 96
    26,
    27,
    28,
    29,
    30,
    31,
    32,
    33,
    34,
    35,
    36,
    37,
    38, // Letters 'a' through 'm'
    39,
    40,
    41,
    42,
    43,
    44,
    45,
    46,
    47,
    48,
    49,
    50,
    51, // Letters 'n' through 'z'
    -9,
    -9,
    -9,
    -9 // Decimal 123 - 126

    /*
     * ,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 127 - 139
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 140 - 152
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 153 - 165
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 166 - 178
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 179 - 191
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 192 - 204
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 205 - 217
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 218 - 230
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,     // Decimal 231 - 243
     * -9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9,-9         // Decimal 244 - 255
     */
  };

  /** The equals sign (=) as a byte. */
  private static final byte EQUALS_SIGN = (byte) '=';

  /** Maximum line length (76) of Base64Util output. */
  private static final int MAX_LINE_LENGTH = 76;

  /** The new line character (\n) as a byte. */
  private static final byte NEW_LINE = (byte) '\n';

  static {
    BAD_ENCODING = -9;
    WHITE_SPACE_ENC = -5;
    EQUALS_SIGN_ENC = -1;
  }

  /** Defeats instantiation. */
  private Base64Util() {}

  /**
   * Decodes data from Base64Util notation.
   *
   * @param s the string to decode
   * @return the decoded data
   * @since 1.4
   */
  public static byte[] decode(String s) {
    byte[] bytes = s.getBytes();

    return decode(bytes, 0, bytes.length);
  }

  /**
   * Decodes Base64Util content in byte array format and returns the decoded byte array.
   *
   * @param source the Base64Util encoded data
   * @param off the offset of where to begin decoding
   * @param len the length of characters to decode
   * @return decoded data
   * @since 1.3
   */
  public static byte[] decode(byte[] source, int off, int len) {
    int len34 = len * 3 / 4;
    byte[] outBuff = new byte[len34]; // Upper limit on size of output
    int outBuffPosn = 0;
    byte[] b4 = new byte[4];
    int b4Posn = 0;
    int i;
    byte sbiCrop;
    byte sbiDecode;

    for (i = off; i < off + len; i++) {
      sbiCrop = (byte) (source[i] & 0x7f); // Only the low seven bits
      sbiDecode = DECODABET[sbiCrop];

      if (sbiDecode >= WHITE_SPACE_ENC) // White space, Equals sign or better
      {
        if (sbiDecode >= EQUALS_SIGN_ENC) {
          b4[b4Posn++] = sbiCrop;

          if (b4Posn > 3) {
            outBuffPosn += decode4to3(b4, 0, outBuff, outBuffPosn);
            b4Posn = 0;

            // If that was the equals sign, break out of 'for' loop
            if (sbiCrop == EQUALS_SIGN) {
              break;
            }
          }
        }
      } else {
        throw new Base64Exception(
            "Bad Base64Util input character at " + i + ": " + source[i] + "(decimal)");
      }
    }

    byte[] out = new byte[outBuffPosn];

    System.arraycopy(outBuff, 0, out, 0, outBuffPosn);

    return out;
  }

  /**
   * Attempts to decode Base64Util data and deserialize a Java Object within. Returns <tt>null if
   * there was an error.
   *
   * @param encodedObject the Base64Util data to decode
   * @return an Optional containing the decoded and deserialized object or an empty Optional if
   *     there was an error
   * @since 1.4
   */
  public static Optional<Object> decodeToObject(String encodedObject) {
    byte[] objBytes = decode(encodedObject);

    try (java.io.ObjectInputStream ois =
        new java.io.ObjectInputStream(new java.io.ByteArrayInputStream(objBytes))) {

      return Optional.of(ois.readObject());
    } catch (Throwable e) {
      return Optional.empty();
    }
  }

  /**
   * Decodes data from Base64Util notation and returns it as a string. Equivlaent to calling <b> new
   * String(decode(s))</b>
   *
   * @param s the string to decode
   * @return the data as a string
   * @since 1.4
   */
  public static String decodeToString(String s) {
    return new String(decode(s));
  }

  /**
   * Encodes a byte array into Base64Util notation. Equivalent to calling <b>encodeBytes(source, 0,
   * source.length)</b>
   *
   * @param source the data to convert
   * @return the Base64Util encoded string
   * @since 1.4
   */
  public static String encodeBytes(byte[] source) {
    return encodeBytes(source, true);
  }

  /**
   * Encodes a byte array into Base64Util notation. Equivalent to calling <b>encodeBytes(source, 0,
   * source.length)</b>
   *
   * @param source the data to convert
   * @param breakLines break lines at 80 characters or less
   * @return the Base64Util encoded bytes
   * @since 1.4
   */
  public static String encodeBytes(byte[] source, boolean breakLines) {
    return encodeBytes(source, 0, source.length, breakLines);
  }

  /**
   * Encodes a byte array into Base64Util notation.
   *
   * @param source the data to convert
   * @param off offset in array where conversion should begin
   * @param len length of data to convert
   * @return the Base64Util encoded bytes
   * @since 1.4
   */
  public static String encodeBytes(byte[] source, int off, int len) {
    return encodeBytes(source, off, len, true);
  }

  /**
   * Encodes a byte array into Base64Util notation.
   *
   * @param source the data to convert
   * @param off offset in array where conversion should begin
   * @param len length of data to convert
   * @param breakLines break lines at 80 characters or less
   * @return the Base64Util encoded bytes
   * @since 1.4
   */
  public static String encodeBytes(byte[] source, int off, int len, boolean breakLines) {
    int len43 = len * 4 / 3;
    byte[] outBuff =
        new byte
            [(len43) // Main 4:3
                + ((len % 3) > 0 ? 4 : 0) // Account for padding
                + (breakLines ? (len43 / MAX_LINE_LENGTH) : 0)]; // New lines
    int d = 0;
    int e = 0;
    int len2 = len - 2;
    int lineLength = 0;

    for (; d < len2; d += 3, e += 4) {
      encode3to4(source, d + off, 3, outBuff, e);
      lineLength += 4;

      if (breakLines && (lineLength == MAX_LINE_LENGTH)) {
        outBuff[e + 4] = NEW_LINE;
        e++;
        lineLength = 0;
      }
    }

    if (d < len) {
      encode3to4(source, d + off, len - d, outBuff, e);
      e += 4;
    }

    return new String(outBuff, 0, e);
  }

  /**
   * Encodes a string in Base64Util notation with line breaks after every 75 Base64Util characters.
   * Of course you probably only need to encode a string if there are non-ASCII characters in it
   * such as many non-English languages.
   *
   * @param s the string to encode
   * @return the encoded string
   * @since 1.3
   */
  public static String encodeString(String s) {
    return encodeString(s, true);
  }

  /**
   * Encodes a string in Base64Util notation with line breaks after every 75 Base64Util characters.
   * Of course you probably only need to encode a string if there are non-ASCII characters in it
   * such as many non-English languages.
   *
   * @param s the string to encode
   * @param breakLines break lines at 80 characters or less
   * @return the encoded string
   * @since 1.3
   */
  public static String encodeString(String s, boolean breakLines) {
    return encodeBytes(s.getBytes(), breakLines);
  }

  /**
   * Decodes four bytes from array <var>source</var> and writes the resulting bytes (up to three of
   * them) to <var>destination</var>. The source and destination arrays can be manipulated anywhere
   * along their length by specifying <var>srcOffset</var> and <var>destOffset</var>. This method
   * does not check to make sure your arrays are large enough to accomodate <var>srcOffset</var> + 4
   * for the <var>source</var> array or <var>destOffset</var> + 3 for the <var>destination</var>
   * array.
   *
   * <p>This method returns the actual number of bytes that were converted from the Base64Util
   * encoding.
   *
   * @param source the array to convert
   * @param srcOffset the index where conversion begins
   * @param destination the array to hold the conversion
   * @param destOffset the index where output will be put
   * @return the number of decoded bytes converted
   * @since 1.3
   */
  private static int decode4to3(byte[] source, int srcOffset, byte[] destination, int destOffset) {
    // Example: Dk==
    if (source[srcOffset + 2] == EQUALS_SIGN) {
      // Two ways to do the same thing. Don't know which way I like best.
      // int outBuff =   ((DECODABET[ source[ srcOffset    ] ] << 24) >>>  6)
      // | ((DECODABET[ source[ srcOffset + 1] ] << 24) >>> 12);
      int outBuff =
          ((DECODABET[source[srcOffset]] & 0xFF) << 18)
              | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12);

      destination[destOffset] = (byte) (outBuff >>> 16);

      return 1;
    }

    // Example: DkL=
    else if (source[srcOffset + 3] == EQUALS_SIGN) {
      // Two ways to do the same thing. Don't know which way I like best.
      // int outBuff =   ((DECODABET[ source[ srcOffset     ] ] << 24) >>>  6)
      // | ((DECODABET[ source[ srcOffset + 1 ] ] << 24) >>> 12)
      // | ((DECODABET[ source[ srcOffset + 2 ] ] << 24) >>> 18);
      int outBuff =
          ((DECODABET[source[srcOffset]] & 0xFF) << 18)
              | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
              | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6);

      destination[destOffset] = (byte) (outBuff >>> 16);
      destination[destOffset + 1] = (byte) (outBuff >>> 8);

      return 2;
    }

    // Example: DkLE
    else {
      try {
        // Two ways to do the same thing. Don't know which way I like best.
        // int outBuff =   ((DECODABET[ source[ srcOffset     ] ] << 24) >>>  6)
        // | ((DECODABET[ source[ srcOffset + 1 ] ] << 24) >>> 12)
        // | ((DECODABET[ source[ srcOffset + 2 ] ] << 24) >>> 18)
        // | ((DECODABET[ source[ srcOffset + 3 ] ] << 24) >>> 24);
        int outBuff =
            ((DECODABET[source[srcOffset]] & 0xFF) << 18)
                | ((DECODABET[source[srcOffset + 1]] & 0xFF) << 12)
                | ((DECODABET[source[srcOffset + 2]] & 0xFF) << 6)
                | ((DECODABET[source[srcOffset + 3]] & 0xFF));

        destination[destOffset] = (byte) (outBuff >> 16);
        destination[destOffset + 1] = (byte) (outBuff >> 8);
        destination[destOffset + 2] = (byte) (outBuff);

        return 3;
      } catch (Exception e) {
        System.out.println("" + source[srcOffset] + ": " + (DECODABET[source[srcOffset]]));
        System.out.println("" + source[srcOffset + 1] + ": " + (DECODABET[source[srcOffset + 1]]));
        System.out.println("" + source[srcOffset + 2] + ": " + (DECODABET[source[srcOffset + 2]]));
        System.out.println("" + source[srcOffset + 3] + ": " + (DECODABET[source[srcOffset + 3]]));

        return -1;
      }
    }
  }

  /**
   * Encodes up to three bytes of the array <var>source</var> and writes the resulting four
   * Base64Util bytes to <var>destination</var>. The source and destination arrays can be
   * manipulated anywhere along their length by specifying <var>srcOffset</var> and
   * <var>destOffset</var>. This method does not check to make sure your arrays are large enough to
   * accomodate <var>srcOffset</var> + 3 for the <var>source</var> array or <var>destOffset</var> +
   * 4 for the <var>destination</var> array. The actual number of significant bytes in your array is
   * given by <var>numSigBytes</var>.
   *
   * @param source the array to convert
   * @param srcOffset the index where conversion begins
   * @param numSigBytes the number of significant bytes in your array
   * @param destination the array to hold the conversion
   * @param destOffset the index where output will be put
   * @return the <var>destination</var> array
   * @since 1.3
   */
  private static byte[] encode3to4(
      byte[] source, int srcOffset, int numSigBytes, byte[] destination, int destOffset) {
    // 1         2         3
    // 01234567890123456789012345678901 Bit position
    // --------000000001111111122222222 Array position from threeBytes
    // --------|    ||    ||    ||    | Six bit groups to index ALPHABET
    // >>18  >>12  >> 6  >> 0  Right shift necessary
    // 0x3f  0x3f  0x3f  Additional AND
    // Create _buffer with zero-padding if there are only one or two
    // significant bytes passed in the array.
    // We have to shift left 24 in order to flush out the 1's that appear
    // when Java treats a value as negative that is cast from a byte to an int.
    int inBuff =
        ((numSigBytes > 0) ? ((source[srcOffset] << 24) >>> 8) : 0)
            | ((numSigBytes > 1) ? ((source[srcOffset + 1] << 24) >>> 16) : 0)
            | ((numSigBytes > 2) ? ((source[srcOffset + 2] << 24) >>> 24) : 0);

    switch (numSigBytes) {
      case 3:
        destination[destOffset] = ALPHABET[(inBuff >>> 18)];
        destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
        destination[destOffset + 3] = ALPHABET[(inBuff) & 0x3f];

        return destination;

      case 2:
        destination[destOffset] = ALPHABET[(inBuff >>> 18)];
        destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = ALPHABET[(inBuff >>> 6) & 0x3f];
        destination[destOffset + 3] = EQUALS_SIGN;

        return destination;

      case 1:
        destination[destOffset] = ALPHABET[(inBuff >>> 18)];
        destination[destOffset + 1] = ALPHABET[(inBuff >>> 12) & 0x3f];
        destination[destOffset + 2] = EQUALS_SIGN;
        destination[destOffset + 3] = EQUALS_SIGN;

        return destination;

      default:
        return destination;
    }
  }
}
