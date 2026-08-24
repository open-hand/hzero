package org.hzero.iam.api.dto;

import java.util.List;

/**
 * @author qingsheng.chen@hand-china.com 2019-06-17 10:25
 */
public class ResponseCompanyOuInvorgDTO {
    private List<CompanyOuInvorgDTO> originList;
    private List<CompanyOuInvorgDTO> treeList;

    public ResponseCompanyOuInvorgDTO() {
    }

    public ResponseCompanyOuInvorgDTO(List<CompanyOuInvorgDTO> originList, List<CompanyOuInvorgDTO> treeList) {
        this.originList = originList;
        this.treeList = treeList;
    }

    public List<CompanyOuInvorgDTO> getOriginList() {
        return originList;
    }

    public ResponseCompanyOuInvorgDTO setOriginList(List<CompanyOuInvorgDTO> originList) {
        this.originList = originList;
        return this;
    }

    public List<CompanyOuInvorgDTO> getTreeList() {
        return treeList;
    }

    public ResponseCompanyOuInvorgDTO setTreeList(List<CompanyOuInvorgDTO> treeList) {
        this.treeList = treeList;
        return this;
    }
}
