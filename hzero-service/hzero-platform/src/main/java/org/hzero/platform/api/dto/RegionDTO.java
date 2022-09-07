package org.hzero.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.core.algorithm.tree.Child;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.Country;
import org.hzero.platform.domain.entity.Region;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.Transient;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * <p>
 * 地区定义数据传输对象，地区之间使用树状结构拼接
 * </p>
 *
 * @author qingsheng.chen 2018/6/22 星期五 10:23
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegionDTO extends Child<org.hzero.platform.api.dto.RegionDTO> implements SecurityToken {
    @Encrypt
    private Long regionId;
    @Encrypt
    private Long countryId;
    private String regionCode;
    private String regionName;
    @Encrypt
    private Long parentRegionId;
    private Integer enabledFlag;
    private String levelPath;
    private Integer levelNumber;
    private Long objectVersionNumber;
    private List<org.hzero.platform.api.dto.RegionDTO> children;
    private String _token;
    private String quickIndex;
    private Integer hasNextFlag;
    private List<String> nameLevelPaths;
    /**
     * 存放弹性域数据
     */
    @Transient
    @ApiModelProperty(hidden = true)
    private Map<String, Object> flex = new HashMap<>(32);

    public Map<String, Object> getFlex() {
        return flex;
    }

    public org.hzero.platform.api.dto.RegionDTO setFlex(Map<String, Object> flex) {
        this.flex = flex;
        return this;
    }


    public RegionDTO() {
    }

    public RegionDTO(Region region) {
        this.setRegionId(region.getRegionId())
                .setCountryId(region.getCountryId())
                .setQuickIndex(region.getQuickIndex())
                .setRegionCode(region.getRegionCode())
                .setRegionName(region.getRegionName())
                .setParentRegionId(region.getParentRegionId())
                .setEnabledFlag(region.getEnabledFlag())
                .setObjectVersionNumber(region.getObjectVersionNumber());
    }

    public Long getRegionId() {
        return regionId;
    }

    public org.hzero.platform.api.dto.RegionDTO setRegionId(Long regionId) {
        this.regionId = regionId;
        return this;
    }

    public Long getCountryId() {
        return countryId;
    }

    public org.hzero.platform.api.dto.RegionDTO setCountryId(Long countryId) {
        this.countryId = countryId;
        return this;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public org.hzero.platform.api.dto.RegionDTO setRegionCode(String regionCode) {
        this.regionCode = regionCode;
        return this;
    }

    public String getRegionName() {
        return regionName;
    }

    public org.hzero.platform.api.dto.RegionDTO setRegionName(String regionName) {
        this.regionName = regionName;
        return this;
    }

    public Long getParentRegionId() {
        return parentRegionId;
    }

    public org.hzero.platform.api.dto.RegionDTO setParentRegionId(Long parentRegionId) {
        this.parentRegionId = parentRegionId;
        return this;
    }

    public Integer getEnabledFlag() {
        return enabledFlag;
    }

    public org.hzero.platform.api.dto.RegionDTO setEnabledFlag(Integer enabledFlag) {
        this.enabledFlag = enabledFlag;
        return this;
    }

    public Long getObjectVersionNumber() {
        return objectVersionNumber;
    }

    public org.hzero.platform.api.dto.RegionDTO setObjectVersionNumber(Long objectVersionNumber) {
        this.objectVersionNumber = objectVersionNumber;
        return this;
    }

    public List<org.hzero.platform.api.dto.RegionDTO> getChildren() {
        return children;
    }

    public org.hzero.platform.api.dto.RegionDTO setChildren(List<org.hzero.platform.api.dto.RegionDTO> children) {
        this.children = children;
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
    public Class<? extends SecurityToken> associateEntityClass() {
        return Region.class;
    }

    public String getLevelPath() {
        return levelPath;
    }

    public org.hzero.platform.api.dto.RegionDTO setLevelPath(String levelPath) {
        this.levelPath = levelPath;
        return this;
    }

    public Integer getLevelNumber() {
        return levelNumber;
    }

    public void setLevelNumber(Integer levelNumber) {
        this.levelNumber = levelNumber;
    }

    public String getQuickIndex() {
        return quickIndex;
    }

    public org.hzero.platform.api.dto.RegionDTO setQuickIndex(String quickIndex) {
        this.quickIndex = quickIndex;
        return this;
    }

    public Integer getHasNextFlag() {
        return hasNextFlag;
    }

    public void setHasNextFlag(Integer hasNextFlag) {
        this.hasNextFlag = hasNextFlag;
    }

    public List<String> getNameLevelPaths() {
        return nameLevelPaths;
    }

    public void setNameLevelPaths(List<String> nameLevelPaths) {
        this.nameLevelPaths = nameLevelPaths;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof org.hzero.platform.api.dto.RegionDTO)) {
            return false;
        }

        org.hzero.platform.api.dto.RegionDTO regionDTO = (org.hzero.platform.api.dto.RegionDTO) o;

        if (!Objects.equals(regionId, regionDTO.regionId)) {
            return false;
        }
        if (!Objects.equals(countryId, regionDTO.countryId)) {
            return false;
        }
        if (!Objects.equals(regionCode, regionDTO.regionCode)) {
            return false;
        }
        if (!Objects.equals(regionName, regionDTO.regionName)) {
            return false;
        }
        if (!Objects.equals(parentRegionId, regionDTO.parentRegionId)) {
            return false;
        }
        if (!Objects.equals(levelNumber, regionDTO.levelNumber)) {
            return false;
        }
        if (!Objects.equals(enabledFlag, regionDTO.enabledFlag)) {
            return false;
        }
        if (!Objects.equals(levelPath, regionDTO.levelPath)) {
            return false;
        }
        if (!Objects.equals(objectVersionNumber, regionDTO.objectVersionNumber)) {
            return false;
        }
        if (!Objects.equals(children, regionDTO.children)) {
            return false;
        }
        return Objects.equals(_token, regionDTO._token);
    }

    @Override
    public int hashCode() {
        int result = regionId != null ? regionId.hashCode() : 0;
        result = 31 * result + (countryId != null ? countryId.hashCode() : 0);
        result = 31 * result + (regionCode != null ? regionCode.hashCode() : 0);
        result = 31 * result + (regionName != null ? regionName.hashCode() : 0);
        result = 31 * result + (parentRegionId != null ? parentRegionId.hashCode() : 0);
        result = 31 * result + (levelNumber != null ? levelNumber.hashCode() : 0);
        result = 31 * result + (enabledFlag != null ? enabledFlag.hashCode() : 0);
        result = 31 * result + (levelPath != null ? levelPath.hashCode() : 0);
        result = 31 * result + (objectVersionNumber != null ? objectVersionNumber.hashCode() : 0);
        result = 31 * result + (children != null ? children.hashCode() : 0);
        result = 31 * result + (_token != null ? _token.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "RegionDTO{" +
                "regionId=" + regionId +
                ", countryId=" + countryId +
                ", regionCode='" + regionCode + '\'' +
                ", regionName='" + regionName + '\'' +
                ", parentRegionId=" + parentRegionId +
                ", enabledFlag=" + enabledFlag +
                ", levelPath='" + levelPath + '\'' +
                ", levelNumber='" + levelNumber + '\'' +
                ", objectVersionNumber=" + objectVersionNumber +
                ", children=" + children +
                ", _token='" + _token + '\'' +
                '}';
    }
}
