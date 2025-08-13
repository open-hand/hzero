package org.hzero.iam.domain.repository;

import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.iam.domain.entity.TenantCustomPoint;

/**
 * 租户客户化端点关系资源库
 *
 * @author bojiangzhou 2019-12-12 11:40:55
 */
public interface TenantCustomPointRelRepository extends BaseRepository<TenantCustomPoint> {

    /**
     * 根据客户化端点查询，返回数据包含租户编码
     * 
     * @param customPointCode 客户化端点编码
     * @return List
     */
    List<TenantCustomPoint> selectByPointCode(String customPointCode);
}
