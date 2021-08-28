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

import {InjectionToken} from '@angular/core';

/**
 * The InceptionConfig interface defines the config that can be passed to the InceptionModule
 * module.
 *
 * @author Marcus Portmann
 */
export interface InceptionConfig {

  /**
   * The ID used to identify the application e.g. demo.
   */
  applicationId: string;

  /**
   * The semantic version number for the application e.g. 1.0.0.
   */
  applicationVersion: string;

  /**
   * The Codes API URL prefix.
   */
  codesApiUrlPrefix?: string;

  /**
   * The Config API URL prefix.
   */
  configApiUrlPrefix?: string;

  /**
   * The Error API URL prefix.
   */
  errorApiUrlPrefix?: string;

  /**
   * Is the forgotten password functionality enabled.
   */
  forgottenPasswordEnabled: boolean;

  /**
   * The application URI to redirect to on logout.
   */
  logoutRedirectUri: string;

  /**
   * The Mail API URL prefix.
   */
  mailApiUrlPrefix?: string;

  /**
   * The OAuth Token URL e.g. http://localhost:8080/oauth/token.
   */
  oauthTokenUrl: string;

  /**
   * The Party API URL prefix.
   */
  partyApiUrlPrefix?: string;

  /**
   * The Party Reference API URL prefix.
   */
  partyReferenceApiUrlPrefix?: string;

  /**
   * The password to prepopulate the password form control on the login component with for testing
   * purposes.
   */
  prepopulatedLoginPassword?: string;

  /**
   * The username to prepopulate the username form control on the login component with for testing
   * purposes.
   */
  prepopulatedLoginUsername?: string;

  /**
   * The Reference API URL prefix.
   */
  referenceApiUrlPrefix?: string;

  /**
   * The Reporting API URL prefix.
   */
  reportingApiUrlPrefix?: string;

  /**
   * The Scheduler API URL prefix.
   */
  schedulerApiUrlPrefix?: string;

  /**
   * The Security API URL prefix.
   */
  securityApiUrlPrefix?: string;

  /**
   * Is the user profile functionality enabled.
   */
  userProfileEnabled: boolean;
}

/**
 * The injection token for the InceptionConfig.
 */
export const INCEPTION_CONFIG = new InjectionToken<InceptionConfig>('INCEPTION_CONFIG');
