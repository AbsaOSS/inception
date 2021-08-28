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

package africa.absa.inception.api;

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.Problem;
import africa.absa.inception.core.service.ServiceException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * The <b>ProblemHandler</b> class implements the error handler that returns errors raised by
 * RESTful controllers as RFC 7807 JSON problem details objects.
 *
 * @author Marcus Portmann
 */
@ControllerAdvice
@SuppressWarnings("unused")
public class ProblemHandler {

  /* Logger */
  private static final Logger logger = LoggerFactory.getLogger(ProblemHandler.class);

  /** Is debugging enabled for the Inception Framework? */
  @Value("${inception.debug.enabled:#{false}}")
  private boolean inDebugMode;

  public ProblemHandler() {}

  @ExceptionHandler
  @ResponseBody
  protected ResponseEntity<ProblemDetails> handle(
      HttpServletRequest request, ServiceException serviceException) {

    if (inDebugMode) {
      logger.error(
          "Failed to process the HTTP servlet request (" + request.getRequestURI() + ")",
          serviceException);
    }

    ProblemDetails problemDetails = new ProblemDetails();

    problemDetails.setTimestamp(serviceException.getTimestamp());

    Problem problem =
        AnnotatedElementUtils.findMergedAnnotation(serviceException.getClass(), Problem.class);

    if (problem != null) {
      problemDetails.setType(problem.type());
      problemDetails.setTitle(problem.title());
      problemDetails.setStatus(problem.status());

    } else if (serviceException instanceof InvalidArgumentException) {
      problemDetails.setType("http://inception.absa.africa/problems/invalid-argument");
      problemDetails.setTitle("Invalid argument.");
      problemDetails.setStatus(HttpStatus.BAD_REQUEST.value());

      InvalidArgumentException invalidArgumentException =
          (InvalidArgumentException) serviceException;

      problemDetails.setParameter(invalidArgumentException.getParameter());
      problemDetails.setValidationErrors(invalidArgumentException.getValidationErrors());

    } else if (serviceException instanceof ServiceUnavailableException) {
      problemDetails.setType("http://inception.absa.africa/problems/service-unavailable");
      problemDetails.setTitle(
          "An error has occurred and your request could not be processed at this time.");
      problemDetails.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

    } else {
      problemDetails.setType("about:blank");
      problemDetails.setTitle(
          "An error has occurred and your request could not be processed at this time.");
      problemDetails.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }

    problemDetails.setDetail(serviceException.getMessage());

    if (inDebugMode) {
      logger.error(serviceException.getMessage(), serviceException);

      problemDetails.setStackTrace(dumpStackTrace(serviceException));
    }

    return new ResponseEntity<>(problemDetails, new HttpHeaders(), problemDetails.getStatus());
  }

  @ExceptionHandler
  @ResponseBody
  protected ResponseEntity<ProblemDetails> handle(
      HttpServletRequest request, AccessDeniedException accessDeniedException) {

    ProblemDetails problemDetails = new ProblemDetails();

    problemDetails.setTimestamp(LocalDateTime.now());
    problemDetails.setType("http://inception.absa.africa/problems/access-denied");
    problemDetails.setTitle("Access denied.");
    problemDetails.setStatus(HttpStatus.FORBIDDEN.value());
    problemDetails.setDetail(accessDeniedException.getMessage());

    if (inDebugMode) {
      problemDetails.setStackTrace(dumpStackTrace(accessDeniedException));
    }

    return new ResponseEntity<>(problemDetails, new HttpHeaders(), problemDetails.getStatus());
  }

  @ExceptionHandler
  @ResponseBody
  protected ResponseEntity<ProblemDetails> handle(HttpServletRequest request, Throwable cause) {

    if (inDebugMode) {
      logger.error(
          "Failed to process the HTTP servlet request (" + request.getRequestURI() + ")", cause);
    }

    ProblemDetails problemDetails = new ProblemDetails();

    problemDetails.setTimestamp(LocalDateTime.now());
    problemDetails.setType("about:blank");
    problemDetails.setTitle(
        "An error has occurred and your request could not be processed at this time.");
    problemDetails.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
    problemDetails.setDetail(cause.getMessage());

    if (inDebugMode) {
      problemDetails.setStackTrace(dumpStackTrace(cause));
    }

    return new ResponseEntity<>(problemDetails, new HttpHeaders(), problemDetails.getStatus());
  }

  private String dumpStackTrace(Throwable throwable) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      PrintWriter pw = new PrintWriter(baos);

      throwable.printStackTrace(pw);

      pw.flush();

      return baos.toString();
    } catch (Throwable e) {
      return "Failed to retrieve the exception stack trace: " + e.getMessage();
    }
  }
}
