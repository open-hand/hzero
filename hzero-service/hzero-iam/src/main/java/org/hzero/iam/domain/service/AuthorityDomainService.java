package org.hzero.iam.domain.service;

import java.util.List;

import org.hzero.iam.api.dto.CompanyOuInvorgDTO;
import org.hzero.iam.api.dto.CompanyOuInvorgNodeDTO;

/**
 * 用户角色单据权限通用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2019/07/17 20:01
 */
public interface AuthorityDomainService {

    /**
     * 生成构建树所需的数据集合
     * @param originList 元数据
     * @return 构建树所需参数
     */
    List<CompanyOuInvorgDTO> generateTreeDataList(List<CompanyOuInvorgNodeDTO> originList);
    /**
     * 生成构建树所需的数据集合
     * @param originList 元数据
     * @return 构建树所需参数
     */
    List<CompanyOuInvorgDTO> buildTreeDataListForSecGrp(List<CompanyOuInvorgNodeDTO> originList);

    /**
     * 初始化用户角色单据权限缓存
     */
    void initDocAuthCache();
}
