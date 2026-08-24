package org.hzero.iam.app.service;

import java.util.List;

import org.hzero.iam.domain.entity.DomainAssign;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 单点二级域名分配应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2020-09-02 15:34:46
 */
public interface DomainAssignService {

    /**
     * 分页查询域名分配信息
     *
     * @param domainId 域名Id
     * @param domainAssign 查询条件
     * @param pageRequest 分页参数
     * @return Page<DomainAssign>
     */
    Page<DomainAssign> pageDomainAssign(Long domainId, DomainAssign domainAssign, PageRequest pageRequest);

    /**
     * 查询获取域名分配明细信息（编辑单条域名信息时调用）
     *
     * @param domainId 域名Id
     * @param domainAssignId 主键Id
     * @return 详情信息
     */
    DomainAssign getDomainAssignDetail(Long domainId, Long domainAssignId);

    /**
     * 创建域名分配信息
     *
     * @param domainId 域名Id
     * @param domainAssign 新增参数
     * @return 新增结果
     */
    DomainAssign createDomainAssign(Long domainId, DomainAssign domainAssign);

    /**
     * 修改域名分配信息
     *
     * @param domainAssign 修改参数
     * @return 修改结果
     */
    DomainAssign updateDomainAssign(DomainAssign domainAssign);

    /**
     * 批量删除域名分配信息
     *
     * @param domainId 域名Id
     * @param domainAssigns 删除参数
     */
    void batchRemoveDomainAssigns(Long domainId, List<DomainAssign> domainAssigns);
}
