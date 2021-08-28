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

package africa.absa.inception.mail.test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import africa.absa.inception.core.util.ResourceUtil;
import africa.absa.inception.mail.IMailService;
import africa.absa.inception.mail.MailTemplate;
import africa.absa.inception.mail.MailTemplateContentType;
import africa.absa.inception.mail.MailTemplateNotFoundException;
import africa.absa.inception.mail.MailTemplateSummary;
import africa.absa.inception.test.InceptionExtension;
import africa.absa.inception.test.TestConfiguration;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

/**
 * The <b>MailServiceTest</b> class contains the implementation of the JUnit tests for the
 * <b>MailService</b> class.
 *
 * @author Marcus Portmann
 */
@ExtendWith(SpringExtension.class)
@ExtendWith(InceptionExtension.class)
@ContextConfiguration(
    classes = {TestConfiguration.class},
    initializers = {ConfigDataApplicationContextInitializer.class})
@TestExecutionListeners(
    listeners = {
      DependencyInjectionTestExecutionListener.class,
      DirtiesContextTestExecutionListener.class,
      TransactionalTestExecutionListener.class
    })
public class MailServiceTest {

  private static int mailTemplateCount;

  /** The Mail Service. */
  @Autowired private IMailService mailService;

  private static synchronized MailTemplate getTestMailTemplateDetails() {
    mailTemplateCount++;

    byte[] testMailTemplate =
        ResourceUtil.getClasspathResource("africa/absa/inception/mail/test/TestMailTemplate");

    MailTemplate mailTemplate = new MailTemplate();
    mailTemplate.setId("TestMailTemplate" + mailTemplateCount);
    mailTemplate.setName("Test Mail Template " + mailTemplateCount);
    mailTemplate.setContentType(MailTemplateContentType.HTML);
    mailTemplate.setTemplate(testMailTemplate);

    return mailTemplate;
  }

  /** Test the mail template functionality. */
  @Test
  public void mailTemplateTest() throws Exception {
    MailTemplate mailTemplate = getTestMailTemplateDetails();

    mailService.createMailTemplate(mailTemplate);

    MailTemplate retrievedMailTemplate = mailService.getMailTemplate(mailTemplate.getId());

    compareMailTemplates(mailTemplate, retrievedMailTemplate);

    boolean mailTemplateExists = mailService.mailTemplateExists(mailTemplate.getId());

    assertTrue(mailTemplateExists, "The mail template does not exist");

    String retrievedMailTemplateName = mailService.getMailTemplateName(mailTemplate.getId());

    assertEquals(
        mailTemplate.getName(),
        retrievedMailTemplateName,
        "The correct mail template name was not retrieved");

    mailTemplate.setName("Updated " + mailTemplate.getName());

    mailService.updateMailTemplate(mailTemplate);

    retrievedMailTemplate = mailService.getMailTemplate(mailTemplate.getId());

    compareMailTemplates(mailTemplate, retrievedMailTemplate);

    mailTemplateExists = mailService.mailTemplateExists(mailTemplate.getId());

    assertTrue(mailTemplateExists, "The updated mail template does not exist");

    List<MailTemplate> mailTemplates = mailService.getMailTemplates();

    assertEquals(1, mailTemplates.size(), "The correct number of mail templates was not retrieved");

    compareMailTemplates(mailTemplate, mailTemplates.get(0));

    MailTemplateSummary retrievedMailTemplateSummary =
        mailService.getMailTemplateSummary(mailTemplate.getId());

    compareMailTemplateToMailTemplateSummary(mailTemplate, retrievedMailTemplateSummary);

    List<MailTemplateSummary> mailTemplateSummaries = mailService.getMailTemplateSummaries();

    assertEquals(
        1,
        mailTemplateSummaries.size(),
        "The correct number of mail template summaries was not retrieved");

    compareMailTemplateToMailTemplateSummary(mailTemplate, mailTemplateSummaries.get(0));

    Map<String, String> mapTemplateParameters = new HashMap<>();
    mapTemplateParameters.put("name", "Joe Bloggs");

    System.out.println(
        mailService.processMailTemplate(mailTemplate.getId(), mapTemplateParameters));

    mailService.deleteMailTemplate(mailTemplate.getId());

    try {
      mailService.getMailTemplate(mailTemplate.getId());

      fail("The mail template that should have been deleted was retrieved successfully");
    } catch (MailTemplateNotFoundException ignored) {
    }
  }

  /** Test the send mail functionality. */
  // @Test
  public void sendMailTest() throws Exception {
    MailTemplate mailTemplate = getTestMailTemplateDetails();

    mailService.createMailTemplate(mailTemplate);

    Map<String, String> mapTemplateParameters = new HashMap<>();
    mapTemplateParameters.put("name", "Joe Bloggs");

    mailService.sendMail(
        Collections.singletonList("test@test.com"),
        "Test Subject",
        "no-reply@inception.digital",
        "Inception",
        mailTemplate.getId(),
        mapTemplateParameters);
  }

  private void compareMailTemplateToMailTemplateSummary(
      MailTemplate mailTemplate, MailTemplateSummary mailTemplateSummary) {
    assertEquals(
        mailTemplate.getId(),
        mailTemplateSummary.getId(),
        "The ID values for the mail template summaries do not match");
    assertEquals(
        mailTemplate.getName(),
        mailTemplateSummary.getName(),
        "The name values for the mail template summaries do not match");
  }

  private void compareMailTemplates(MailTemplate mailTemplate1, MailTemplate mailTemplate2) {
    assertEquals(
        mailTemplate1.getId(),
        mailTemplate2.getId(),
        "The ID values for the mail templates do not match");
    assertEquals(
        mailTemplate1.getName(),
        mailTemplate2.getName(),
        "The name values for the mail templates do not match");
    assertEquals(
        mailTemplate1.getContentType(),
        mailTemplate2.getContentType(),
        "The content type values for the mail templates do not match");
    assertArrayEquals(
        mailTemplate1.getTemplate(),
        mailTemplate2.getTemplate(),
        "The template values for the mail templates do not match");
  }
}
