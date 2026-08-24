package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.DomainAssign;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 单点二级域名分配Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2020-09-02 15:34:46
 */
public interface DomainAssignMapper extends BaseMapper<DomainAssign> {

    /**
     * 分页查询域名分配信息
     *
     * @param domainAssign 查询条件
     * @return List<DomainAssign>
     */
    List<DomainAssign> selectDomainAssign(DomainAssign domainAssign);

    /**
     * 查询获取域名分配明细信息（编辑单条域名信息时调用）
     *
     * @param domainId 域名Id
     * @param domainAssignId 主键Id
     * @return 详情信息
     */
    DomainAssign selectDomainAssignDetail(@Param("domainId") Long domainId, @Param("domainAssignId") Long domainAssignId);
}
