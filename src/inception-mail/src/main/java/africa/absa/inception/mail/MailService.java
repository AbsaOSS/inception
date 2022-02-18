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
import africa.absa.inception.core.service.ValidationError;
import freemarker.cache.TemplateLookupContext;
import freemarker.cache.TemplateLookupResult;
import freemarker.cache.TemplateLookupStrategy;
import freemarker.template.Configuration;
import freemarker.template.Template;
import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * The <b>MailService</b> class provides the Mail Service implementation.
 *
 * @author Marcus Portmann
 */
@Service
@SuppressWarnings({"unused"})
public class MailService implements IMailService, InitializingBean {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(MailService.class);

  /** The Spring application context. */
  private final ApplicationContext applicationContext;

  /** The Apache FreeMarker configuration., */
  private final Configuration freeMarkerConfiguration;

  /** The Mail Template Repository. */
  private final MailTemplateRepository mailTemplateRepository;

  /** The Mail Template Summary Repository. */
  private final MailTemplateSummaryRepository mailTemplateSummaryRepository;

  /** The JSR-303 validator. */
  private final Validator validator;

  /** The Java mail sender. */
  private JavaMailSender javaMailSender;

  /**
   * Constructs a new <b>MailService</b>.
   *
   * @param applicationContext the Spring application context
   * @param validator the JSR-303 validator
   * @param mailTemplateRepository the Mail Template Repository
   * @param mailTemplateSummaryRepository the Mail Template Summary Repository
   */
  public MailService(
      ApplicationContext applicationContext,
      Validator validator,
      MailTemplateRepository mailTemplateRepository,
      MailTemplateSummaryRepository mailTemplateSummaryRepository) {
    this.applicationContext = applicationContext;
    this.validator = validator;
    this.mailTemplateRepository = mailTemplateRepository;
    this.mailTemplateSummaryRepository = mailTemplateSummaryRepository;

    this.freeMarkerConfiguration = new Configuration(Configuration.VERSION_2_3_29);
    this.freeMarkerConfiguration.setTemplateLoader(new FreeMarkerTemplateLoader(this));
    this.freeMarkerConfiguration.setTemplateLookupStrategy(
        new TemplateLookupStrategy() {
          @Override
          public TemplateLookupResult lookup(TemplateLookupContext templateLookupContext)
              throws IOException {
            return templateLookupContext.lookupWithAcquisitionStrategy(
                templateLookupContext.getTemplateName());
          }

          @Override
          public String toString() {
            return "MailServiceLookupStrategy";
          }
        });
  }

  @Override
  public void afterPropertiesSet() {
    try {
      javaMailSender = applicationContext.getBean(JavaMailSender.class);
    } catch (NoSuchBeanDefinitionException ignored) {
      logger.warn("No JavaMailSender implementation found");
    }
  }

  @Override
  @Transactional
  @CachePut(cacheNames = "mailTemplates", key = "#mailTemplate.id")
  public MailTemplate createMailTemplate(MailTemplate mailTemplate)
      throws InvalidArgumentException, DuplicateMailTemplateException, ServiceUnavailableException {
    validateMailTemplate(mailTemplate);

    try {
      if (mailTemplateRepository.existsById(mailTemplate.getId())) {
        throw new DuplicateMailTemplateException(mailTemplate.getId());
      }

      mailTemplateRepository.saveAndFlush(mailTemplate);

      return mailTemplate;
    } catch (DuplicateMailTemplateException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to create the mail template (" + mailTemplate.getId() + ")", e);
    }
  }

  @Override
  @Transactional
  @CacheEvict(cacheNames = "mailTemplates", key = "#mailTemplateId")
  public void deleteMailTemplate(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    try {
      if (!mailTemplateRepository.existsById(mailTemplateId)) {
        throw new MailTemplateNotFoundException(mailTemplateId);
      }

      mailTemplateRepository.deleteById(mailTemplateId);

      /*
       * Clear the FreeMarker template cache. This could be optimized in future but given that
       * message templates are not updated on a continuous or even regular basis this is probably
       * sufficient for now.
       */
      freeMarkerConfiguration.clearTemplateCache();
    } catch (MailTemplateNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to delete the mail template (" + mailTemplateId + ")", e);
    }
  }

  @Override
  @Cacheable(cacheNames = "mailTemplates", key = "#mailTemplateId")
  public MailTemplate getMailTemplate(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    try {
      Optional<MailTemplate> mailTemplateOptional = mailTemplateRepository.findById(mailTemplateId);

      if (mailTemplateOptional.isPresent()) {
        return mailTemplateOptional.get();
      } else {
        throw new MailTemplateNotFoundException(mailTemplateId);
      }
    } catch (MailTemplateNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the mail template (" + mailTemplateId + ")", e);
    }
  }

  @Override
  public LocalDateTime getMailTemplateLastModified(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    try {
      Optional<LocalDateTime> updatedOptional =
          mailTemplateRepository.getLastModifiedById(mailTemplateId);

      if (updatedOptional.isPresent()) {
        return updatedOptional.get();
      }

      throw new MailTemplateNotFoundException(mailTemplateId);
    } catch (MailTemplateNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the date and time the mail template ("
              + mailTemplateId
              + ") was last modified",
          e);
    }
  }

