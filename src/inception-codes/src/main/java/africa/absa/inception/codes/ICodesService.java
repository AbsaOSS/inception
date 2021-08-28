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

package africa.absa.inception.codes;

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * The <b>ICodesService</b> interface defines the functionality provided by a Codes Service
 * implementation.
 *
 * @author Marcus Portmann
 */
public interface ICodesService {

  /**
   * Check whether the code category exists.
   *
   * @param codeCategoryId the ID for the code category
   * @return <b>true</b> if the code category exists or <b>false</b> otherwise
   */
  boolean codeCategoryExists(String codeCategoryId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Check whether the code exists.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   * @param codeId the ID for the code
   * @return <b>true</b> if the code exists or <b>false</b> otherwise
   */
  boolean codeExists(String codeCategoryId, String codeId)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Create the new code.
   *
   * @param code the <b>Code</b> instance containing the information for the new code
   */
  void createCode(Code code)
      throws InvalidArgumentException, DuplicateCodeException, CodeCategoryNotFoundException,
          ServiceUnavailableException;

  /**
   * Create the new code category.
   *
   * @param codeCategory the <b>CodeCategory</b> instance containing the information for the new
   *     code category
   */
  void createCodeCategory(CodeCategory codeCategory)
      throws InvalidArgumentException, DuplicateCodeCategoryException, ServiceUnavailableException;

  /**
   * Delete the code.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   * @param codeId the ID for the code
   */
  void deleteCode(String codeCategoryId, String codeId)
      throws InvalidArgumentException, CodeNotFoundException, ServiceUnavailableException;

  /**
   * Delete the code category.
   *
   * @param codeCategoryId the ID for the code category
   */
  void deleteCodeCategory(String codeCategoryId)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the code.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   * @param codeId the ID for the code
   * @return the code
   */
  Code getCode(String codeCategoryId, String codeId)
      throws InvalidArgumentException, CodeNotFoundException, ServiceUnavailableException;

  /**
   * Returns all the code categories.
   *
   * @return all the code categories
   */
  List<CodeCategory> getCodeCategories() throws ServiceUnavailableException;

  /**
   * Retrieve the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return the code category
   */
  CodeCategory getCodeCategory(String codeCategoryId)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the XML or JSON data for the code category.
   *
   * <p>This will also attempt to retrieve the data from the appropriate code provider that has been
   * registered with the Codes Service in the <b>META-INF/code-providers.xml</b> configuration file.
   *
   * @param codeCategoryId the ID for the code category
   * @return the XML or JSON data for the code category
   */
  String getCodeCategoryData(String codeCategoryId)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the XML or JSON data for the code category using the specified parameters.
   *
   * <p>This will also attempt to retrieve the data from the appropriate code provider that has been
   * registered with the Codes Service in the <b>META-INF/code-providers.xml</b> configuration file.
   *
   * @param codeCategoryId the ID for the code category
   * @param parameters the parameters
   * @return the XML or JSON data for the code category
   */
  String getCodeCategoryDataWithParameters(String codeCategoryId, Map<String, String> parameters)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the name of the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return the name of the code category
   */
  String getCodeCategoryName(String codeCategoryId)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Returns the summaries for all the code categories.
   *
   * @return the summaries for all the code categories
   */
  List<CodeCategorySummary> getCodeCategorySummaries() throws ServiceUnavailableException;

  /**
   * Returns the date and time the code category was last updated.
   *
   * @param codeCategoryId the ID for the code category
   * @return the date and time the code category was last updated
   */
  LocalDateTime getCodeCategoryUpdated(String codeCategoryId)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the name of the code.
   *
   * @param codeCategoryId the ID for the code category the code is associated with
   * @param codeId the ID for the code
   * @return the name of the code
   */
  String getCodeName(String codeCategoryId, String codeId)
      throws InvalidArgumentException, CodeNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the codes for the code category.
   *
   * <p>This will also attempt to retrieve the codes from the appropriate code provider that has
   * been registered with the Codes Service in the <b>META-INF/code-providers.xml</b> configuration
   * file.
   *
   * @param codeCategoryId the ID for the code category
   * @return the codes for the code category
   */
  List<Code> getCodesForCodeCategory(String codeCategoryId)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Retrieve the codes for the code category using the specified parameters.
   *
   * <p>This will also attempt to retrieve the codes from the appropriate code provider that has
   * been registered with the Codes Service in the <b>META-INF/code-providers.xml</b> configuration
   * file.
   *
   * @param codeCategoryId the ID for the code category
   * @param parameters the parameters
   * @return the codes for the code category
   */
  List<Code> getCodesForCodeCategoryWithParameters(
      String codeCategoryId, Map<String, String> parameters)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Update the existing code.
   *
   * @param code the <b>Code</b> instance containing the updated information for the code
   */
  void updateCode(Code code)
      throws InvalidArgumentException, CodeNotFoundException, ServiceUnavailableException;

  /**
   * Update the existing code category.
   *
   * @param codeCategory the <b>CodeCategory</b> instance containing the updated information for the
   *     code category
   */
  void updateCodeCategory(CodeCategory codeCategory)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;

  /**
   * Update the XML or JSON data for the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @param data the updated XML or JSON data
   */
  void updateCodeCategoryData(String codeCategoryId, String data)
      throws InvalidArgumentException, CodeCategoryNotFoundException, ServiceUnavailableException;
}
