package org.hzero.platform.infra.mapper;

import java.util.List;

import org.hzero.platform.api.dto.PermissionRelDTO;
import org.hzero.platform.domain.entity.PermissionRel;
import io.choerodon.mybatis.common.BaseMapper;

/**
 * 屏蔽范围规则关系Mapper
 *
 * @author yunxiang.zhou01@hand-china.com 2018-08-29 15:19:45
 */
public interface PermissionRelMapper extends BaseMapper<PermissionRel> {

    /**
     * 查询数据权限规则
     *
     * @param rangeId 范围id
     * @return 数据权限规则
     */
    List<PermissionRelDTO> selectPermissionRuleByRangeId(Long rangeId);
}