  @Override
  public String getMailTemplateName(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    try {
      Optional<String> nameOptional = mailTemplateRepository.getNameById(mailTemplateId);

      if (nameOptional.isPresent()) {
        return nameOptional.get();
      }

      throw new MailTemplateNotFoundException(mailTemplateId);
    } catch (MailTemplateNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the name of the mail template (" + mailTemplateId + ")", e);
    }
  }

  @Override
  public List<MailTemplateSummary> getMailTemplateSummaries() throws ServiceUnavailableException {
    try {
      return mailTemplateSummaryRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the summaries for the mail templates", e);
    }
  }

  @Override
  public MailTemplateSummary getMailTemplateSummary(String mailTemplateId)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException {
    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    try {
      Optional<MailTemplateSummary> mailTemplateSummaryOptional =
          mailTemplateSummaryRepository.findById(mailTemplateId);

      if (mailTemplateSummaryOptional.isPresent()) {
        return mailTemplateSummaryOptional.get();
      } else {
        throw new MailTemplateNotFoundException(mailTemplateId);
      }
    } catch (MailTemplateNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to retrieve the summary for the mail template (" + mailTemplateId + ")", e);
    }
  }

  @Override
  public List<MailTemplate> getMailTemplates() throws ServiceUnavailableException {
    try {
      return mailTemplateRepository.findAll();
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to retrieve the mail templates", e);
    }
  }

  @Override
  public boolean mailTemplateExists(String mailTemplateId)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    try {
      return mailTemplateRepository.existsById(mailTemplateId);
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to check whether the mail template (" + mailTemplateId + ") exists", e);
    }
  }

  @Override
  public String processMailTemplate(String mailTemplateId, Map<String, String> templateParameters)
      throws InvalidArgumentException, ServiceUnavailableException {
    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    if (templateParameters == null) {
      throw new InvalidArgumentException("templateParameters");
    }

    try {
      Template template = freeMarkerConfiguration.getTemplate(mailTemplateId);

      StringWriter sw = new StringWriter();
      template.process(templateParameters, sw);

      return sw.toString();
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to process the mail template (" + mailTemplateId + ")", e);
    }
  }

  public void sendMail(
      List<String> to,
      String subject,
      String from,
      String fromName,
      String mailTemplateId,
      Map<String, String> mailTemplateParameters)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException {
    if (to == null) {
      throw new InvalidArgumentException("to");
    }

    if (!StringUtils.hasText(subject)) {
      throw new InvalidArgumentException("subject");
    }

    if (!StringUtils.hasText(from)) {
      throw new InvalidArgumentException("from");
    }

    if (!StringUtils.hasText(fromName)) {
      throw new InvalidArgumentException("fromName");
    }

    if (!StringUtils.hasText(mailTemplateId)) {
      throw new InvalidArgumentException("mailTemplateId");
    }

    if (mailTemplateParameters == null) {
      throw new InvalidArgumentException("mailTemplateParameters");
    }

    try {
      // Retrieve the mail template
      Optional<MailTemplate> mailTemplateOptional = mailTemplateRepository.findById(mailTemplateId);

      if (mailTemplateOptional.isEmpty()) {
        throw new MailTemplateNotFoundException(mailTemplateId);
      }

      MailTemplate mailTemplate = mailTemplateOptional.get();

      if (javaMailSender != null) {
        // Send the e-mail message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

        helper.setFrom(new InternetAddress(from, fromName));

        helper.setTo(to.toArray(new String[0]));

        helper.setSubject(subject);

        helper.setText(
            processMailTemplate(mailTemplate.getId(), mailTemplateParameters),
            mailTemplate.getContentType() == MailTemplateContentType.HTML);

        javaMailSender.send(helper.getMimeMessage());
      } else {
        throw new ServiceUnavailableException("No JavaMailSender bean has been configured");
      }
    } catch (MailTemplateNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException("Failed to send the mail", e);
    }
  }

  @Override
  @Transactional
  @CachePut(cacheNames = "mailTemplates", key = "#mailTemplate.id")
  public MailTemplate updateMailTemplate(MailTemplate mailTemplate)
      throws InvalidArgumentException, MailTemplateNotFoundException, ServiceUnavailableException {
    validateMailTemplate(mailTemplate);

    try {
      if (!mailTemplateRepository.existsById(mailTemplate.getId())) {
        throw new MailTemplateNotFoundException(mailTemplate.getId());
      }

      mailTemplateRepository.saveAndFlush(mailTemplate);

      /*
       * Clear the FreeMarker template cache. This could be optimized in future but given that
       * message templates are not updated on a continuous or even regular basis this is probably
       * sufficient for now.
       */
      freeMarkerConfiguration.clearTemplateCache();

      return mailTemplate;
    } catch (MailTemplateNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new ServiceUnavailableException(
          "Failed to update the mail template (" + mailTemplate.getId() + ")", e);
    }
  }

  private void validateMailTemplate(MailTemplate mailTemplate) throws InvalidArgumentException {
    if (mailTemplate == null) {
      throw new InvalidArgumentException("mailTemplate");
    }

    Set<ConstraintViolation<MailTemplate>> constraintViolations = validator.validate(mailTemplate);

    if (!constraintViolations.isEmpty()) {
      throw new InvalidArgumentException(
          "mailTemplate", ValidationError.toValidationErrors(constraintViolations));
    }
  }
}
