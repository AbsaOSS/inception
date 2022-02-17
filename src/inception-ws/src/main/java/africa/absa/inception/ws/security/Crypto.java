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

package africa.absa.inception.ws.security;

import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertPath;
import java.security.cert.CertPathValidator;
import java.security.cert.CertStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.x500.X500Principal;
import org.apache.wss4j.common.crypto.CryptoBase;
import org.apache.wss4j.common.crypto.CryptoType;
import org.apache.wss4j.common.ext.WSPasswordCallback;
import org.apache.wss4j.common.ext.WSSecurityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The <b>Crypto</b> class.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class Crypto extends CryptoBase implements org.apache.wss4j.common.crypto.Crypto {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(Crypto.class);

  /** The certificate revocation list store. */
  private final CertStore crlStore;

  /** The key store. */
  private final KeyStore keyStore;

  /** The key store password. */
  private final String keyStorePassword;

  /** The trust store. */
  private final KeyStore trustStore;

  /**
   * Constructs a new <b>Crypto</b>.
   *
   * @param keyStore the key store
   * @param keyStorePassword the key store password
   * @param trustStore the trust store
   */
  public Crypto(KeyStore keyStore, String keyStorePassword, KeyStore trustStore) {
    this.keyStore = keyStore;
    this.keyStorePassword = keyStorePassword;
    this.trustStore = trustStore;
    this.crlStore = null;
  }

  /**
   * Constructs a new <b>Crypto</b>.
   *
   * @param keyStore the key store
   * @param keyStorePassword the key store password
   * @param trustStore the trust store
   * @param crlStore the certificate revocation list store
   */
  public Crypto(
      KeyStore keyStore, String keyStorePassword, KeyStore trustStore, CertStore crlStore) {
    this.keyStore = keyStore;
    this.keyStorePassword = keyStorePassword;
    this.trustStore = trustStore;
    this.crlStore = crlStore;
  }

  private static String createKeyStoreErrorMessage(KeyStore keyStore) throws KeyStoreException {
    Enumeration<String> aliases = keyStore.aliases();
    StringBuilder buffer = new StringBuilder(keyStore.size() * 7);
    boolean firstAlias = true;
    while (aliases.hasMoreElements()) {
      if (!firstAlias) {
        buffer.append(", ");
      }

      buffer.append(aliases.nextElement());
      firstAlias = false;
    }

    return " in key store of type ("
        + keyStore.getType()
        + ") from provider ("
        + keyStore.getProvider()
        + ") with size ("
        + keyStore.size()
        + ") and aliases: {"
        + buffer.toString()
        + "}";
  }

  /**
   * Get the private key associated with the public key.
   *
   * @param publicKey the public key
   * @param callbackHandler the callback handler
   * @return the private key associated with the public key
   */
  @Override
  public PrivateKey getPrivateKey(PublicKey publicKey, CallbackHandler callbackHandler)
      throws WSSecurityException {
    if (keyStore == null) {
      throw new WSSecurityException(
          WSSecurityException.ErrorCode.FAILURE, "empty", new Object[] {"The key store is null"});
    }

    if (callbackHandler == null) {
      throw new WSSecurityException(
          WSSecurityException.ErrorCode.FAILURE,
          "empty",
          new Object[] {"The callback handler is null"});
    }

    String alias = getAliasForPublicKey(publicKey, keyStore);
    if (alias == null) {
      try {
        String message = "Failed to find the private key for the public key";
        String keyStoreErrorMessage = createKeyStoreErrorMessage(keyStore);
        logger.error(message + keyStoreErrorMessage);

        throw new WSSecurityException(
            WSSecurityException.ErrorCode.FAILURE, "empty", new Object[] {message});
      } catch (KeyStoreException ex) {
        throw new WSSecurityException(
            WSSecurityException.ErrorCode.FAILURE,
            ex,
            "noPrivateKey",
            new Object[] {ex.getMessage()});
      }
    }

    String password = getPassword(alias, callbackHandler);

    return getPrivateKey(alias, password);
  }

  /**
   * Gets the private key corresponding to the identifier.
   *
   * @param identifier the implementation-specific identifier corresponding to the private key
   * @param password the password required to access the key
   * @return the private key
   */
  @Override
  public PrivateKey getPrivateKey(String identifier, String password) throws WSSecurityException {
    if (keyStore == null) {
      throw new WSSecurityException(
          WSSecurityException.ErrorCode.FAILURE, "empty", new Object[] {"The key store is null"});
    }

    try {
      if ((identifier == null) || (!keyStore.isKeyEntry(identifier))) {
        String message = "Failed to find the private key for the alias (" + identifier + ")";
        String keyStoreErrorMessage = createKeyStoreErrorMessage(keyStore);
        logger.error(message + keyStoreErrorMessage);

        throw new WSSecurityException(
            WSSecurityException.ErrorCode.FAILURE, "empty", new Object[] {message});
      }

      String pwd = password;
      if ((pwd == null) && (keyStorePassword != null)) {
        pwd = keyStorePassword;
      }

      Key key = keyStore.getKey(identifier, (pwd == null) ? new char[] {} : pwd.toCharArray());
      if (!(key instanceof PrivateKey)) {
        String message = "The key with the alias (" + identifier + ") is not a private key";
        String keyStoreErrorMessage = createKeyStoreErrorMessage(keyStore);
        logger.error(message + keyStoreErrorMessage);

        throw new WSSecurityException(
            WSSecurityException.ErrorCode.FAILURE, "empty", new Object[] {message});
      }

      return (PrivateKey) key;
    } catch (KeyStoreException | UnrecoverableKeyException | NoSuchAlgorithmException ex) {
      throw new WSSecurityException(
          WSSecurityException.ErrorCode.FAILURE,
          ex,
          "noPrivateKey",
          new Object[] {ex.getMessage()});
    }
  }

  /**
   * Get the private key associated with the X.509 certificate using the password provided by the
   * callback handler.
   *
   * @param certificate the X.509 certificate
   * @param callbackHandler the callback handler
   * @return the private key associated with the X.509 certificate
   */
  @Override
  public PrivateKey getPrivateKey(X509Certificate certificate, CallbackHandler callbackHandler)
      throws WSSecurityException {
    if (keyStore == null) {
      throw new WSSecurityException(
          WSSecurityException.ErrorCode.FAILURE, "empty", new Object[] {"The key store is null"});
    }

    if (callbackHandler == null) {
      throw new WSSecurityException(
          WSSecurityException.ErrorCode.FAILURE,
          "empty",
          new Object[] {"The callback handler is null"});
    }

    String alias = getAliasForPublicKey(certificate, keyStore);
    if (alias == null) {
      try {
        String message = "Failed to find the private key for the X.509 certificate";
        String keyStoreErrorMessage = createKeyStoreErrorMessage(keyStore);
        logger.error(message + keyStoreErrorMessage);

        throw new WSSecurityException(
            WSSecurityException.ErrorCode.FAILURE, "empty", new Object[] {message});
      } catch (KeyStoreException ex) {
        throw new WSSecurityException(
            WSSecurityException.ErrorCode.FAILURE,
            ex,
            "noPrivateKey",
            new Object[] {ex.getMessage()});
      }
    }

    String password = getPassword(alias, callbackHandler);

    return getPrivateKey(alias, password);
  }

  /**
   * Get the X.509 certificate or certificate chain for the crypto type.
   *
   * <p>The supported types are as follows:
   *
   * <ul>
   *   <li><b>TYPE.ISSUER_SERIAL</b><br>
   *       The X.509 certificate (chain) is identified by the issuer name and serial number.
   *   <li><b>TYPE.THUMBPRINT_SHA1</b><br>
   *       The X.509 certificate (chain) is identified by the SHA1 of the (root) certificate.
   *   <li><b>TYPE.SKI_BYTES</b><br>
   *       The X.509 certificate (chain) is identified by the SKI bytes of the (root) certificate.
   *   <li><b>TYPE.SUBJECT_DN</b><br>
   *       The X.509 certificate (chain) is identified by the subject DN of the (root) certificate.
   *   <li><b>TYPE.ALIAS</b><br>
   *       The X.509 certificate (chain) is identified by an alias, which for this implementation
   *       means an alias of the key store or trust store.
   * </ul>
   *
   * @param cryptoType the crypto type
   * @return the X.509 certificate or certificate chain for the crypto type
   */
  @Override
  public X509Certificate[] getX509Certificates(CryptoType cryptoType) throws WSSecurityException {
    if (cryptoType == null) {
      return null;
    }

    CryptoType.TYPE type = cryptoType.getType();
    X509Certificate[] certificateChain = null;
    switch (type) {
      case ISSUER_SERIAL:
        certificateChain =
            getX509CertificateChainForIssuerAndSerialNumber(
                cryptoType.getIssuer(), cryptoType.getSerial());

        break;

      case THUMBPRINT_SHA1:
        certificateChain = getX509CertificateChainForThumbprint(cryptoType.getBytes());

        break;

      case SKI_BYTES:
        certificateChain = getX509CertificateChainForSKI(cryptoType.getBytes());

        break;

      case SUBJECT_DN:
        certificateChain = getX509CertificateChainForSubject(cryptoType.getSubjectDN());

        break;

      case ALIAS:
        certificateChain = getX509CertificateChainForAlias(cryptoType.getAlias());

        break;

      case ENDPOINT:
        break;
    }

    return certificateChain;
  }

  /**
   * Get the identifier for the X.509 certificate i.e. the key store alias.
   *
   * @param certificate the X.509 certificate
   * @return the identifier for the X.509 certificate i.e. the key store alias or <b>null</b> if the
   *     X.509 certificate could not be found
   */
  @Override
  public String getX509Identifier(X509Certificate certificate) throws WSSecurityException {
    String alias = null;

    if (keyStore != null) {
      alias = getAliasForPublicKey(certificate, keyStore);
    }

    if ((alias == null) && (trustStore != null)) {
      alias = getAliasForPublicKey(certificate, trustStore);
    }

    return alias;
  }

  /**
   * Evaluate whether the public key is trusted.
   *
   * @param publicKey the public key
   */
  public void verifyTrust(PublicKey publicKey) throws WSSecurityException {
    // If the public key is null, do not trust the signature
    if (publicKey == null) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
    }

    /*
     * Search the key store for the transmitted public key (direct trust). If not found then search
     * the trust store for the transmitted public key (direct trust).
     */
    if ((!isPublicKeyInKeyStore(publicKey, keyStore))
        && (!isPublicKeyInKeyStore(publicKey, trustStore))) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
    }
  }

  @Override
  public void verifyTrust(
      X509Certificate[] certs,
      boolean enableRevocation,
      Collection<Pattern> subjectCertConstraints,
      Collection<Pattern> issuerCertConstraints)
      throws WSSecurityException {
    verifyTrust(certs, enableRevocation, subjectCertConstraints);

    if (!matchesIssuerDnPattern(certs[0], issuerCertConstraints)) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
    }
  }

  /**
   * Adds the trust anchors in the key store to the set.
   *
   * @param trustAnchors the set to which to add the trust anchors
   * @param keyStore the key store to search for trust anchors
   */
  private void addTrustAnchors(Set<TrustAnchor> trustAnchors, KeyStore keyStore)
      throws KeyStoreException, WSSecurityException {
    Enumeration<String> aliases = keyStore.aliases();
    while (aliases.hasMoreElements()) {
      String alias = aliases.nextElement();
      X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);
      if (certificate != null) {
        TrustAnchor anchor = new TrustAnchor(certificate, getNameConstraints(certificate));
        trustAnchors.add(anchor);
      }
    }
  }

  /**
   * Convert the subject DN to an X.500 principal.
   *
   * @param subjectDN the subject DN
   * @return the X.500 principal
   */
  private Object convertSubjectToPrincipal(String subjectDN) {
    /*
     * Convert the subject DN to a java X500Principal object first. This is to ensure
     * interoperability with a DN constructed from .NET, where e.g. it uses "S" instead of "ST".
     * Then convert it to a BouncyCastle X509Name, which will order the attributes of the DN in a
     * particular way (see WSS-168). If the conversion to an X500Principal object fails (e.g. if
     * the DN contains "E" instead of "EMAILADDRESS"), then fall back on a direct conversion to a
     * BC X509Name.
     */
    Object subject;
    try {
      X500Principal subjectRDN = new X500Principal(subjectDN);
      subject = createBCX509Name(subjectRDN.getName());
    } catch (java.lang.IllegalArgumentException ex) {
      subject = createBCX509Name(subjectDN);
    }

    return subject;
  }

  /**
   * Create the parameters for the PXIX algorithm used to validate the certificate path.
   *
   * @param trustAnchors the trust anchors
   * @param enableRevocation are certificate revocation checks enabled
   * @return he parameters for the PXIX algorithm used to validate the certificate path
   */
  private PKIXParameters createPKIXParameters(
      Set<TrustAnchor> trustAnchors, boolean enableRevocation)
      throws InvalidAlgorithmParameterException {
    PKIXParameters pkixParameters = new PKIXParameters(trustAnchors);
    pkixParameters.setRevocationEnabled(enableRevocation);

    if (enableRevocation && (crlStore != null)) {
      pkixParameters.addCertStore(crlStore);
    }

    return pkixParameters;
  }

  /**
   * Get the key store alias that corresponds to the public key in the key store.
   *
   * @param publicKey the public key
   * @param keyStore the key store
   * @return the key store alias that corresponds to the public key in the key store
   */
  private String getAliasForPublicKey(PublicKey publicKey, KeyStore keyStore)
      throws WSSecurityException {
    try {
      for (Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
        String alias = aliases.nextElement();

        Certificate[] certificateChain = keyStore.getCertificateChain(alias);
        if ((certificateChain) == null || (certificateChain.length == 0)) {
          // No certificate chain, so check if getCertificate gives a result
          Certificate retrievedCert = keyStore.getCertificate(alias);
          if (retrievedCert != null) {
            certificateChain = new Certificate[] {retrievedCert};
          }
        }

        if ((certificateChain != null)
            && (certificateChain.length > 0)
            && (certificateChain[0].getPublicKey().equals(publicKey))) {
          return alias;
        }
      }
    } catch (KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "keystore");
    }

    return null;
  }

  /**
   * Get the key store alias that corresponds to the X.509 certificate in the key store.
   *
   * @param certificate the X.509 certificate
   * @param keyStore the key store
   * @return the key store alias that corresponds to the X.509 certificate in the key store
   */
  private String getAliasForPublicKey(X509Certificate certificate, KeyStore keyStore)
      throws WSSecurityException {
    try {
      for (Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
        String alias = aliases.nextElement();

        Certificate[] certificateChain = keyStore.getCertificateChain(alias);
        if ((certificateChain == null) || (certificateChain.length == 0)) {
          // No certificate chain, so check if getCertificate gives a result
          Certificate retrievedCertificate = keyStore.getCertificate(alias);
          if (retrievedCertificate != null) {
            certificateChain = new Certificate[] {retrievedCertificate};
          }
        }

        if ((certificateChain != null)
            && (certificateChain.length > 0)
            && (certificateChain[0].equals(certificate))) {
          return alias;
        }
      }
    } catch (KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "keystore");
    }

    return null;
  }

  /**
   * Get the certificate or certificate chain with the serial number and issuer DN from the key
   * store.
   *
   * @param issuerRDN the X500Principal or BouncyCastle X509Name instance identifying the issuer
   * @param serialNumber the serial number
   * @param keyStore the key store
   * @return the certificate or certificate chain
   */
  private Certificate[] getCertificateChainForIssuerAndSerialNumber(
      Object issuerRDN, BigInteger serialNumber, KeyStore keyStore) throws WSSecurityException {
    if (logger.isDebugEnabled()) {
      logger.debug(
          "Searching the key store for the certificate with issuer {} and serial {}",
          issuerRDN,
          serialNumber);
    }

    try {
      for (Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
        String alias = aliases.nextElement();
        Certificate[] certificateChain = keyStore.getCertificateChain(alias);
        if ((certificateChain == null) || (certificateChain.length == 0)) {
          // No certificate chain, so check if getCertificate gives a result
          Certificate cert = keyStore.getCertificate(alias);
          if (cert != null) {
            certificateChain = new Certificate[] {cert};
          }
        }

        if ((certificateChain != null)
            && (certificateChain.length > 0)
            && (certificateChain[0] instanceof X509Certificate)) {
          X509Certificate certificate = (X509Certificate) certificateChain[0];

          if (logger.isDebugEnabled()) {
            logger.debug(
                "The key store alias {} has issuer {} and serial {}",
                alias,
                certificate.getIssuerX500Principal().getName(),
                certificate.getSerialNumber());
          }

          if (certificate.getSerialNumber().compareTo(serialNumber) == 0) {
            Object certificateName =
                createBCX509Name(certificate.getIssuerX500Principal().getName());
            if (certificateName.equals(issuerRDN)) {
              if (logger.isDebugEnabled()) {
                logger.debug("Issuer serial match found using the key store alias {}", alias);
              }

              return certificateChain;
            }
          }
        }
      }
    } catch (KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "keystore");
    }

    if (logger.isDebugEnabled()) {
      logger.debug("No issuer serial match found in the key store");
    }

    return new Certificate[] {};
  }

  /**
   * Get the certificate or certificate chain identified by the subject key identifier in the key
   * store.
   *
   * @param skiBytes the subject key identifier bytes
   * @param keyStore the key store
   * @return the certificate or certificate chain or <b>null</b> if the certificate or certificate
   *     chain identified by the subject key identifier could not be found in the key store
   */
  private Certificate[] getCertificateChainForSKI(byte[] skiBytes, KeyStore keyStore)
      throws WSSecurityException {
    logger.debug(
        "Searching the key store for the certificate or certificate chain matching the subject key identifier");

    try {
      for (Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
        String alias = aliases.nextElement();
        Certificate[] certificateChain = keyStore.getCertificateChain(alias);
        if ((certificateChain == null) || (certificateChain.length == 0)) {
          // No certificate chain, so check if getCertificate gives a result.
          Certificate certificate = keyStore.getCertificate(alias);
          if (certificate != null) {
            certificateChain = new Certificate[] {certificate};
          }
        }

        if ((certificateChain != null)
            && (certificateChain.length > 0)
            && (certificateChain[0] instanceof X509Certificate)) {
          X509Certificate certificate = (X509Certificate) certificateChain[0];
          byte[] data = getSKIBytesFromCert(certificate);
          if ((data.length == skiBytes.length) && (Arrays.equals(data, skiBytes))) {
            logger.debug(
                "Found the certificate or certificate chain matching the subject key identifier with alias {}",
                alias);

            return certificateChain;
          }
        }
      }
    } catch (KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "keystore");
    }

    logger.debug(
        "Failed to find the certificate or certificate chain matching the subject key identifier in the key store");

    return new Certificate[] {};
  }

  /**
   * Get the certificate or certificate chain with the thumbprint from the key store using the SHA-1
   * message digest.
   *
   * @param thumbprint the thumbprint
   * @param keyStore the key store
   * @param sha1MessageDigest the SHA-1 message digest
   * @return the certificate or certificate chain
   */
  private Certificate[] getCertificateChainForThumbprint(
      byte[] thumbprint, KeyStore keyStore, MessageDigest sha1MessageDigest)
      throws WSSecurityException {
    if (logger.isDebugEnabled()) {
      logger.debug("Searching the keystore for the certificate using a SHA-1 thumbprint");
    }

    try {
      for (Enumeration<String> aliases = keyStore.aliases(); aliases.hasMoreElements(); ) {
        String alias = aliases.nextElement();
        Certificate[] certificateChain = keyStore.getCertificateChain(alias);
        if ((certificateChain == null) || (certificateChain.length == 0)) {
          // No certificate chain, so check if getCertificate gives a result
          Certificate certificate = keyStore.getCertificate(alias);
          if (certificate != null) {
            certificateChain = new Certificate[] {certificate};
          }
        }

        if ((certificateChain != null)
            && (certificateChain.length > 0)
            && (certificateChain[0] instanceof X509Certificate)) {
          X509Certificate certificate = (X509Certificate) certificateChain[0];
          try {
            sha1MessageDigest.update(certificate.getEncoded());
          } catch (CertificateEncodingException ex) {
            throw new WSSecurityException(
                WSSecurityException.ErrorCode.SECURITY_TOKEN_UNAVAILABLE, ex, "encodeError");
          }

          byte[] data = sha1MessageDigest.digest();

          if (Arrays.equals(data, thumbprint)) {
            if (logger.isDebugEnabled()) {
              logger.debug("Thumbprint match found using key store alias {}", alias);
            }

            return certificateChain;
          }
        }
      }
    } catch (KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "keystore");
    }

    if (logger.isDebugEnabled()) {
      logger.debug("No thumbprint match found in the key store");
    }

    return new Certificate[] {};
  }

  /**
   * Get all the certificates and certificate chains matching the subject in the key store.
   *
   * @param subjectDN either an X500Principal or a BouncyCastle X509Name instance for the subject
   * @param store the key store
   * @return the certificates and certificate chains matching the subject in the key store
   */
  private List<Certificate[]> getCertificateChainsForSubject(Object subjectDN, KeyStore store)
      throws WSSecurityException {
    logger.debug(
        "Searching the key store for the certificates and certificate chains with subject ({})",
        subjectDN);

    List<Certificate[]> foundCertificates = new ArrayList<>();
    try {
      for (Enumeration<String> aliases = store.aliases(); aliases.hasMoreElements(); ) {
        String alias = aliases.nextElement();
        Certificate[] certificateChain = store.getCertificateChain(alias);
        if ((certificateChain == null) || (certificateChain.length == 0)) {
          // No certificate chain, so check if getCertificate gives a result
          Certificate certificate = store.getCertificate(alias);
          if (certificate != null) {
            certificateChain = new Certificate[] {certificate};
          }
        }

        if ((certificateChain != null)
            && (certificateChain.length > 0)
            && (certificateChain[0] instanceof X509Certificate)) {
          X500Principal certificateDN =
              ((X509Certificate) certificateChain[0]).getSubjectX500Principal();
          Object certificateX500PrincipalName = createBCX509Name(certificateDN.getName());

          if (subjectDN.equals(certificateX500PrincipalName)) {
            logger.debug(
                "Found the certificate with subject ({}) and alias ({}) in the key store",
                subjectDN,
                alias);
            foundCertificates.add(certificateChain);
          }
        }
      }
    } catch (KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "keystore");
    }

    if (foundCertificates.isEmpty()) {
      logger.debug(
          "Failed to find the certificates or certificate chains with subject ({}) in the key store",
          subjectDN);
    }

    return foundCertificates;
  }

  /**
   * Get a password from the callback handler.
   *
   * @param identifier the identifier
   * @param callbackHandler the callback handler
   * @return the password retrieved from the callback handler
   */
  private String getPassword(String identifier, CallbackHandler callbackHandler)
      throws WSSecurityException {
    WSPasswordCallback passwordCallback =
        new WSPasswordCallback(identifier, WSPasswordCallback.DECRYPT);
    try {
      Callback[] callbacks = new Callback[] {passwordCallback};
      callbackHandler.handle(callbacks);
    } catch (IOException | UnsupportedCallbackException e) {
      throw new WSSecurityException(
          WSSecurityException.ErrorCode.FAILURE, e, "noPassword", new Object[] {identifier});
    }

    return passwordCallback.getPassword();
  }

  /**
   * Get the X.509 certificate or certificate chain that corresponds to the key store alias from the
   * key store or trust store.
   *
   * @param alias the key store alias
   * @return the X.509 certificate or certificate chain that corresponds to the identifier or <b>
   *     null</b> if no X.509 certificate or certificate chain could not be found
   */
  private X509Certificate[] getX509CertificateChainForAlias(String alias)
      throws WSSecurityException {
    if (alias == null) {
      return null;
    }

    Certificate[] certificateChain = null;
    try {
      if (keyStore != null) {
        certificateChain = keyStore.getCertificateChain(alias);

        if ((certificateChain == null) || (certificateChain.length == 0)) {
          Certificate certificate = keyStore.getCertificate(alias);
          if (certificate != null) {
            certificateChain = new Certificate[] {certificate};
          }
        }
      }

      if ((certificateChain == null) && (trustStore != null)) {
        certificateChain = trustStore.getCertificateChain(alias);

        if (certificateChain == null) {
          Certificate certificate = trustStore.getCertificate(alias);
          if (certificate != null) {
            certificateChain = new Certificate[] {certificate};
          }
        }
      }

      if (certificateChain == null) {
        return null;
      }
    } catch (KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "keystore");
    }

    return Arrays.copyOf(certificateChain, certificateChain.length, X509Certificate[].class);
  }

  /**
   * Get the X.509 certificate or certificate chain with the serial number and issuer from the key
   * store or trust store.
   *
   * @param issuer the issuer
   * @param serialNumber the serial number
   * @return the X.509 certificate or certificate chain or <b>null</b> if the X.509 certificate or
   *     certificate chain could not be found
   */
  private X509Certificate[] getX509CertificateChainForIssuerAndSerialNumber(
      String issuer, BigInteger serialNumber) throws WSSecurityException {
    /*
     * Convert the subject DN to a java X500Principal object first. This is to ensure
     * interoperability with a DN constructed from .NET, where e.g. it uses "S" instead of "ST".
     * Then convert it to a BouncyCastle X509Name, which will order the attributes of the DN in a
     * particular way (see WSS-168). If the conversion to an X500Principal object fails (e.g. if
     * the DN contains "E" instead of "EMAILADDRESS"), then fall back on a direct conversion to a
     * BC X509Name.
     */
    Object issuerName;
    try {
      X500Principal issuerRDN = new X500Principal(issuer);
      issuerName = createBCX509Name(issuerRDN.getName());
    } catch (java.lang.IllegalArgumentException ex) {
      issuerName = createBCX509Name(issuer);
    }

    Certificate[] certificateChain = null;
    if (keyStore != null) {
      certificateChain =
          getCertificateChainForIssuerAndSerialNumber(issuerName, serialNumber, keyStore);
    }

    // If we cannot find the issuer in the key store then look at the trust store
    if (((certificateChain == null) || (certificateChain.length == 0)) && (trustStore != null)) {
      certificateChain =
          getCertificateChainForIssuerAndSerialNumber(issuerName, serialNumber, trustStore);
    }

    if ((certificateChain == null) || (certificateChain.length == 0)) {
      return null;
    }

    return Arrays.copyOf(certificateChain, certificateChain.length, X509Certificate[].class);
  }

  /**
   * Get the X.509 certificate or certificate chain identified by the subject key identifier from
   * the key store or trust store.
   *
   * @param skiBytes the subject key identifier bytes
   * @return the X.509 certificate or certificate chain or <b>null</b> if the X.509 certificate or
   *     certificate chain identified by the subject key identifier could not be found
   */
  private X509Certificate[] getX509CertificateChainForSKI(byte[] skiBytes)
      throws WSSecurityException {
    Certificate[] certificateChain = null;
    if (keyStore != null) {
      certificateChain = getCertificateChainForSKI(skiBytes, keyStore);
    }

    // If we cannot find the issuer in the keystore then check the trust store
    if (((certificateChain == null) || (certificateChain.length == 0)) && (trustStore != null)) {
      certificateChain = getCertificateChainForSKI(skiBytes, trustStore);
    }

    if ((certificateChain == null) || (certificateChain.length == 0)) {
      return null;
    }

    return Arrays.copyOf(certificateChain, certificateChain.length, X509Certificate[].class);
  }

  /**
   * Get the X.509 certificate or certificate chain identified by the subject DN from the key store
   * or trust store.
   *
   * @param subjectDN the subject DN
   * @return the X.509 certificate or certificate chain identified by the subject DN or <b>null</b>
   *     if the X.509 certificate or certificate chain could not be found
   */
  private X509Certificate[] getX509CertificateChainForSubject(String subjectDN)
      throws WSSecurityException {
    Object subject = convertSubjectToPrincipal(subjectDN);

    List<Certificate[]> certificateChains = null;
    if (keyStore != null) {
      certificateChains = getCertificateChainsForSubject(subject, keyStore);
    }

    // If we cannot find the X.509 certificate (chain) in the key store then check the trust store
    if (((certificateChains == null) || certificateChains.isEmpty()) && (trustStore != null)) {
      certificateChains = getCertificateChainsForSubject(subject, trustStore);
    }

    if ((certificateChains == null) || certificateChains.isEmpty()) {
      return null;
    }

    // We just choose the first entry
    return Arrays.copyOf(
        certificateChains.get(0), certificateChains.get(0).length, X509Certificate[].class);
  }

  /**
   * Get the X.509 certificate or certificate chain matching the thumbprint from the key store or
   * trust store.
   *
   * @param thumbprint the SHA1 thumbprint
   * @return the X.509 certificate or certificate chain matching the thumbprint or <b>null</b> if
   *     the X.509 certificate or certificate chain could not be found
   */
  private X509Certificate[] getX509CertificateChainForThumbprint(byte[] thumbprint)
      throws WSSecurityException {
    MessageDigest sha1MessageDigest;

    try {
      sha1MessageDigest = MessageDigest.getInstance("SHA1");
    } catch (NoSuchAlgorithmException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "decoding.general");
    }

    Certificate[] certificateChain = null;
    if (keyStore != null) {
      certificateChain = getCertificateChainForThumbprint(thumbprint, keyStore, sha1MessageDigest);
    }

    // If we cannot find the issuer in the key store then look at the trust store
    if (((certificateChain == null) || (certificateChain.length == 0)) && (trustStore != null)) {
      certificateChain =
          getCertificateChainForThumbprint(thumbprint, trustStore, sha1MessageDigest);
    }

    if ((certificateChain == null) || (certificateChain.length == 0)) {
      return null;
    }

    return Arrays.copyOf(certificateChain, certificateChain.length, X509Certificate[].class);
  }

  /**
   * Check whether the public key is in the key store.
   *
   * @param publicKey the public key to search for
   * @param keyStoreToSearch the key store to search
   * @return <b>true</b> if the public key is in the key store or <b>false</b> otherwise
   */
  private boolean isPublicKeyInKeyStore(PublicKey publicKey, KeyStore keyStoreToSearch) {
    if (keyStoreToSearch == null) {
      return false;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("Searching the key store for the public key {}", publicKey);
    }

    try {
      for (Enumeration<String> aliases = keyStoreToSearch.aliases(); aliases.hasMoreElements(); ) {
        String alias = aliases.nextElement();
        Certificate[] certificateChain = keyStoreToSearch.getCertificateChain(alias);
        if ((certificateChain == null) || (certificateChain.length == 0)) {
          // No certificate chain, so check if getCertificate gives a result
          Certificate certificate = keyStoreToSearch.getCertificate(alias);
          if (certificate != null) {
            certificateChain = new Certificate[] {certificate};
          }
        }

        if ((certificateChain != null)
            && (certificateChain.length > 0)
            && (certificateChain[0] instanceof X509Certificate)
            && (publicKey.equals(certificateChain[0].getPublicKey()))) {
          if (logger.isDebugEnabled()) {
            logger.debug("Found a matching public key in the key store with alias {}", alias);
          }

          return true;
        }
      }
    } catch (KeyStoreException e) {
      return false;
    }

    if (logger.isDebugEnabled()) {
      logger.debug("No matching public key found in the key store");
    }

    return false;
  }

  /**
   * Evaluate whether a given certificate or certificate chain should be trusted.
   *
   * @param certificateChain the X.509 certificate (chain) to validate
   * @param enableRevocation whether to enable CRL verification or not
   * @param subjectCertificateConstraints the set of constraints on the Subject DN of the
   *     certificates
   */
  private void verifyTrust(
      X509Certificate[] certificateChain,
      boolean enableRevocation,
      Collection<Pattern> subjectCertificateConstraints)
      throws WSSecurityException {
    /*
     * FIRST:
     *
     * Search the key store and trust store for the issuer of transmitted certificate using the
     * X.500 distinguished name and serial number of the issuer certificate.
     */
    if ((certificateChain.length == 1) && (!enableRevocation)) {
      String issuerDN = certificateChain[0].getIssuerX500Principal().getName();
      BigInteger issuerSerial = certificateChain[0].getSerialNumber();

      CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ISSUER_SERIAL);
      cryptoType.setIssuerSerial(issuerDN, issuerSerial);

      X509Certificate[] foundCertificateChain = getX509Certificates(cryptoType);

      /*
       * If a certificate or certificate chain has been found, the certificates must be compared to
       * ensure against phony DNs (compare encoded form including signature).
       */
      if ((foundCertificateChain != null)
          && (foundCertificateChain[0] != null)
          && foundCertificateChain[0].equals(certificateChain[0])) {
        try {
          certificateChain[0].checkValidity();
        } catch (CertificateExpiredException | CertificateNotYetValidException e) {
          throw new WSSecurityException(
              WSSecurityException.ErrorCode.FAILED_CHECK, e, "invalidCert");
        }

        if (logger.isDebugEnabled()) {
          logger.debug(
              "Direct trust for the certificate ({})",
              certificateChain[0].getSubjectX500Principal().getName());
        }

        return;
      }
    }

    /*
     * SECOND:
     *
     * Search the key store and trust store for the issuer of transmitted certificate using the
     * Java X500Principal derived from the X.500 distinguished name.
     */
    List<Certificate[]> issuingCertificateChainsToVerifyAgainst = null;
    String issuerDN = certificateChain[0].getIssuerX500Principal().getName();
    if (certificateChain.length == 1) {
      Object subject = convertSubjectToPrincipal(issuerDN);

      if (keyStore != null) {
        issuingCertificateChainsToVerifyAgainst = getCertificateChainsForSubject(subject, keyStore);
      }

      // If we cannot find the issuer in the key store then look at the trust store
      if (((issuingCertificateChainsToVerifyAgainst == null)
              || issuingCertificateChainsToVerifyAgainst.isEmpty())
          && (trustStore != null)) {
        issuingCertificateChainsToVerifyAgainst =
            getCertificateChainsForSubject(subject, trustStore);
      }

      if ((issuingCertificateChainsToVerifyAgainst == null)
          || issuingCertificateChainsToVerifyAgainst.isEmpty()
          || (issuingCertificateChainsToVerifyAgainst.get(0).length < 1)) {
        String subjectDN = certificateChain[0].getSubjectX500Principal().getName();

        if (logger.isDebugEnabled()) {
          logger.debug(
              "No certificates found in the key store for the issuer ({}) of the certificate ({})",
              issuerDN,
              subjectDN);
        }

        throw new WSSecurityException(
            WSSecurityException.ErrorCode.FAILURE,
            "certpath",
            new Object[] {"No trusted certs found"});
      }
    }

    /*
     * THIRD:
     *
     *  Check the certificate trust path for the issuer certificate chain.
     */
    if (logger.isDebugEnabled()) {
      logger.debug("Preparing to validate the certificate path for the issuer ({})", issuerDN);
    }

    try {
      Set<TrustAnchor> set = new HashSet<>();
      if (trustStore != null) {
        addTrustAnchors(set, trustStore);
      }

      // Add the certificates from the key store only if there is no trust store
      if ((keyStore != null) && (trustStore == null)) {
        addTrustAnchors(set, keyStore);
      }

      // Verify the trust path using the above settings
      String provider = getCryptoProvider();
      CertPathValidator validator;
      if ((provider == null) || (provider.length() == 0)) {
        validator = CertPathValidator.getInstance("PKIX");
      } else {
        validator = CertPathValidator.getInstance("PKIX", provider);
      }

      PKIXParameters pkixParameters = createPKIXParameters(set, enableRevocation);

      // Generate the certificate path
      if ((issuingCertificateChainsToVerifyAgainst != null)
          && (!issuingCertificateChainsToVerifyAgainst.isEmpty())) {
        java.security.cert.CertPathValidatorException validatorException = null;

        // Try each potential issuing certificate path for a match
        for (Certificate[] issuingCertificateChainToVerifyAgainst :
            issuingCertificateChainsToVerifyAgainst) {
          X509Certificate[] x509certs =
              new X509Certificate[issuingCertificateChainToVerifyAgainst.length + 1];
          x509certs[0] = certificateChain[0];
          System.arraycopy(
              issuingCertificateChainToVerifyAgainst,
              0,
              x509certs,
              1,
              issuingCertificateChainToVerifyAgainst.length);

          List<X509Certificate> certificateList = Arrays.asList(x509certs);
          CertPath path = getCertificateFactory().generateCertPath(certificateList);

          try {
            validator.validate(path, pkixParameters);

            // We have a valid cert path at this point so break
            validatorException = null;

            break;
          } catch (java.security.cert.CertPathValidatorException e) {
            validatorException = e;
          }
        }

        if (validatorException != null) {
          throw validatorException;
        }
      } else {
        List<X509Certificate> certificateList = Arrays.asList(certificateChain);
        CertPath path = getCertificateFactory().generateCertPath(certificateList);

        validator.validate(path, pkixParameters);
      }
    } catch (NoSuchProviderException
        | NoSuchAlgorithmException
        | CertificateException
        | InvalidAlgorithmParameterException
        | java.security.cert.CertPathValidatorException
        | KeyStoreException e) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILURE, e, "certpath");
    }

    // Finally check the subject certificate constraints
    if (!matchesSubjectDnPattern(certificateChain[0], subjectCertificateConstraints)) {
      throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_AUTHENTICATION);
    }
  }
}
