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

import java.security.SecureRandom;
import java.util.Objects;
import java.util.Random;

/**
 * The <b>RandomStringGenerator</b> class is a utility class that provides the capability to
 * generate random alphanumeric strings.
 *
 * @author Marcus Portmann
 */
public class RandomStringGenerator {

  private static final String DIGITS = "0123456789";

  private static final String UPPER_CASE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

  private static final String LOWER_CASE_CHARACTERS = UPPER_CASE_CHARACTERS.toLowerCase();

  private static final String ALPHANUMERIC = UPPER_CASE_CHARACTERS + LOWER_CASE_CHARACTERS + DIGITS;

  private final char[] buf;

  private final Random random;

  private final char[] symbols;

  /**
   * Constructs a new <b>RandomStringGenerator</b> that can be used to generate session identifiers.
   */
  public RandomStringGenerator() {
    this(21);
  }

  /**
   * Constructs a new <b>RandomStringGenerator</b>.
   *
   * @param length the length of the random strings that should be generated
   */
  public RandomStringGenerator(int length) {
    this(length, new SecureRandom());
  }

  /**
   * Constructs a new <b>RandomStringGenerator</b>.
   *
   * @param length the length of the random strings that should be generated
   * @param random the random number generator
   */
  public RandomStringGenerator(int length, Random random) {
    this(length, random, ALPHANUMERIC);
  }

  /**
   * Constructs a new <b>RandomStringGenerator</b>.
   *
   * @param length the length of the random strings that should be generated
   * @param random the random number generator
   * @param symbols the symbols to use when generating the random string
   */
  public RandomStringGenerator(int length, Random random, String symbols) {
    if (length < 1) {
      throw new IllegalArgumentException();
    }

    if (symbols.length() < 2) {
      throw new IllegalArgumentException();
    }

    this.random = Objects.requireNonNull(random);
    this.symbols = symbols.toCharArray();
    this.buf = new char[length];
  }

  /**
   * Generate the next random alphanumeric string.
   *
   * @return the next random alphanumeric string
   */
  public String nextString() {
    for (int idx = 0; idx < buf.length; ++idx) {
      buf[idx] = symbols[random.nextInt(symbols.length)];
    }

    return new String(buf);
  }
}
