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

package africa.absa.inception.mail;

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * The <b>IMailService</b> interface defines the functionality provided by a Mail Service
 * implementation.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public interface IMailService {

  /**
   * Create the new mail template.
   *
   * @param mailTemplate the <b>MailTemplate</b> instance containing the information for the new
   *     mail template
   * @return the mail template
   */
  MailTemplate createMailTemplate(MailTemplate mailTemplate)
      throws InvalidArgumentException, DuplicateMailTemplateException, ServiceUnavailableException;

  /**
   * Delete the existing mail template.
   *
   * @param mailTemplateId the ID for the mail template
   */
  void deleteMailTemplate(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the mail template.
   *
   * @param mailTemplateId the ID for the mail template
   * @return the mail template
   */
  MailTemplate getMailTemplate(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the name of the mail template.
   *
   * @param mailTemplateId the ID for the mail template
   * @return the name of the mail template
   */
  String getMailTemplateName(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException;

  /**
   * Returns the summaries for all the mail templates.
   *
   * @return the summaries for all the mail templates
   */
  List<MailTemplateSummary> getMailTemplateSummaries() throws ServiceUnavailableException;

  /**
   * Retrieve the summary for the mail template.
   *
   * @param mailTemplateId the ID for the mail template
   * @return the summary for the mail template
   */
  MailTemplateSummary getMailTemplateSummary(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException;

  /**
   * Returns the date and time the mail template was last updated.
   *
   * @param mailTemplateId the ID for the mail template
   * @return the date and time the mail template was last updated
   */
  LocalDateTime getMailTemplateUpdated(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException;

  /**
   * Returns all the mail templates.
   *
   * @return all the mail templates
   */
  List<MailTemplate> getMailTemplates() throws ServiceUnavailableException;

  /**
   * Check whether the mail template exists.
   *
   * @param mailTemplateId the ID for the mail template
   * @return <b>true</b> if the mail template exists or <b>false</b> otherwise
   */
  boolean mailTemplateExists(String mailTemplateId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Process the mail template.
   *
   * @param mailTemplateId the ID for the mail template
   * @param templateParameters the template parameters
   * @return the output of processing the template
   */
  String processMailTemplate(String mailTemplateId, Map<String, String> templateParameters)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Send a mail.
   *
   * @param to the list of e-mail addresses to send the mail to
   * @param subject the subject for the mail
   * @param from the from e-mail address
   * @param fromName the from e-mail name
   * @param mailTemplateId the ID for the mail template
   * @param mailTemplateParameters the parameters to apply to the mail template
   */
  void sendMail(
      List<String> to,
      String subject,
      String from,
      String fromName,
      String mailTemplateId,
      Map<String, String> mailTemplateParameters)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException;

  /**
   * Update the mail template.
   *
   * @param mailTemplate the <b>MailTemplate</b> instance containing the updated information for the
   *     mail template
   * @param the mail template
   */
  MailTemplate updateMailTemplate(MailTemplate mailTemplate)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException;
}
