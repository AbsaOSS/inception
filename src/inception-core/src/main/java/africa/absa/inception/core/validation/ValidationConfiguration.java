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

package africa.absa.inception.core.validation;

import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.springframework.beans.BeanUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

/**
 * The <b>ValidationConfiguration</b> class provides access to the JSR-303 validation configuration
 * and initialises the JSR-303 validator.
 *
 * @author Marcus Portmann
 */
@Configuration
public class ValidationConfiguration {

  @Bean
  public static LocalValidatorFactoryBean validator() {
    LocalValidatorFactoryBean factoryBean = new LocalValidatorFactoryBean();
    factoryBean.setMessageInterpolator(
        BeanUtils.instantiateClass(ParameterMessageInterpolator.class));
    return factoryBean;
  }
}
