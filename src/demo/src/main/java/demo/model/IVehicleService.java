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

import africa.absa.inception.core.service.InvalidArgumentException;
import africa.absa.inception.core.service.ServiceUnavailableException;
import africa.absa.inception.core.sorting.SortDirection;

/**
 * The <b>IVehicleService</b> interface defines the functionality that must be provided by a Vehicle
 * Service implementation.
 *
 * @author Marcus Portmann
 */
@SuppressWarnings("unused")
public interface IVehicleService {

  /**
   * Create the new car.
   *
   * @param car the car
   */
  void createCar(Car car)
      throws InvalidArgumentException, DuplicateCarException, ServiceUnavailableException;

  /**
   * Create the new vehicle.
   *
   * @param vehicle the vehicle
   */
  void createVehicle(Vehicle vehicle)
      throws InvalidArgumentException, DuplicateVehicleException, ServiceUnavailableException;

  /**
   * Retrieve the cars.
   *
   * @param filter the optional filter to apply to the cars
   * @param sortDirection the optional sort direction to apply to the cars
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the cars
   */
  Cars getCars(String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException;

  /**
   * Retrieve the vehicles.
   *
   * @param filter the optional filter to apply to the vehicles
   * @param sortDirection the optional sort direction to apply to the vehicles
   * @param pageIndex the optional page index
   * @param pageSize the optional page size
   * @return the vehicles
   */
  Vehicles getVehicles(
      String filter, SortDirection sortDirection, Integer pageIndex, Integer pageSize)
      throws InvalidArgumentException, ServiceUnavailableException;
}
