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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * The <b>ICodeProvider</b> interface defines the interface that must be implemented by all custom
 * code providers.
 *
 * @author Marcus Portmann
 */
public interface ICodeProvider {

  /**
   * Returns whether the code provider supports the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return <b>true</b> if the code provider supports the code category or <b>false</b> otherwise
   */
  boolean codeCategoryExists(String codeCategoryId) throws CodeProviderException;

  /**
   * Check whether the code exists.
   *
   * @param codeCategoryId the ID for the code category
   * @param codeId the ID for the code
   * @return <b>true</b> if the code exists or <b>false</b> otherwise
   */
  boolean codeExists(String codeCategoryId, String codeId) throws CodeProviderException;

  /**
   * Retrieve the code.
   *
   * @param codeCategoryId the ID for the code category
   * @param codeId the ID for the code
   * @return the code
   */
  Code getCode(String codeCategoryId, String codeId)
      throws CodeNotFoundException, CodeProviderException;

  /**
   * Returns all the code categories for the code provider.
   *
   * @return all the code categories for the code provider
   */
  List<CodeCategory> getCodeCategories() throws CodeProviderException;

  /**
   * Retrieve the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return the code category
   */
  CodeCategory getCodeCategory(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException;

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
      throws CodeCategoryNotFoundException, CodeProviderException;

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
      throws CodeCategoryNotFoundException, CodeProviderException;

  /**
   * Returns the date and time the code category was last updated.
   *
   * @param codeCategoryId the ID for the code category
   * @return the date and time the code category was last updated
   */
  LocalDateTime getCodeCategoryLastUpdated(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException;

  /**
   * Retrieve the name of the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return the name of the code category
   */
  String getCodeCategoryName(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException;

  /**
   * Retrieve the name of the code.
   *
   * @param codeCategoryId the ID for the code category
   * @param codeId the ID for the code
   * @return the name of code
   */
  String getCodeName(String codeCategoryId, String codeId)
      throws CodeNotFoundException, CodeProviderException;

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
      throws CodeCategoryNotFoundException, CodeProviderException;

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
      throws CodeCategoryNotFoundException, CodeProviderException;
}
