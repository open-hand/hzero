package org.hzero.iam.domain.service.secgrp.observer.dcl;

import org.hzero.iam.domain.entity.SecGrpAssign;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 数据权限变更用户订阅者接口
 *
 * @author bo.he02@hand-china.com 2020/04/21
 */
public interface UserSecGrpDclObserver extends SecGrpDclObserver {
    /**
     * 给用户分配数据权限
     *
     * @param secGrpAssigns 待操作的用户
     * @param dcl           数据权限头
     * @param dclLines      数据权限行
     */
    void assignUsersDcl(@Nonnull List<SecGrpAssign> secGrpAssigns, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines);

    /**
     * 取消用户分配的数据权限
     *
     * @param secGrpAssigns 待操作的用户
     * @param dcl           数据权限头
     * @param dclLines      数据权限行
     */
    void recycleUsersDcl(@Nonnull List<SecGrpAssign> secGrpAssigns, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines);
}
