package org.hzero.iam.domain.service.secgrp.observer.dcl;

import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAssign;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 数据权限变更用户订阅者接口抽象实现
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public abstract class AbstractUserSecGrpDclObserver implements UserSecGrpDclObserver {
    @Autowired
    private SecGrpRepository secGrpRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        // 查询安全组分配的用户
        List<SecGrpAssign> secGrpAssigns = this.secGrpRepository.listSecGrpAssign(secGrp.getSecGrpId());

        // 分配用户Dcl
        this.assignUsersDcl(secGrpAssigns, dcl, dclLines);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines) {
        // 查询安全组分配的用户
        List<SecGrpAssign> secGrpAssigns = this.secGrpRepository.listSecGrpAssign(secGrp.getSecGrpId());

        // 回收用户Dcl
        this.recycleUsersDcl(secGrpAssigns, dcl, dclLines);
    }
}
