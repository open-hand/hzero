package org.hzero.iam.domain.service.secgrp.authority.dcl;

import io.choerodon.core.exception.CommonException;
import org.hzero.iam.domain.entity.DocTypeDimension;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据权限类型
 * 数据权限类型由两段进行拼接而成：值来源类型 + 值来源
 * 值来源类型包含两种：值集(LOV) 本地编码(LOCAL)
 * 值来源：值集类型的值来源同一处理，所以只有一种数据权限类型 LOV_ALL，其他全是本地编码类型
 *
 * @author bo.he02@hand-china.com 2020/04/03
 * @author bojiangzhou 2020/03/04
 * @see DocTypeDimension#valueSourceType    值来源类型
 * @see DocTypeDimension#valueSource        值来源
 */
public enum DclAuthType {
    /**
     * 值集：所有
     */
    LOV_ALL("LOV_ALL"),
    /**
     * 本地编码：公司
     */
    LOCAL_COMPANY("LOCAL_COMPANY"),
    /**
     * 本地编码：业务单元
     */
    LOCAL_OU("LOCAL_OU"),
    /**
     * 本地编码：库存组织
     */
    LOCAL_INVORG("LOCAL_INVORG"),

    /**
     * 本地编码：采购组织
     */
    LOCAL_PURORG("LOCAL_PURORG"),
    /**
     * 本地编码：值集
     */
    LOCAL_LOV("LOCAL_LOV"),
    /**
     * 本地编码：值集视图
     */
    LOCAL_LOV_VIEW("LOCAL_LOV_VIEW"),
    /**
     * 本地编码：采购员
     */
    LOCAL_PURAGENT("LOCAL_PURAGENT"),
    /**
     * 本地编码：数据源
     */
    LOCAL_DATASOURCE("LOCAL_DATASOURCE"),
    /**
     * 本地编码：数据组
     */
    LOCAL_DATA_GROUP("LOCAL_DATA_GROUP");

    private String code;

    DclAuthType(String code) {
        this.code = code;
    }

    public String code() {
        return code;
    }

    private static final Map<String, DclAuthType> MAP = new HashMap<>();

    static {
        for (DclAuthType type : DclAuthType.values()) {
            MAP.put(type.code, type);
        }
    }

    public static DclAuthType match(String authType) {
        DclAuthType type = MAP.get(authType);
        if (type == null) {
            throw new CommonException("hiam.warn.secGrp.dclAuthTypeIllegal");
        }
        return type;
    }
}