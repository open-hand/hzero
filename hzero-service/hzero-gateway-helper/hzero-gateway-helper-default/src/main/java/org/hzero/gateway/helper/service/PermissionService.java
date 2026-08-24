package org.hzero.gateway.helper.service;


import org.hzero.gateway.helper.entity.PermissionDO;

public interface PermissionService {

    PermissionDO selectPermissionByRequest(String requestKey);

}
