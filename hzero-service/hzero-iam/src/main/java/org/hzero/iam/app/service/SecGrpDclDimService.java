package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.api.dto.SecGrpDclDimDTO;

/**
 * 安全组数据权限维度应用服务
 *
 * @author xingxing.wu@hand-china.com 2019-10-20 10:20:22
 */
public interface SecGrpDclDimService {
    /**
     * 批量新增或者更新
     *
     * @param tenantId            租户ID
     * @param secGrpId            安全组ID
     * @param secGrpDclDimDTOList 安全组数据权限维度
     */
    void batchSaveSecGrpDim(Long tenantId, Long secGrpId, List<SecGrpDclDimDTO> secGrpDclDimDTOList);

}
