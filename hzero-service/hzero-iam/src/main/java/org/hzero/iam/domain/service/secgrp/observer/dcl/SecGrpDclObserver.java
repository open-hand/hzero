package org.hzero.iam.domain.service.secgrp.observer.dcl;

import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDcl;
import org.hzero.iam.domain.entity.SecGrpDclLine;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 数据权限变更订阅者
 *
 * @author bojiangzhou 2020/02/27
 */
public interface SecGrpDclObserver {

    /**
     * 安全组数据权限增加时，向分配了此安全组的对象分配数据权限
     *
     * @param secGrp   安全组
     * @param dcl      数据权限头
     * @param dclLines 数据权限行
     */
    void assignSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines);

    /**
     * 安全组数据权限删除时，向分配了此安全组的对象回收数据权限
     *
     * @param secGrp   安全组
     * @param dcl      数据权限头
     * @param dclLines 数据权限行
     */
    void recycleSecGrpDcl(@Nonnull SecGrp secGrp, @Nonnull SecGrpDcl dcl, List<SecGrpDclLine> dclLines);
}
