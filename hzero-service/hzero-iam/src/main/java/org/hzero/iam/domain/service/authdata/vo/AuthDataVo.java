package org.hzero.iam.domain.service.authdata.vo;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.iam.infra.common.utils.AssertUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 权限数据对象
 *
 * @author bo.he02@hand-china.com 2020/06/05 11:08
 */
public class AuthDataVo {
    /**
     * 日志打印对象
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthDataVo.class);
    /**
     * 数据ID
     */
    private final Long dataId;
    /**
     * 数据编码
     */
    private final String dataCode;
    /**
     * 数据名称
     */
    private final String dataName;
    /**
     * 权限类型
     */
    private String authorityTypeCode;

    public AuthDataVo(Long dataId, String dataCode, String dataName) {
        this.dataId = dataId;
        this.dataCode = dataCode;
        this.dataName = dataName;
    }

    public AuthDataVo(String authorityTypeCode, Long dataId, String dataCode, String dataName) {
        this.authorityTypeCode = authorityTypeCode;
        this.dataId = dataId;
        this.dataCode = dataCode;
        this.dataName = dataName;
    }

    /**
     * 静态工厂
     *
     * @param authorityTypeCode 权限类型码
     * @param dataId            数据ID
     * @param dataCode          数据编码
     * @param dataName          数据名称
     * @return 权限数据对象
     */
    public static AuthDataVo of(String authorityTypeCode, Long dataId, String dataCode, String dataName) {
        return new AuthDataVo(authorityTypeCode, dataId, dataCode, dataName);
    }

    public String getAuthorityTypeCode() {
        return authorityTypeCode;
    }

    public AuthDataVo setAuthorityTypeCode(String authorityTypeCode) {
        this.authorityTypeCode = authorityTypeCode;
        return this;
    }

    public Long getDataId() {
        return dataId;
    }

    public String getDataCode() {
        return dataCode;
    }

    public String getDataName() {
        return dataName;
    }

    /**
     * 参数校验
     */
    public void validate() {
        LOGGER.debug("Auth Data Is: {}", this);

        AssertUtils.notNull(StringUtils.isBlank(this.authorityTypeCode) ? null : this.authorityTypeCode,
                BaseConstants.ErrorCode.NOT_NULL);
        AssertUtils.notNull(this.dataId, BaseConstants.ErrorCode.NOT_NULL);

        LOGGER.debug("Auth Data Is Right");
    }

    @Override
    public String toString() {
        return "AuthDataVo{" +
                "authorityTypeCode='" + authorityTypeCode + '\'' +
                ", dataId=" + dataId +
                ", dataCode='" + dataCode + '\'' +
                ", dataName='" + dataName + '\'' +
                '}';
    }
}
