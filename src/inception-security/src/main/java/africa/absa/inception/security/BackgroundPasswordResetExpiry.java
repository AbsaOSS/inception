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

package africa.absa.inception.security;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * The <b>BackgroundPasswordResetExpiry</b> class implements the background password reset expiry.
 *
 * @author Marcus Portmann
 */
@Service
public class BackgroundPasswordResetExpiry {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(BackgroundPasswordResetExpiry.class);

  /** The Password Reset Repository. */
  private final PasswordResetRepository passwordResetRepository;

  /** The password reset expiry in seconds */
  @Value("${inception.application.security.passwordResetExpiry:900}")
  private int passwordResetExpiry;

  /**
   * Constructs a new <b>BackgroundPasswordResetExpiry</b>.
   *
   * @param passwordResetRepository the Password Reset Repository
   */
  public BackgroundPasswordResetExpiry(PasswordResetRepository passwordResetRepository) {
    this.passwordResetRepository = passwordResetRepository;
  }

  /** Expire the password resets. */
  @Scheduled(cron = "0 * * * * *")
  @Transactional
  public void expirePasswordResets() {
    try {
      LocalDateTime requestedBefore = LocalDateTime.now();
      requestedBefore = requestedBefore.minus(passwordResetExpiry, ChronoUnit.SECONDS);

      passwordResetRepository.expirePasswordResets(LocalDateTime.now(), requestedBefore);
    } catch (Throwable e) {
      logger.error("Failed to expire the password resets", e);
    }
  }
}
