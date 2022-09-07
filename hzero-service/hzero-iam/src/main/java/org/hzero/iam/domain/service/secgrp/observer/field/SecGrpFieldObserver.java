package org.hzero.iam.domain.service.secgrp.observer.field;

import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpAclField;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * 字段权限变更订阅者
 *
 * @author bojiangzhou 2020/02/27
 */
public interface SecGrpFieldObserver {

    /**
     * 安全组字段权限增加时，向分配了此安全组的对象分配字段权限
     *
     * @param secGrp 安全组
     * @param fields 字段权限
     */
    void assignSecGrpField(@Nonnull SecGrp secGrp, List<SecGrpAclField> fields);

    /**
     * 安全组字段权限删除时，向分配了此安全组的对象回收字段权限
     *
     * @param secGrp 安全组
     * @param fields 字段权限
     */
    void recycleSecGrpField(@Nonnull SecGrp secGrp, List<SecGrpAclField> fields);
}
