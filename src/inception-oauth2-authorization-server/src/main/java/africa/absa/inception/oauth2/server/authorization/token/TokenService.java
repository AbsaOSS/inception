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

import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.security.ISecurityService;
import africa.absa.inception.security.User;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * The <b>TokenService</b> class provides the Token Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings({"unused", "WeakerAccess"})
public class TokenService implements ITokenService {

  /** The access token validity in seconds. */
  public static final int ACCESS_TOKEN_VALIDITY = 5 * 60;

  /**
   * The period in seconds prior to expiry of the refresh token during which it will be
   * automatically re-issued.
   */
  public static final int REFRESH_TOKEN_REISSUE_INTERVAL = 90 * 24 * 60 * 60;

  /** The refresh token validity in seconds. */
  public static final int REFRESH_TOKEN_VALIDITY = 365 * 24 * 60 * 60;

  /* Security Service */
  private final ISecurityService securityService;

  /* The application name. */
  @Value("${spring.application.name:#{null}}")
  private String applicationName;

  /* The RSA private key used to sign the JWTs. */
  @Value("${inception.oauth2.authorization-server.jwt.rsa-private-key-location}")
  private RSAPrivateKey rsaPrivateKey;

  /* The RSA public key used to verify the JWTs. */
  @Value("${inception.oauth2.authorization-server.jwt.rsa-public-key-location}")
  private RSAPublicKey rsaPublicKey;

  /**
   * Constructs a new <b>TokenService</b>.
   *
   * @param securityService the Security Service
   */
  public TokenService(ISecurityService securityService) {
    this.securityService = securityService;
  }

  /**
   * Issue an OAuth2 access token for the specified user.
   *
   * @param username the username for the user
   * @param scopes the optional scope(s) for the access token
   * @return the OAuth2 access token
   */
  public OAuth2AccessToken issueOAuth2AccessToken(String username, Set<String> scopes)
      throws ServiceUnavailableException {
    try {
      return createOAuth2AccessToken(username, scopes);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to issue the OAuth2 access token for the user (" + username + ")", e);
    }
  }

  /**
   * Issue an OAuth2 refresh token for the specified user.
   *
   * @param username the username for the user
   * @param scopes the optional scope(s) for the refresh token
   * @return the OAuth2 refresh token
   */
  public OAuth2RefreshToken issueOAuth2RefreshToken(String username, Set<String> scopes)
      throws ServiceUnavailableException {
    try {
      return createOAuth2RefreshToken(username, scopes);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to issue the OAuth2 refresh token for the user (" + username + ")", e);
    }
  }

  /**
   * Refresh an OAuth2 access token and if required the OAuth2 refresh token.
   *
   * @param encodedOAuth2RefreshToken the encoded OAuth2 refresh token
   * @return the refreshed tokens
   */
  public RefreshedOAuth2Tokens refreshOAuth2Tokens(String encodedOAuth2RefreshToken)
      throws InvalidOAuth2RefreshTokenException, ServiceUnavailableException {
    try {
      SignedJWT signedJWT = SignedJWT.parse(encodedOAuth2RefreshToken);

      RSASSAVerifier verifier = new RSASSAVerifier(rsaPublicKey);

      if (!signedJWT.verify(verifier)) {
        throw new InvalidOAuth2RefreshTokenException();
      }

      JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

      String username = claimsSet.getSubject();
      String scopeClaim = claimsSet.getStringClaim(OAuth2AccessToken.SCOPE_CLAIM);
      Instant issuedAt = claimsSet.getIssueTime().toInstant();
      Instant expiresAt = claimsSet.getExpirationTime().toInstant();

      if (Instant.now().isAfter(expiresAt)) {
        throw new InvalidOAuth2RefreshTokenException();
      }

      OAuth2AccessToken accessToken =
          createOAuth2AccessToken(
              username, (scopeClaim != null) ? Set.of(scopeClaim.split(" ")) : null);

      if (Instant.now().isBefore(expiresAt.minusSeconds(REFRESH_TOKEN_REISSUE_INTERVAL))) {
        return new RefreshedOAuth2Tokens(accessToken, null);
      } else {
        OAuth2RefreshToken refreshToken =
            createOAuth2RefreshToken(
                username, (scopeClaim != null) ? Set.of(scopeClaim.split(" ")) : null);

        return new RefreshedOAuth2Tokens(accessToken, refreshToken);
      }
    } catch (InvalidOAuth2RefreshTokenException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to refresh the OAuth tokens", e);
    }
  }

  private OAuth2AccessToken createOAuth2AccessToken(String username, Set<String> scopes)
      throws TokenCreationException {
    try {
      Optional<UUID> userDirectoryIdOptional = securityService.getUserDirectoryIdForUser(username);

      if (userDirectoryIdOptional.isEmpty()) {
        throw new TokenCreationException(
            "Failed to retrieve the user directory ID for the user (" + username + ")");
      } else {
        UUID userDirectoryId = userDirectoryIdOptional.get();

        // Retrieve the details for the user
        User user = securityService.getUser(userDirectoryId, username);

        // Retrieve the function codes for the user
        List<String> functionCodes =
            securityService.getFunctionCodesForUser(userDirectoryId, username);

        // Retrieve the list of IDs for the tenants the user is associated with
        List<UUID> tenantIds = securityService.getTenantIdsForUserDirectory(userDirectoryId);

        // Retrieve the list of roles for the user
        List<String> roleCodes = securityService.getRoleCodesForUser(userDirectoryId, username);

        // Build the OAuth2 access token
        return OAuth2AccessToken.build(
            user,
            roleCodes,
            functionCodes,
            tenantIds,
            scopes,
            applicationName,
            ACCESS_TOKEN_VALIDITY,
            rsaPrivateKey);
      }
    } catch (Throwable e) {
      throw new TokenCreationException("Failed to create the OAuth2 access token", e);
    }
  }

  private OAuth2RefreshToken createOAuth2RefreshToken(String username, Set<String> scopes)
      throws TokenCreationException {
    try {
      return OAuth2RefreshToken.build(username, scopes, REFRESH_TOKEN_VALIDITY, rsaPrivateKey);
    } catch (Throwable e) {
      throw new TokenCreationException("Failed to create the OAuth2 refresh token", e);
    }
  }
}
