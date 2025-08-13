package org.hzero.platform.app.service.impl;

import org.hzero.boot.platform.entity.dto.RegistParam;
import org.hzero.boot.platform.entity.dto.SupportTypeEnum;
import org.hzero.boot.platform.entity.service.EntityRegisterExecutor;
import org.hzero.platform.app.service.EntityTableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/17 22:13
 */
@Service
public class LocalEntityRegister implements EntityRegisterExecutor {
    @Autowired
    private EntityTableService entityTableService;
    @Override
    public void doRegist(RegistParam param) {
        entityTableService.doRegist(param);
    }

    @Override
    public SupportTypeEnum getSupportType() {
        return SupportTypeEnum.INTERNAL;
    }
}
