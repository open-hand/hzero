package org.hzero.plugin.platform.hr.api.dto;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Transient;

import org.hzero.core.algorithm.tree.Child;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.plugin.platform.hr.domain.entity.Position;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 岗位数据传输
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 13:33
 */
@SuppressWarnings("all")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PositionDTO extends Child<PositionDTO> implements SecurityToken {
    @Encrypt
    private Long positionId;
    private Long tenantId;
    @Encrypt
    private Long unitCompanyId;
    @Encrypt
    private Long unitId;
    @Encrypt
    private Long parentId;
    @Encrypt
    private Long parentPositionId;
    private String unitCompanyName;
    public String getUnitCompanyName() {
		return unitCompanyName;
	}

	public void setUnitCompanyName(String unitCompanyName) {
		this.unitCompanyName = unitCompanyName;
	}

	public String getUnitName() {
		return unitName;
	}

	public void setUnitName(String unitName) {
		this.unitName = unitName;
	}

	private String unitName;
    private String parentPositionName;
    private String positionCode;
    private String positionName;
    private String description;
    private Integer orderSeq;
    private Integer supervisorFlag;
    private Integer enabledFlag;
    private String levelPath;
    private Long objectVersionNumber;
    private String _token;

    /**
     * 存放弹性域数据
     */
    @Transient
    @ApiModelProperty(hidden = true)
    private Map<String, Object> flex = new HashMap<>(32);

    public Map<String, Object> getFlex() {
        return flex;
    }

    public PositionDTO setFlex(Map<String, Object> flex) {
        this.flex = flex;
        return this;
    }

    /**
     * 未知的反序列化字段
     */
    @Transient
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @ApiModelProperty(hidden = true)
    private Map<String, Object> _innerMap;

    public PositionDTO() {
    }

    public PositionDTO(Position position) {
        this.setPositionId(position.getPositionId())
                .setTenantId(position.getTenantId())
                .setUnitCompanyId(position.getUnitCompanyId())
                .setUnitId(position.getUnitId())
                .setParentPositionId(position.getParentPositionId())
                .setPositionCode(position.getPositionCode())
                .setPositionName(position.getPositionName())
                .setDescription(position.getDescription())
                .setOrderSeq(position.getOrderSeq())
                .setSupervisorFlag(position.getSupervisorFlag())
                .setEnabledFlag(position.getEnabledFlag())
                .setLevelPath(position.getLevelPath())
                .setObjectVersionNumber(position.getObjectVersionNumber());
    }

    public Long getPositionId() {
        return positionId;
    }

    public PositionDTO setPositionId(Long positionId) {
        this.positionId = positionId;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public PositionDTO setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public Long getUnitCompanyId() {
        return unitCompanyId;
    }

    public PositionDTO setUnitCompanyId(Long unitCompanyId) {
        this.unitCompanyId = unitCompanyId;
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public PositionDTO setUnitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public PositionDTO setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Long getParentPositionId() {
        return parentPositionId;
    }

    public PositionDTO setParentPositionId(Long parentPositionId) {
        this.parentPositionId = parentPositionId;
        return this;
    }

    public String getParentPositionName() {
        return parentPositionName;
    }

    public PositionDTO setParentPositionName(String parentPositionName) {
        this.parentPositionName = parentPositionName;
        return this;
    }

    public String getPositionCode() {
        return positionCode;
    }

    public PositionDTO setPositionCode(String positionCode) {
        this.positionCode = positionCode;
        return this;
    }

    public String getPositionName() {
        return positionName;
    }

    public PositionDTO setPositionName(String positionName) {
        this.positionName = positionName;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public PositionDTO setDescription(String description) {
        this.description = description;
        return this;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public PositionDTO setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public Integer getSupervisorFlag() {
        return supervisorFlag;
    }

    public PositionDTO setSupervisorFlag(Integer supervisorFlag) {
        this.supervisorFlag = supervisorFlag;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public PositionDTO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public PositionDTO setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public PositionDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return Position.class;
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
        return "PositionDTO{" +
                "positionId=" + positionId +
                ", tenantId=" + tenantId +
                ", unitCompanyId=" + unitCompanyId +
                ", unitId=" + unitId +
                ", parentId=" + parentId +
                ", parentPositionId=" + parentPositionId +
                ", parentPositionName='" + parentPositionName + '\'' +
                ", positionCode='" + positionCode + '\'' +
                ", positionName='" + positionName + '\'' +
                ", description='" + description + '\'' +
                ", orderSeq=" + orderSeq +
                ", supervisorFlag=" + supervisorFlag +
                ", enabledFlag=" + enabledFlag +
                ", levelPath='" + levelPath + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                '}';
    }

    public void set_innerMap(Map<String, Object> _innerMap) {
        this._innerMap = _innerMap;
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

}
