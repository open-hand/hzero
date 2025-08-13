package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.TenantCustomPoint;

/**
 * 租户客户化端点关系Mapper
 *
 * @author bojiangzhou 2019-12-12 11:40:55
 */
public interface TenantCustomPointMapper extends BaseMapper<TenantCustomPoint> {

    List<TenantCustomPoint> selectByPointCode(@Param("customPointCode") String customPointCode);
}
