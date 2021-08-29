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

import java.io.InputStream;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPrivateCrtKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.UUID;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;

/**
 * The <b>CryptoUtil</b> class provides a number of cryptography related utility functions.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class CryptoUtil {

  /** The AES block size. */
  public static final int AES_BLOCK_SIZE = 16;

  /** The AES key size. */
  public static final int AES_KEY_SIZE = 32;

  /** The AES key specification. */
  public static final String AES_KEY_SPEC = "AES";

  /** The AES tranformation name. */
  public static final String AES_TRANSFORMATION_NAME = "AES/CFB8/NoPadding";

  private static final SecureRandom secureRandom = new SecureRandom();

  /**
   * Creates a random encryption initialization vector with the specified length.
   *
   * @param length the length of the random encryption initialization vector
   * @return the random encryption initialization vector
   */
  public static byte[] createRandomEncryptionIV(int length) {
    byte[] encryptionIV = new byte[length];

    secureRandom.nextBytes(encryptionIV);

    return encryptionIV;
  }

  /**
   * Retrieve the key pair with the specified alias from the key store.
   *
   * @param keyStore the key store
   * @param alias the alias for the key pair
   * @param password the password for the key
   * @return the key pair
   * @throws GeneralSecurityException if the key pair could not be retrieved
   */
  public static KeyPair getKeyPair(KeyStore keyStore, String alias, String password)
      throws GeneralSecurityException {
    try {
      RSAPrivateCrtKey key = (RSAPrivateCrtKey) keyStore.getKey(alias, password.toCharArray());
      RSAPublicKeySpec spec = new RSAPublicKeySpec(key.getModulus(), key.getPublicExponent());
      PublicKey publicKey = KeyFactory.getInstance("RSA").generatePublic(spec);

      return new KeyPair(publicKey, key);
    } catch (Throwable e) {
      throw new GeneralSecurityException(
          "Failed to load the key pair (" + alias + ") from the key store", e);
    }
  }

  /**
   * Returns a randomly generated AES key.
   *
   * @return a randomly generated AES key
   */
  public static byte[] getRandomAESKey() {
    String randomPassword = new BigInteger(130, secureRandom).toString(32);

    return CryptoUtil.passwordToAESKey(randomPassword, UUID.randomUUID().toString());
  }

  /**
   * Load a key store.
   *
   * @param type the key store type e.g. JKS, PKCS12, etc
   * @param path the path to the key store
   * @param password the key store password
   * @return the key store that was loaded
   * @throws GeneralSecurityException if the key store could not be loaded
   */
  public static KeyStore loadKeyStore(String type, String path, String password)
      throws GeneralSecurityException {
    InputStream input = null;

    try {
      PathMatchingResourcePatternResolver resourceLoader =
          new PathMatchingResourcePatternResolver();

      Resource keyStoreResource = resourceLoader.getResource(path);

      if (!keyStoreResource.exists()) {
        throw new GeneralSecurityException("The key store (" + path + ") could not be found");
      }

      KeyStore ks = KeyStore.getInstance(type);

      input = keyStoreResource.getInputStream();

      ks.load(
          input,
          ((password == null) || (password.length() == 0)) ? new char[0] : password.toCharArray());

      return ks;
    } catch (Throwable e) {
      throw new GeneralSecurityException("Failed to load the key store (" + path + ")", e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (Throwable ignored) {
      }
    }
  }

  /**
   * Load a key store and query it to confirm a key pair with the specified alias is present.
   *
   * @param type the key store type e.g. JKS, PKCS12, etc
   * @param path the path to the key store
   * @param password the key store password
   * @param alias the alias for the key pair in the key store that should be retrieved
   * @return the key store that was loaded
   * @throws GeneralSecurityException if the key store could not be loaded
   */
  public static KeyStore loadKeyStore(String type, String path, String password, String alias)
      throws GeneralSecurityException {
    InputStream input = null;

    try {
      PathMatchingResourcePatternResolver resourceLoader =
          new PathMatchingResourcePatternResolver();

      Resource keyStoreResource = resourceLoader.getResource(path);

      if (!keyStoreResource.exists()) {
        throw new GeneralSecurityException("The key store (" + path + ") could not be found");
      }

      KeyStore ks = KeyStore.getInstance(type);

      input = keyStoreResource.getInputStream();

      ks.load(
          input,
          ((password == null) || (password.length() == 0)) ? new char[0] : password.toCharArray());

      // Attempt to retrieve the private key from the key store
      Key privateKey =
          ks.getKey(
              alias, StringUtils.hasText(password) ? password.toCharArray() : "".toCharArray());

      if (privateKey == null) {
        throw new GeneralSecurityException(
            "A private key with alias ("
                + alias
                + ") could not be found in the key store ("
                + path
                + ")");
      }

      // Attempt to retrieve the certificate from the key store
      java.security.cert.Certificate certificate = ks.getCertificate(alias);

      if (certificate == null) {
        throw new GeneralSecurityException(
            "A certificate with alias ("
                + alias
                + ") could not be found in the key store ("
                + path
                + ")");
      }

      if (!(certificate instanceof X509Certificate)) {
        throw new GeneralSecurityException(
            "The certificate with alias (" + alias + ") is not an X509 certificate");
      }

      return ks;
    } catch (Throwable e) {
      throw new GeneralSecurityException(
          "Failed to load and query the key store (" + path + ")", e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (Throwable ignored) {
      }
    }
  }

  /**
   * Load the trust store.
   *
   * @param type the trust store type e.g. JKS, PKCS12, etc
   * @param path the path to the trust store
   * @param password the trust store password
   * @return the trust store that was loaded
   * @throws GeneralSecurityException if the trust store could not be loaded
   */
  public static KeyStore loadTrustStore(String type, String path, String password)
      throws GeneralSecurityException {
    KeyStore ks;

    InputStream input = null;

    try {
      PathMatchingResourcePatternResolver resourceLoader =
          new PathMatchingResourcePatternResolver();

      Resource trustStoreResource = resourceLoader.getResource(path);

      if (!trustStoreResource.exists()) {
        throw new GeneralSecurityException("The trust store (" + path + ") could not be found");
      }

      ks = KeyStore.getInstance(type);

      input = trustStoreResource.getInputStream();

      ks.load(
          input,
          ((password == null) || (password.length() == 0)) ? new char[0] : password.toCharArray());

      return ks;
    } catch (Throwable e) {
      throw new GeneralSecurityException("Failed to load the trust store (" + path + ")", e);
    } finally {
      try {
        if (input != null) {
          input.close();
        }
      } catch (Throwable ignored) {
      }
    }
  }

  /**
   * Convert the specified password to a 3DES key that can be used with the 3DES cypher encrypt and
   * decrypt functions.
   *
   * @param password the password to convert to a 3DES key
   * @return the 3DES key
   */
  public static byte[] passwordTo3DESKey(String password) throws CryptoException {
    byte[] salt = "0907df13-2ef5-41a8-90e7-f08a3ca16af4".getBytes();

    return passwordTo3DESKey(password.getBytes(), salt);
  }

  /**
   * Convert the specified password to an AES key that can be used with the AES cypher encrypt and
   * decrypt functions.
   *
   * @param password the password to convert to an AES key
   * @return the AES key
   */
  public static byte[] passwordToAESKey(String password) throws CryptoException {
    byte[] salt = "9aeabd0f-be94-486e-a693-ed2d553ea202".getBytes();

    return passwordToAESKey(password.getBytes(), salt, AES_KEY_SIZE);
  }

  /**
   * Convert the specified password to an AES key that can be used with the AES cypher encrypt and
   * decrypt functions.
   *
   * @param password the password to convert to an AES key
   * @param salt the salt to use when generating the AES key
   * @return the AES key
   */
  public static byte[] passwordToAESKey(String password, byte[] salt) throws CryptoException {
    return passwordToAESKey(password.getBytes(), salt, AES_KEY_SIZE);
  }

  /**
   * Convert the specified password to an AES key that can be used with the AES cypher encrypt and
   * decrypt functions.
   *
   * @param password the password to convert to an AES key
   * @param salt the salt to use when generating the AES key
   * @return the AES key
   */
  public static byte[] passwordToAESKey(String password, String salt) throws CryptoException {
    return passwordToAESKey(password.getBytes(), salt.getBytes(), AES_KEY_SIZE);
  }

  private static byte[] hashPasswordAndSalt(byte[] password, byte[] salt) {
    try {
      // Concatenate password and salt.
      byte[] pwAndSalt = new byte[password.length + salt.length];

      System.arraycopy(password, 0, pwAndSalt, 0, password.length);
      System.arraycopy(salt, 0, pwAndSalt, password.length, salt.length);

      // Create the key as sha1(sha1(sha1(sha1(...(pw+salt))...)
      MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");

      for (int i = 0; i < 4; i++) {
        messageDigest.update(pwAndSalt, 0, pwAndSalt.length);
        messageDigest.digest(pwAndSalt, 0, messageDigest.getDigestLength());
      }

      return pwAndSalt;
    } catch (Throwable e) {
      throw new CryptoException("Failed to hash the password and key", e);
    }
  }

  private static byte[] passwordTo3DESKey(byte[] password, byte[] salt) throws CryptoException {
    try {
      byte[] key = new byte[24];

      System.arraycopy(hashPasswordAndSalt(password, salt), 0, key, 0, 24);

      return key;
    } catch (Throwable e) {
      throw new CryptoException("Failed to convert the password to a 3DES key", e);
    }
  }

  private static byte[] passwordToAESKey(byte[] password, byte[] salt, int keysize)
      throws CryptoException {
    try {
      byte[] key = new byte[keysize];

      System.arraycopy(hashPasswordAndSalt(password, salt), 0, key, 0, keysize);

      return key;
    } catch (Throwable e) {
      throw new CryptoException("Failed to convert the password to an AES key", e);
    }
  }
}
