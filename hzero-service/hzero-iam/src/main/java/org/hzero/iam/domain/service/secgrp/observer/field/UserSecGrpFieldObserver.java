package org.hzero.iam.domain.service.secgrp.observer.field;

import org.hzero.iam.domain.entity.SecGrpAclField;
import org.hzero.iam.domain.entity.SecGrpAssign;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 字段权限变更用户订阅者
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public interface UserSecGrpFieldObserver extends SecGrpFieldObserver {
    /**
     * 向用户分配字段权限
     *
     * @param secGrpAssigns 处理的用户
     * @param fields        字段权限
     */
    void assignUsersField(@Nonnull List<SecGrpAssign> secGrpAssigns, List<SecGrpAclField> fields);

    /**
     * 向用户分配回收字段权限
     *
     * @param secGrpAssigns 处理的用户
     * @param fields        字段权限
     */
    void recycleUsersField(@Nonnull List<SecGrpAssign> secGrpAssigns, List<SecGrpAclField> fields);
}
