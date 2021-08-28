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

import {CommonModule} from '@angular/common';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {RouterModule, Routes} from '@angular/router';
import {CanActivateFunctionGuard, CoreModule} from 'ngx-inception/core';
import {EditGroupTitleResolver} from './edit-group-title-resolver';
import {EditGroupComponent} from './edit-group.component';
import {EditTenantTitleResolver} from './edit-tenant-title-resolver';
import {EditTenantComponent} from './edit-tenant.component';
import {EditUserDirectoryTitleResolver} from './edit-user-directory-title-resolver';
import {EditUserDirectoryComponent} from './edit-user-directory.component';
import {EditUserTitleResolver} from './edit-user-title-resolver';
import {EditUserComponent} from './edit-user.component';
import {GroupMembersTitleResolver} from './group-members-title-resolver';
import {GroupMembersComponent} from './group-members.component';
import {GroupRolesTitleResolver} from './group-roles-title-resolver';
import {GroupRolesComponent} from './group-roles.component';
import {GroupTitleResolver} from './group-title-resolver';
import {GroupsTitleResolver} from './groups-title-resolver';
import {GroupsComponent} from './groups.component';
import {InternalUserDirectoryComponent} from './internal-user-directory.component';
import {LdapUserDirectoryComponent} from './ldap-user-directory.component';
import {NewGroupTitleResolver} from './new-group-title-resolver';
import {NewGroupComponent} from './new-group.component';
import {NewTenantTitleResolver} from './new-tenant-title-resolver';
import {NewTenantComponent} from './new-tenant.component';
import {NewUserDirectoryTitleResolver} from './new-user-directory-title-resolver';
import {NewUserDirectoryComponent} from './new-user-directory.component';
import {NewUserTitleResolver} from './new-user-title-resolver';
import {NewUserComponent} from './new-user.component';
import {ResetUserPasswordTitleResolver} from './reset-user-password-title-resolver';
import {ResetUserPasswordComponent} from './reset-user-password.component';
import {SecurityOverviewTitleResolver} from './security-overview-title-resolver';
import {SecurityOverviewComponent} from './security-overview.component';
import {TenantTitleResolver} from './tenant-title-resolver';
import {TenantUserDirectoriesTitleResolver} from './tenant-user-directories-title-resolver';
import {TenantUserDirectoriesComponent} from './tenant-user-directories.component';
import {TenantsTitleResolver} from './tenants-title-resolver';
import {TenantsComponent} from './tenants.component';
import {UserDirectoriesTitleResolver} from './user-directories-title-resolver';
import {UserDirectoriesComponent} from './user-directories.component';
import {UserDirectoryTitleResolver} from './user-directory-title-resolver';
import {UserGroupsTitleResolver} from './user-groups-title-resolver';
import {UserGroupsComponent} from './user-groups.component';
import {UserProfileComponent} from "./user-profile.component";
import {UserTitleResolver} from './user-title-resolver';
import {UsersTitleResolver} from './users-title-resolver';
import {UsersComponent} from './users.component';

