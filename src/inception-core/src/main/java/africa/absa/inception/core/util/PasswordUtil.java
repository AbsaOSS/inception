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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The <b>PasswordUtil</b> class is a utility class which provides password-related utility methods.
 *
 * @author Marcus Portmann
 */
public class PasswordUtil {

  /** Special characters allowed in password. */
  private static final String ALLOWED_SPL_CHARACTERS = "!@#$%^&*()_+";

  /** The secure random number generator. */
  private static final Random random = new SecureRandom();

  /**
   * Create the SHA-512 hash of the specified password.
   *
   * @param password the password to hash
   * @return the SHA-512 hash of the password
   */
  public static final String createPasswordHash(String password) {
    try {
      MessageDigest md = MessageDigest.getInstance("SHA-512");

      md.update(password.getBytes(StandardCharsets.ISO_8859_1), 0, password.length());

      return Base64Util.encodeBytes(md.digest());
    } catch (Throwable e) {
      throw new RuntimeException(
          String.format("Failed to generate a SHA-512 hash of the password (%s)", password), e);
    }
  }

  /**
   * Generate a random password.
   *
   * @return the random password
   */
  public static String generateRandomPassword() {
    Stream<Character> pwdStream =
        Stream.concat(
            getRandomNumbers(2),
            Stream.concat(
                getRandomSpecialChars(2),
                Stream.concat(getRandomAlphabets(2, true), getRandomAlphabets(4, false))));
    List<Character> charList = pwdStream.collect(Collectors.toList());
    Collections.shuffle(charList);

    String password =
        charList.stream()
            .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
            .toString();

    return password;
  }

  private static Stream<Character> getRandomAlphabets(int count, boolean upperCase) {
    IntStream characters = null;
    if (upperCase) {
      characters = random.ints(count, 65, 90);
    } else {
      characters = random.ints(count, 97, 122);
    }

    return characters.mapToObj(data -> (char) data);
  }

  private static Stream<Character> getRandomNumbers(int count) {
    IntStream numbers = random.ints(count, 48, 57);

    return numbers.mapToObj(data -> (char) data);
  }

  private static Stream<Character> getRandomSpecialChars(int count) {
    IntStream specialChars = random.ints(count, 33, 45);

    return specialChars.mapToObj(data -> (char) data);
  }
}
