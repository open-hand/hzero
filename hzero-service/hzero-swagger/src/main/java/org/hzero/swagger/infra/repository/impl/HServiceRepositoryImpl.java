package org.hzero.swagger.infra.repository.impl;

import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.hzero.swagger.domain.entity.HService;
import org.hzero.swagger.domain.repository.HServiceRepository;
import org.springframework.stereotype.Component;

/**
 * 应用服务 资源库实现
 */
@Component
public class HServiceRepositoryImpl extends BaseRepositoryImpl<HService> implements HServiceRepository {

}
