package org.hzero.plugin.platform.hr.api.dto;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.starter.keyencrypt.core.Encrypt;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.Objects;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * <p>
 * 部门岗位聚合DTO
 * </p>
 *
 * @author qingsheng.chen 2018/6/28 星期四 14:52
 */
@SuppressWarnings("all")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class UnitPositionDTO extends AuditDomain {
    @Encrypt
    private Long id;
    private String type;
    private String name;
    private String code;
    @Encrypt
    private Long unitId;
    @Encrypt
    private Long parentId;
    private Integer orderSeq;
    private Integer assignFlag;
    private Integer primaryPositionFlag;
    @JsonIgnore
    private String levelPath;
    private List<UnitPositionDTO> children;

    public String getTypeWithId() {
        return StringUtils.join(type, String.valueOf(id));
    }

    public Long getId() {
        return id;
    }

    public UnitPositionDTO setId(Long id) {
        this.id = id;
        return this;
    }

    public String getType() {
        return type;
    }

    public UnitPositionDTO setType(String type) {
        this.type = type;
        return this;
    }

    public String getName() {
        return name;
    }

    public UnitPositionDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getCode() {
        return code;
    }

    public UnitPositionDTO setCode(String code) {
        this.code = code;
        return this;
    }

    public Long getUnitId() {
        return unitId;
    }

    public UnitPositionDTO setUnitId(Long unitId) {
        this.unitId = unitId;
        return this;
    }

    public Long getParentId() {
        return parentId;
    }

    public UnitPositionDTO setParentId(Long parentId) {
        this.parentId = parentId;
        return this;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public UnitPositionDTO setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
        return this;
    }

    public Integer getAssignFlag() {
        return assignFlag;
    }

    public UnitPositionDTO setAssignFlag(Integer assignFlag) {
        this.assignFlag = assignFlag;
        return this;
    }

    public Integer getPrimaryPositionFlag() {
        return primaryPositionFlag;
    }

    public UnitPositionDTO setPrimaryPositionFlag(Integer primaryPositionFlag) {
        this.primaryPositionFlag = primaryPositionFlag;
        return this;
    }

    public List<UnitPositionDTO> getChildren() {
        return children;
    }

    public UnitPositionDTO setChildren(List<UnitPositionDTO> children) {
        this.children = children;
        return this;
    }

    public UnitPositionDTO addChildren(List<UnitPositionDTO> children) {
        if (this.children == null) {
            this.children = children;
        } else {
            this.children.addAll(children);
        }
        return this;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public UnitPositionDTO setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    @Override
    public String toString() {
        return "UnitPositionDTO{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", unitId=" + unitId +
                ", parentId=" + parentId +
                ", orderSeq=" + orderSeq +
                ", assignFlag=" + assignFlag +
                ", primaryPositionFlag=" + primaryPositionFlag +
                ", children=" + children +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UnitPositionDTO)) {
            return false;
        }
        UnitPositionDTO that = (UnitPositionDTO) o;
        return Objects.equal(id, that.id) &&
                Objects.equal(type, that.type) &&
                Objects.equal(name, that.name) &&
                Objects.equal(code, that.code) &&
                Objects.equal(unitId, that.unitId) &&
                Objects.equal(parentId, that.parentId) &&
                Objects.equal(orderSeq, that.orderSeq) &&
                Objects.equal(assignFlag, that.assignFlag) &&
                Objects.equal(primaryPositionFlag, that.primaryPositionFlag) &&
                Objects.equal(levelPath, that.levelPath) &&
                Objects.equal(children, that.children);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (unitId != null ? unitId.hashCode() : 0);
        result = 31 * result + (parentId != null ? parentId.hashCode() : 0);
        result = 31 * result + (orderSeq != null ? orderSeq.hashCode() : 0);
        result = 31 * result + (assignFlag != null ? assignFlag.hashCode() : 0);
        result = 31 * result + (primaryPositionFlag != null ? primaryPositionFlag.hashCode() : 0);
        result = 31 * result + (levelPath != null ? levelPath.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        return result;
    }
}
