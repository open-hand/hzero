package org.hzero.platform.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.PermissionRangeExcl;

import java.util.List;
import java.util.Set;

/**
 * 屏蔽范围黑名单Mapper
 *
 * @author qingsheng.chen@hand-china.com 2020-06-10 10:17:25
 */
public interface PermissionRangeExclMapper extends BaseMapper<PermissionRangeExcl> {

    List<PermissionRangeExcl> listExcl(@Param("rangeIds") Set<Long> rangeIds);
}
