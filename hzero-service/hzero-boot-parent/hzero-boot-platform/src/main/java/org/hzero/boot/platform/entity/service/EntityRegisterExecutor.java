package org.hzero.boot.platform.entity.service;

import org.hzero.boot.platform.entity.dto.RegistParam;
import org.hzero.boot.platform.entity.dto.SupportTypeEnum;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/17 21:01
 */
public interface EntityRegisterExecutor {
    void doRegist( RegistParam param);
    SupportTypeEnum getSupportType();

}
