package org.hzero.iam.domain.repository;

import org.hzero.iam.domain.entity.DomainAssign;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单点二级域名分配资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2020-09-02 15:34:46
 */
public interface DomainAssignRepository extends BaseRepository<DomainAssign> {

    /**
     * 分页查询域名分配信息
     *
     * @param domainAssign 查询条件
     * @param pageRequest 分页参数
     * @return Page<DomainAssign>
     */
    Page<DomainAssign> pageDomainAssign(DomainAssign domainAssign, PageRequest pageRequest);

    /**
     * 查询获取域名分配明细信息（编辑单条域名信息时调用）
     *
     * @param domainId 域名Id
     * @param domainAssignId 主键Id
     * @return 详情信息
     */
    DomainAssign getDomainAssignDetail(Long domainId, Long domainAssignId);
}
