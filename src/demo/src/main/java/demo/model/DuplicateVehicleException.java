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

package demo.model;

import africa.absa.inception.core.service.Problem;
import africa.absa.inception.core.service.ServiceException;
import java.util.UUID;

/**
 * The <b>DuplicateVehicleException</b> exception is thrown to indicate an error condition as a
 * result of an attempt to create a duplicate vehicle.
 *
 * <p>This is a checked exception to prevent the automatic rollback of the current transaction.
 *
 * @author Marcus Portmann
 */
@Problem(
    type = "http://demo/problems/duplicate-vehicle-exception",
    title = "The vehicle already exists.",
    status = 409)
public class DuplicateVehicleException extends ServiceException {

  private static final long serialVersionUID = 1000000;

  /**
   * Constructs a new <b>DuplicateVehicleException</b>.
   *
   * @param id the Universally Unique Identifier (UUID) for the vehicle
   */
  public DuplicateVehicleException(UUID id) {
    super("The vehicle with the ID (" + id + ") already exists");
  }
}