const routes: Routes = [{
  path: '',
  redirectTo: 'overview'
}, {
  path: 'groups',
  resolve: {
    title: GroupsTitleResolver
  },
  children: [{
    path: '',
    canActivate: [CanActivateFunctionGuard],
    component: GroupsComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
        'FUNCTION_Security.GroupAdministration'
      ]
    }
  }, {
    path: ':userDirectoryId/new',
    pathMatch: 'full',
    canActivate: [CanActivateFunctionGuard],
    component: NewGroupComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
        'FUNCTION_Security.GroupAdministration'
      ]
    },
    resolve: {
      title: NewGroupTitleResolver
    }
  }, {
    path: ':userDirectoryId/:groupName',
    pathMatch: 'full',
    redirectTo: ':userDirectoryId/:groupName/edit'
  }, {
    path: ':userDirectoryId/:groupName',
    resolve: {
      title: GroupTitleResolver
    },
    children: [{
      path: 'edit',
      canActivate: [CanActivateFunctionGuard],
      component: EditGroupComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
          'FUNCTION_Security.GroupAdministration'
        ]
      },
      resolve: {
        title: EditGroupTitleResolver
      }
    }, {
      path: 'members',
      canActivate: [CanActivateFunctionGuard],
      component: GroupMembersComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
          'FUNCTION_Security.GroupAdministration'
        ]
      },
      resolve: {
        title: GroupMembersTitleResolver
      }
    }, {
      path: 'roles',
      canActivate: [CanActivateFunctionGuard],
      component: GroupRolesComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
          'FUNCTION_Security.GroupAdministration'
        ]
      },
      resolve: {
        title: GroupRolesTitleResolver
      }
    }
    ]
  }
  ]
}, {
  path: 'overview',
  component: SecurityOverviewComponent,
  data: {
    icon: 'fa fa-shield-alt'
  },
  resolve: {
    title: SecurityOverviewTitleResolver
  }
}, {
  path: 'tenants',
  resolve: {
    title: TenantsTitleResolver
  },
  children: [{
    path: '',
    canActivate: [CanActivateFunctionGuard],
    component: TenantsComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration']
    }
  }, {
    path: 'new',
    pathMatch: 'full',
    canActivate: [CanActivateFunctionGuard],
    component: NewTenantComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration']
    },
    resolve: {
      title: NewTenantTitleResolver
    }
  }, {
    path: ':tenantId',
    pathMatch: 'full',
    redirectTo: ':tenantId/edit'
  }, {
    path: ':tenantId',
    resolve: {
      title: TenantTitleResolver
    },
    children: [{
      path: 'edit',
      canActivate: [CanActivateFunctionGuard],
      component: EditTenantComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration']
      },
      resolve: {
        title: EditTenantTitleResolver
      }
    }, {
      path: 'user-directories',
      canActivate: [CanActivateFunctionGuard],
      component: TenantUserDirectoriesComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration']
      },
      resolve: {
        title: TenantUserDirectoriesTitleResolver
      }
    }
    ]
  }
  ]
}, {
  path: 'user-directories',
  resolve: {
    title: UserDirectoriesTitleResolver
  },
  children: [{
    path: '',
    canActivate: [CanActivateFunctionGuard],
    component: UserDirectoriesComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.UserDirectoryAdministration']
    }
  }, {
    path: 'new',
    pathMatch: 'full',
    canActivate: [CanActivateFunctionGuard],
    component: NewUserDirectoryComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.UserDirectoryAdministration']
    },
    resolve: {
      title: NewUserDirectoryTitleResolver
    }
  }, {
    path: ':userDirectoryId',
    pathMatch: 'full',
    redirectTo: ':userDirectoryId/edit'
  }, {
    path: ':userDirectoryId',
    resolve: {
      title: UserDirectoryTitleResolver
    },
    children: [{
      path: 'edit',
      canActivate: [CanActivateFunctionGuard],
      component: EditUserDirectoryComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.UserDirectoryAdministration']
      },
      resolve: {
        title: EditUserDirectoryTitleResolver
      }
    }
    ]
  }
  ]
}, {
  path: 'users',
  resolve: {
    title: UsersTitleResolver
  },
  children: [{
    path: '',
    canActivate: [CanActivateFunctionGuard],
    component: UsersComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
        'FUNCTION_Security.UserAdministration', 'FUNCTION_Security.UserGroups', 'FUNCTION_Security.ResetUserPassword'
      ]
    }
  }, {
    path: ':userDirectoryId/new',
    pathMatch: 'full',
    canActivate: [CanActivateFunctionGuard],
    component: NewUserComponent,
    data: {
      authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
        'FUNCTION_Security.UserAdministration'
      ]
    },
    resolve: {
      title: NewUserTitleResolver
    }
  }, {
    path: ':userDirectoryId/:username',
    pathMatch: 'full',
    redirectTo: ':userDirectoryId/:username/edit'
  }, {
    path: ':userDirectoryId/:username',
    resolve: {
      title: UserTitleResolver
    },
    children: [{
      path: 'edit',
      canActivate: [CanActivateFunctionGuard],
      component: EditUserComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
          'FUNCTION_Security.UserAdministration'
        ]
      },
      resolve: {
        title: EditUserTitleResolver
      }
    }, {
      path: 'groups',
      canActivate: [CanActivateFunctionGuard],
      component: UserGroupsComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
          'FUNCTION_Security.UserAdministration']
      },
      resolve: {
        title: UserGroupsTitleResolver
      }
    }, {
      path: 'reset-user-password',
      canActivate: [CanActivateFunctionGuard],
      component: ResetUserPasswordComponent,
      data: {
        authorities: ['ROLE_Administrator', 'FUNCTION_Security.TenantAdministration',
          'FUNCTION_Security.UserAdministration', 'FUNCTION_Security.ResetUserPassword'
        ]
      },
      resolve: {
        title: ResetUserPasswordTitleResolver
      }
    }
    ]
  }
  ]
}
];

@NgModule({
  declarations: [
    // Components
    EditGroupComponent, EditTenantComponent, EditUserDirectoryComponent, EditUserComponent,
    GroupMembersComponent, GroupRolesComponent, GroupsComponent, InternalUserDirectoryComponent,
    LdapUserDirectoryComponent, NewGroupComponent, NewTenantComponent, NewUserComponent,
    NewUserDirectoryComponent, TenantsComponent, TenantUserDirectoriesComponent,
    ResetUserPasswordComponent, SecurityOverviewComponent, UserDirectoriesComponent,
    UserGroupsComponent, UserProfileComponent, UsersComponent
  ],
  imports: [
    // Angular modules
    CommonModule, FormsModule, ReactiveFormsModule, RouterModule.forChild(routes),

    // Inception modules
    CoreModule
  ],
  providers: [
    // Resolvers
    EditGroupTitleResolver, EditTenantTitleResolver, EditUserDirectoryTitleResolver, EditUserTitleResolver,
    GroupMembersTitleResolver, GroupRolesTitleResolver, GroupTitleResolver, GroupsTitleResolver, NewGroupTitleResolver,
    NewTenantTitleResolver, NewUserDirectoryTitleResolver, NewUserTitleResolver, TenantsTitleResolver,
    TenantTitleResolver, TenantUserDirectoriesTitleResolver, ResetUserPasswordTitleResolver,
    SecurityOverviewTitleResolver, UserDirectoriesTitleResolver, UserDirectoryTitleResolver, UserGroupsTitleResolver,
    UsersTitleResolver, UserTitleResolver
  ]
})
export class SecurityViewsModule {
}
