package org.hzero.gateway.helper.service;


import org.hzero.gateway.helper.domain.CustomUserDetailsWithResult;

public interface GetUserDetailsService {

    CustomUserDetailsWithResult getUserDetails(String accessToken);

}
