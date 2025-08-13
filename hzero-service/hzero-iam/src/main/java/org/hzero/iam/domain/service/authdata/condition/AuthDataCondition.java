package org.hzero.iam.domain.service.authdata.condition;

/**
 * 权限数据处理条件对象
 *
 * @author bo.he02@hand-china.com 2020/05/26 10:24
 */
public class AuthDataCondition {
    /**
     * 权限数据租户Id
     */
    private final Long tenantId;

    /**
     * 权限数据Code
     */
    private final String dataCode;

    /**
     * 权限数据名称
     */
    private final String dataName;

    /**
     * 扩展参数，格式自定义
     */
    private final String extendsParam;

    /**
     * 原始参数对象
     */
    private final Object originObject;

    public AuthDataCondition(Long tenantId, String dataCode, String dataName, String extendsParam, Object originObject) {
        this.tenantId = tenantId;
        this.dataCode = dataCode;
        this.dataName = dataName;
        this.extendsParam = extendsParam;
        this.originObject = originObject;
    }

    /**
     * 静态工厂
     *
     * @param tenantId     权限数据租户Id
     * @param dataCode     权限数据Code
     * @param dataName     权限数据名称
     * @param extendsParam 扩展参数，格式自定义
     * @return 权限数据对象
     */
    public static AuthDataCondition of(Long tenantId, String dataCode, String dataName, String extendsParam, Object originObject) {
        return new AuthDataCondition(tenantId, dataCode, dataName, extendsParam, originObject);
    }

    /**
     * //////////////////////////////// getter  ///////////////////////////////////////
     */

    public Long getTenantId() {
        return tenantId;
    }

    public String getDataCode() {
        return dataCode;
    }

    public String getDataName() {
        return dataName;
    }

    public String getExtendsParam() {
        return extendsParam;
    }

    public Object getOriginObject() {
        return originObject;
    }

    @Override
    public String toString() {
        return "AuthDataVo{" +
                "tenantId=" + tenantId +
                ", dataCode='" + dataCode + '\'' +
                ", dataName='" + dataName + '\'' +
                ", extendsParam='" + extendsParam + '\'' +
                ", originObject=" + originObject +
                '}';
    }
}
