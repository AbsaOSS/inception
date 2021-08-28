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

import {Component} from '@angular/core';

/**
 * The ListsComponent class implements the lists component.
 *
 * @author Marcus Portmann
 */
@Component({
  templateUrl: 'lists.component.html'
})
export class ListsComponent {

  folders = [{
    name: 'Photos',
    updated: new Date('1/1/16'),
  }, {
    name: 'Recipes',
    updated: new Date('1/17/16'),
  }, {
    name: 'Work',
    updated: new Date('1/28/16'),
  }
  ];
  notes = [{
    name: 'Vacation Itinerary',
    updated: new Date('2/20/16'),
  }, {
    name: 'Kitchen Remodel',
    updated: new Date('1/18/16'),
  }
  ];
  planets: any[] = [{
    name: 'Jupiter',
    image: 'assets/images/planets/1.png',
    description: 'Fifth planet',
    detail: 'Jupiter is the fifth planet from the Sun and the largest in the Solar System'
  }, {
    name: 'Venus',
    image: 'assets/images/planets/2.png',
    description: 'Second planet',
    detail: 'Venus is the second planet from the Sun, orbiting it every 224.7 Earth days'
  }, {
    name: 'Mars',
    image: 'assets/images/planets/3.png',
    description: 'Fourth planet',
    detail: 'Mars is the fourth planet from the Sun and the second-smallest planet'
  }
  ];
  typesOfShoes = ['Dogs', 'Cats', 'Birds', 'Hamsters', 'Ponies'];

  constructor() {
  }
}
