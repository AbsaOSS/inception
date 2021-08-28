/// *
// * Copyright 2021 original authors
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *   http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
// package africa.absa.inception.demo;
//
//
//
// import africa.absa.inception.security.ISecurityService;
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.LinkedHashMap;
// import java.util.Map;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.GrantedAuthority;
// import org.springframework.security.core.authority.AuthorityUtils;
// import org.springframework.security.oauth2.provider.token.UserAuthenticationConverter;
// import org.springframework.util.StringUtils;
//
//
//
/// **
// * The <b>DemoUserAuthenticationConverter</b> class.
// *
// * @author Marcus Portmann
// */
// public class DemoUserAuthenticationConverter implements UserAuthenticationConverter {
//
//  @Autowired private ISecurityService securityService;
//
//  @Override
//  public Map<String, ?> convertUserAuthentication(Authentication authentication) {
//    Map<String, Object> response = new LinkedHashMap<>();
//    response.put(USERNAME, authentication.getName());
//
//    if ((authentication.getAuthorities() != null) && !authentication.getAuthorities().isEmpty()) {
//      response.put(AUTHORITIES,
// AuthorityUtils.authorityListToSet(authentication.getAuthorities()));
//    }
//
//    return response;
//  }
//
//  @Override
//  public Authentication extractAuthentication(Map<String, ?> map) {
//    if (map.containsKey(USERNAME)) {
//      Object principal = map.get(USERNAME);
//      Collection<? extends GrantedAuthority> authorities = getAuthorities(map);
//
//      //    if (userDetailsService != null) {
//      //      UserDetails user = userDetailsService.loadUserByUsername((String)
// map.get(USERNAME));
//      //      authorities = user.getAuthorities();
//      //      principal = user;
//      //    }
//      return new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
//    }
//
//    return null;
//  }
//
//  private Collection<? extends GrantedAuthority> getAuthorities(Map<String, ?> map) {
//    if (!map.containsKey(AUTHORITIES)) {
//      return new ArrayList<>();
//    }
//
//    Object authorities = map.get(AUTHORITIES);
//    if (authorities instanceof String) {
//      return AuthorityUtils.commaSeparatedStringToAuthorityList((String) authorities);
//    }
//
//    if (authorities instanceof Collection) {
//      return AuthorityUtils.commaSeparatedStringToAuthorityList(
//          StringUtils.collectionToCommaDelimitedString((Collection<?>) authorities));
//    }
//
//    throw new IllegalArgumentException("Authorities must be either a String or a Collection");
//  }
// }
