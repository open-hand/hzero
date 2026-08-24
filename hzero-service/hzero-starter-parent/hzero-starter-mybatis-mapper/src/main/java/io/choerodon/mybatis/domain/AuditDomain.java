package io.choerodon.mybatis.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import org.hzero.mybatis.domian.SecurityToken;

/**
 * @author Created by xausky on 3/20/17.
 */
@SuppressWarnings("all")
public abstract class AuditDomain implements SecurityToken {
    public static final String FIELD_CREATION_DATE = "creationDate";
    public static final String FIELD_CREATED_BY = "createdBy";
    public static final String FIELD_LAST_UPDATE_DATE = "lastUpdateDate";
    public static final String FIELD_LAST_UPDATED_BY = "lastUpdatedBy";
    public static final String FIELD_OBJECT_VERSION_NUMBER = "objectVersionNumber";
    public static final String FIELD_TABLE_ID = "tableId";

    @ApiModelProperty(hidden = true)
    private Date creationDate;
    @ApiModelProperty(hidden = true)
    private Long createdBy;
    @ApiModelProperty(hidden = true)
    private Date lastUpdateDate;
    @ApiModelProperty(hidden = true)
    private Long lastUpdatedBy;
    @ApiModelProperty(hidden = true)
    private Long objectVersionNumber;

    @Transient
    @ApiModelProperty(hidden = true)
    private String tableId;

    /**
     * 存放所有多语言字段的描述信息.
     * <p>
     * data example:
     *
     * <pre>
     * _tls:{
     *      roleName : {
     *          zh_CN : '管理员',
     *          en_GB : 'Admin'
     *      },
     *      description : {
     *          zh_CN : '管理员',
     *          en_GB : 'administrator'
     *      }
     *   }
     * </pre>
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(hidden = true)
    private Map<String, Map<String, String>> _tls;

    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(hidden = true)
    private String _token;

    /**
     * 未知的反序列化字段
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(hidden = true)
    private Map<String, Object> _innerMap;

    /**
     * 记录状态
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(hidden = true)
    private RecordStatus _status;

    /**
     * 存放弹性域数据
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(hidden = true)
    private Map<String, Object> flex = new HashMap<>(32);

    public AuditDomain set_innerMap(Map<String, Object> _innerMap) {
        this._innerMap = _innerMap;
        return this;
    }

    @JsonAnySetter
    public void set_innerMap(String key, Object value) {
        if (this._innerMap == null) {
            this._innerMap = new HashMap<>();
        }
        this._innerMap.put(key, value);
    }

    @JsonAnyGetter
    public Map<String, Object> get_innerMap() {
        return _innerMap;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Long createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(Date lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Long getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(Long lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public void setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
    }

    /**
     * @return 前端组件唯一标识
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public String getTableId() {
        return tableId;
    }

    public void setTableId(String tableId) {
        this.tableId = tableId;
    }

    public Map<String, Map<String, String>> get_tls() {
        return _tls;
    }

    public AuditDomain set_tls(Map<String, Map<String, String>> _tls) {
        this._tls = _tls;
        return this;
    }

    @Override
    public String get_token() {
        return _token;
    }

    @Override
    public void set_token(String _token) {
        this._token = _token;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }

    public String toJSONString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }

    public RecordStatus get_status() {
        return _status;
    }

    public AuditDomain set_status(RecordStatus _status) {
        this._status = _status;
        return this;
    }

    public Map<String, Object> getFlex() {
        return flex;
    }

    public AuditDomain setFlex(Map<String, Object> flex) {
        this.flex = flex;
        return this;
    }

    public static enum RecordStatus {
        /**
         * 新增
         */
        create,
        /**
         * 更新
         */
        update,
        /**
         * 删除
         */
        delete;
    }
}
