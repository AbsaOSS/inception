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

/**
 * The ErrorReport class holds the information for an error report.
 *
 * @author Marcus Portmann
 */
export class ErrorReport {

  /**
   * The ID for the application that generated the error report.
   */
  applicationId: string;

  /**
   * The version of the application that generated the error report.
   */
  applicationVersion: string;

  /**
   * The date and time the error report was created.
   */
  created: Date;

  /**
   * The optional base-64 encoded data associated with the error report.
   */
  data?: string;

  /**
   * The description of the error.
   */
  description: string;

  /**
   * The error detail.
   */
  detail: string;

  /**
   * The optional ID for the device the error report originated
   * from.
   */
  deviceId?: string;

  /**
   * The optional feedback provided by the user for the error.
   */
  feedback?: string;

  /**
   * The ID for the error report.
   */
  id: string;

  /**
   * The optional username for the user associated with the error report.
   */
  who?: string;

  /**
   * Constructs a new ErrorReport.
   *
   * @param id                 The ID for the error report.
   * @param applicationId      The ID for the application that generated the error report.
   * @param applicationVersion The version of the application that generated the error report.
   * @param description        The description of the error.
   * @param detail             The error detail.
   * @param created            The date and time the error report was created.
   * @param who                The optional username for the user associated with the error report.
   * @param feedback           The optional feedback provided by the user for the error.
   * @param data               The optional base-64 encoded data associated with the error report.
   */
  constructor(id: string, applicationId: string, applicationVersion: string, description: string, detail: string,
              created: Date, who?: string, feedback?: string, data?: string) {
    this.id = id;
    this.applicationId = applicationId;
    this.applicationVersion = applicationVersion;
    this.description = description;
    this.detail = detail;
    this.created = created;
    this.who = who;
    this.feedback = feedback;
    this.data = data;
  }
}
