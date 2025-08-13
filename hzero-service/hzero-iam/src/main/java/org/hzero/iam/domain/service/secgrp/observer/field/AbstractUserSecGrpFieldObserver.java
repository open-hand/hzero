package org.hzero.iam.domain.service.secgrp.observer.field;

import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.entity.SecGrpAssign;
import org.hzero.iam.domain.repository.SecGrpRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 数据维度变更用户订阅者接口的抽象实现
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public abstract class AbstractUserSecGrpFieldObserver extends AbstractSecGrpFieldObserver implements UserSecGrpFieldObserver {
    @Autowired
    private SecGrpRepository secGrpRepository;

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void assignSecGrpField(@Nonnull SecGrp secGrp, List<SecGrpAclField> fields) {
        // 查询安全组分配的用户
        List<SecGrpAssign> secGrpAssigns = this.secGrpRepository.listSecGrpAssign(secGrp.getSecGrpId());

        // 分配用户字段权限
        this.assignUsersField(secGrpAssigns, fields);
    }

    @Override
    @Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
    public void recycleSecGrpField(@Nonnull SecGrp secGrp, List<SecGrpAclField> fields) {
        // 查询安全组分配的用户
        List<SecGrpAssign> secGrpAssigns = this.secGrpRepository.listSecGrpAssign(secGrp.getSecGrpId());

        // 回收用户字段权限
        this.recycleUsersField(secGrpAssigns, fields);
    }
}
