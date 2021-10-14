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

package africa.absa.inception.oauth2.server.authorization.token;

import africa.absa.inception.security.User;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTClaimsSet.Builder;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPrivateKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.util.StringUtils;

/**
 * The <b>OAuth2AccessToken</b> holds the information for an OAuth2 access token.
 *
 * @author Marcus Portmann
 */
public class OAuth2AccessToken extends org.springframework.security.oauth2.core.OAuth2AccessToken {

  /** The name of the functions claim that provides the functions assigned to the user. */
  public static final String FUNCTIONS_CLAIM = "functions";

  /** The name of the claim that provides the name of the user. */
  public static final String NAME_CLAIM = "name";

  /** The name of the roles claim that provides the roles assigned to the user. */
  public static final String ROLES_CLAIM = "roles";

  /** The name of the scope claim. */
  public static final String SCOPE_CLAIM = "scope";

  /**
   * The name of the tenants claim that provides the IDs for the tenants the user is associated
   * with.
   */
  public static final String TENANTS_CLAIM = "tenants";

  /**
   * The name of the user directory ID claim that provides the ID for the user directory the user is
   * associated with.
   */
  public static final String USER_DIRECTORY_ID_CLAIM = "user_directory_id";

  private static final long serialVersionUID = 1000000;

  /** The subject for the token. */
  private final String subject;

  /**
   * Constructs a new <b>OAuth2AccessToken</b>.
   *
   * @param tokenType the token type
   * @param tokenValue the token value
   * @param subject the subject for the token
   * @param issuedAt the time at which the token was issued
   * @param expiresAt the expiration time on or after which the token MUST NOT be accepted
   * @param scopes the scope(s) associated to the token
   */
  public OAuth2AccessToken(
      TokenType tokenType,
      String tokenValue,
      String subject,
      Instant issuedAt,
      Instant expiresAt,
      Set<String> scopes) {
    super(tokenType, tokenValue, issuedAt, expiresAt, scopes);

    this.subject = subject;
  }

  /**
   * Build a new <b>OAuth2AccessToken</b>.
   *
   * @param user the User the token is being issued for
   * @param roleCodes the role codes for the user the token is being issued for
   * @param functionCodes the function codes for the user the token is being issued for
   * @param tenantIds the IDs for the tenants that the user the token is being issued for is
   *     associated with
   * @param scopes the optional scope(s) associated to the token
   * @param issuer the optional issuer of the token, which can be <b>null</b>
   * @param validFor the number of seconds the token should be valid for
   * @param rsaPrivateKey the RSA private key used to sign the token
   * @return the OAuth2 access token
   */
  public static OAuth2AccessToken build(
      User user,
      List<String> roleCodes,
      List<String> functionCodes,
      List<UUID> tenantIds,
      Set<String> scopes,
      String issuer,
      int validFor,
      RSAPrivateKey rsaPrivateKey) {
    try {
      Instant issuedAt = Instant.now();
      Instant expiresAt = issuedAt.plusSeconds(validFor);

      JWSSigner signer = new RSASSASigner(rsaPrivateKey);

      JWTClaimsSet.Builder jwtClaimsSetBuilder = new Builder();

      jwtClaimsSetBuilder.subject(user.getUsername());

      if (StringUtils.hasText(issuer)) {
        jwtClaimsSetBuilder.issuer(issuer);
      }

      if (StringUtils.hasText(user.getName())) {
        jwtClaimsSetBuilder.claim(NAME_CLAIM, user.getName());
      } else if (StringUtils.hasText(user.getEmail())) {
        jwtClaimsSetBuilder.claim(NAME_CLAIM, user.getEmail());
      } else {
        jwtClaimsSetBuilder.claim(NAME_CLAIM, user.getUsername());
      }

      jwtClaimsSetBuilder.claim(USER_DIRECTORY_ID_CLAIM, user.getUserDirectoryId().toString());

      jwtClaimsSetBuilder.issueTime(Date.from(issuedAt));

      jwtClaimsSetBuilder.expirationTime(Date.from(expiresAt));

      jwtClaimsSetBuilder.claim(
          TENANTS_CLAIM,
          Arrays.stream(tenantIds.toArray(new UUID[0]))
              .map(Object::toString)
              .toArray(String[]::new));

      jwtClaimsSetBuilder.claim(ROLES_CLAIM, roleCodes);

      jwtClaimsSetBuilder.claim(FUNCTIONS_CLAIM, functionCodes);

      if ((scopes != null) && (!scopes.isEmpty())) {
        jwtClaimsSetBuilder.claim(
            SCOPE_CLAIM, StringUtils.collectionToDelimitedString(scopes, " "));
      }

      SignedJWT signedJWT =
          new SignedJWT(
              new JWSHeader.Builder(JWSAlgorithm.RS256).build(), jwtClaimsSetBuilder.build());

      signedJWT.sign(signer);

      return new OAuth2AccessToken(
          TokenType.BEARER, signedJWT.serialize(), user.getUsername(), issuedAt, expiresAt, scopes);

    } catch (Throwable e) {
      throw new OAuth2AccessTokenException("Failed to build the OAuth2 access token", e);
    }
  }

  /**
   * Returns the lifetime in seconds of the access token.
   *
   * @return the lifetime in seconds of the access token
   */
  public long getExpiresIn() {
    if (getIssuedAt() == null) {
      return Duration.between(Instant.now(), getExpiresAt()).toSeconds();
    } else {
      return Duration.between(getIssuedAt(), getExpiresAt()).toSeconds();
    }
  }

  /**
   * Returns the subject for the token.
   *
   * @return the subject for the token
   */
  public String getSubject() {
    return subject;
  }
}
