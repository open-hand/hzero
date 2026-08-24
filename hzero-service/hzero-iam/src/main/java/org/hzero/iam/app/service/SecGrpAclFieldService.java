package org.hzero.iam.app.service;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

import org.hzero.iam.api.dto.SecGrpAclFieldDTO;
import org.hzero.iam.domain.entity.SecGrpAclField;

/**
 * 安全组字段权限应用服务
 *
 * @author bojiangzhou 2020/02/18
 * @author xingxing.wu@hand-china.com 2019-10-28 10:00:59
 */
public interface SecGrpAclFieldService {

    /**
     * 批量操作安全组字段
     *
     * @param tenantId        租户ID
     * @param secGrpId        安全组ID
     * @param secGrpAclFields 安全组字段权列表
     */
    void batchSaveSecGrpField(Long tenantId, Long secGrpId, List<SecGrpAclField> secGrpAclFields);

}
