package org.hzero.boot.platform.entity.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.hzero.boot.platform.entity.dto.RegistParam;
import org.hzero.boot.platform.entity.dto.SupportTypeEnum;
import org.hzero.boot.platform.entity.feign.EntityRegistFeignClient;
import org.hzero.boot.platform.entity.service.EntityRegisterExecutor;

/**
 * 远程注册实现类
 *
 * @author xingxing.wu@hand-china.com 2019/07/17 21:55
 */
@Service
public class RemoteEntityRegister implements EntityRegisterExecutor {
    @Autowired
    private EntityRegistFeignClient entityRegistFeignClient;

    @Override
    public void doRegist(RegistParam param) {
        entityRegistFeignClient.register(param);
    }

    @Override
    public SupportTypeEnum getSupportType() {
        return SupportTypeEnum.FEIGN;
    }
}
