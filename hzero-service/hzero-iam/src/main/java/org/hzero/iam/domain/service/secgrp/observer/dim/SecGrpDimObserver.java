package org.hzero.iam.domain.service.secgrp.observer.dim;

import org.hzero.iam.domain.entity.SecGrp;
import org.hzero.iam.domain.entity.SecGrpDclDim;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * 数据维度变更订阅者
 *
 * @author bojiangzhou 2020/02/27
 */
public interface SecGrpDimObserver {

    /**
     * 安全组维度值增加时，向分配了此安全组的对象分配单据维度
     *
     * @param secGrp        安全组
     * @param dim           维度头
     * @param authTypeCodes 维度值
     */
    void assignSecGrpDim(@Nonnull SecGrp secGrp, @Nonnull SecGrpDclDim dim, Set<String> authTypeCodes);

    /**
     * 安全组维度值删除时，向分配了此安全组的对象回收单据维度
     *
     * @param secGrp        安全组
     * @param dim           维度头
     * @param authTypeCodes 维度值
     */
    void recycleSecGrpDim(@Nonnull SecGrp secGrp, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes);

    /**
     * 安全组维度值删除时，向分配了此安全组的对象回收单据维度
     *
     * @param secGrp        安全组
     * @param dim           维度头
     * @param authTypeCodes 维度值
     */
    void recycleSecGrpDimLine(@Nonnull SecGrp secGrp, @Nonnull SecGrpDclDim dim, @Nonnull Set<String> authTypeCodes);
}
