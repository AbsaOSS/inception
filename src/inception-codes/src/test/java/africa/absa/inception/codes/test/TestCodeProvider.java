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

package africa.absa.inception.codes.test;

import africa.absa.inception.codes.Code;
import africa.absa.inception.codes.CodeCategory;
import africa.absa.inception.codes.CodeCategoryNotFoundException;
import africa.absa.inception.codes.CodeNotFoundException;
import africa.absa.inception.codes.CodeProviderConfig;
import africa.absa.inception.codes.CodeProviderException;
import africa.absa.inception.codes.ICodeProvider;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * The <b>TestCodeProvider</b> class.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public class TestCodeProvider implements ICodeProvider {

  private final CodeCategory codeCategory;

  private final List<Code> codes;

  /**
   * Constructs a new <b>TestCodeProvider</b>.
   *
   * @param codeProviderConfig the configuration for the code provider
   */
  public TestCodeProvider(CodeProviderConfig codeProviderConfig) {
    codeCategory =
        new CodeCategory("TestCodeCategory", "Test Code Category Name", "Test Code Category Data");

    codes = new ArrayList<>();

    codes.add(new Code("TestCode1", codeCategory.getId(), "Test Code Name 1", "Test Code Value 1"));
    codes.add(new Code("TestCode2", codeCategory.getId(), "Test Code Name 2", "Test Code Value 2"));
    codes.add(new Code("TestCode3", codeCategory.getId(), "Test Code Name 3", "Test Code Value 3"));
  }

  /**
   * Returns whether the code provider supports the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return <b>true</b> if the code provider supports the code category or <b>false</b> otherwise
   */
  @Override
  public boolean codeCategoryExists(String codeCategoryId) throws CodeProviderException {
    try {
      return codeCategory.getId().equals(codeCategoryId);
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format(
              "Failed to check whether the code provider supports the code category (%s)",
              codeCategoryId),
          e);
    }
  }

  /**
   * Check whether the code exists.
   *
   * @param codeCategoryId the ID for the code category
   * @param codeId the ID for the code
   * @return <b>true</b> if the code exists or <b>false</b> otherwise
   */
  @Override
  public boolean codeExists(String codeCategoryId, String codeId) throws CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        for (Code code : codes) {
          if (code.getId().equals(codeId)) {
            return true;
          }
        }
      }

      return false;
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format(
              "Failed to check whether the code (%s) exists for the code category (%s)",
              codeId, codeCategoryId),
          e);
    }
  }

  /**
   * Retrieve the code.
   *
   * @param codeCategoryId the ID for the code category
   * @param codeId the ID for the code
   * @return the code
   */
  @Override
  public Code getCode(String codeCategoryId, String codeId)
      throws CodeNotFoundException, CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        for (Code code : codes) {
          if (code.getId().equals(codeId)) {
            return code;
          }
        }
      }
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format(
              "Failed to retrieve the code (%s) for the code category (%s)",
              codeId, codeCategoryId),
          e);
    }

    throw new CodeNotFoundException(codeCategoryId, codeId);
  }

  /**
   * Returns all the code categories for the code provider.
   *
   * @return all the code categories for the code provider
   */
  @Override
  public List<CodeCategory> getCodeCategories() throws CodeProviderException {
    try {
      List<CodeCategory> codeCategories = new ArrayList<>();

      codeCategories.add(codeCategory);

      return codeCategories;
    } catch (Throwable e) {
      throw new CodeProviderException("Failed to retrieve code categories", e);
    }
  }

  /**
   * Retrieve the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return the code category
   */
  @Override
  public CodeCategory getCodeCategory(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        return codeCategory;
      }

      throw new CodeCategoryNotFoundException(codeCategoryId);
    } catch (CodeCategoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format("Failed to retrieve the code category (%s)", codeCategoryId), e);
    }
  }

  /**
   * Retrieve the XML or JSON data for the code category.
   *
   * <p>This will also attempt to retrieve the data from the appropriate code provider that has been
   * registered with the Codes Service in the <b>META-INF/code-providers.xml</b> configuration file.
   *
   * @param codeCategoryId the ID for the code category
   * @return the XML or JSON data for the code category
   */
  @Override
  public String getCodeCategoryData(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        return "<codes><code name=\"name1\" value=\"value1\"/></codes>";
      }

      throw new CodeCategoryNotFoundException(codeCategoryId);
    } catch (CodeCategoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format("Failed to retrieve the data for the code category (%s)", codeCategoryId),
          e);
    }
  }

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
  @Override
  public String getCodeCategoryDataWithParameters(
      String codeCategoryId, Map<String, String> parameters)
      throws CodeCategoryNotFoundException, CodeProviderException {
    return getCodeCategoryData(codeCategoryId);
  }

  /**
   * Returns the date and time the code category was last updated.
   *
   * @param codeCategoryId the ID for the code category
   * @return the date and time the code category was last updated
   */
  @Override
  public LocalDateTime getCodeCategoryLastUpdated(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        return codeCategory.getUpdated();
      }

      throw new CodeCategoryNotFoundException(codeCategoryId);
    } catch (CodeCategoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format(
              "Failed to retrieve the date and time the code category (%s) was last updated",
              codeCategoryId),
          e);
    }
  }

  /**
   * Retrieve the name of the code category.
   *
   * @param codeCategoryId the ID for the code category
   * @return the name of the code category
   */
  @Override
  public String getCodeCategoryName(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        return codeCategory.getName();
      }

      throw new CodeCategoryNotFoundException(codeCategoryId);
    } catch (CodeCategoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format("Failed to retrieve the code category name (%s)", codeCategoryId), e);
    }
  }

  /**
   * Retrieve the name of the code.
   *
   * @param codeCategoryId the ID for the code category
   * @param codeId the ID for the code
   * @return the name of code
   */
  @Override
  public String getCodeName(String codeCategoryId, String codeId)
      throws CodeNotFoundException, CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        for (Code code : codes) {
          if (code.getId().equals(codeId)) {
            return code.getName();
          }
        }
      }
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format(
              "Failed to retrieve the code (%s) for the code category (%s)",
              codeId, codeCategoryId),
          e);
    }

    throw new CodeNotFoundException(codeCategoryId, codeId);
  }

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
  @Override
  public List<Code> getCodesForCodeCategory(String codeCategoryId)
      throws CodeCategoryNotFoundException, CodeProviderException {
    try {
      if (codeCategory.getId().equals(codeCategoryId)) {
        return codes;
      }

      throw new CodeCategoryNotFoundException(codeCategoryId);
    } catch (CodeCategoryNotFoundException e) {
      throw e;
    } catch (Throwable e) {
      throw new CodeProviderException(
          String.format("Failed to retrieve the codes for the code category (%s)", codeCategoryId),
          e);
    }
  }

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
  @Override
  public List<Code> getCodesForCodeCategoryWithParameters(
      String codeCategoryId, Map<String, String> parameters)
      throws CodeCategoryNotFoundException, CodeProviderException {
    return getCodesForCodeCategory(codeCategoryId);
  }
}
