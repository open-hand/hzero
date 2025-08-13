/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package org.hzero.starter.social.core.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


/**
 * similar to {@link UserDetailsService} but loads details by user id, not username
 * 
 * @author Stefan Fussennegger
 */
public interface SocialUserDetailsService {

    /**
     * @see UserDetailsService#loadUserByUsername(String)
     * @param username the user ID used to lookup the user details
     * @return the SocialUserDetails requested
     */
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
