package org.hzero.boot.platform.lov.dto;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;

/**
 * 值集值DTO
 *
 * @author gaokuo.dai@hand-china.com 2018年6月27日下午4:11:14
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LovValueDTO {

    /**
     * 快速创建
     *
     * @return 值集值DTO
     */
    public static LovValueDTO build(String value, String meaning, String description, String tag, String parentValue, Integer orderSeq) {
        LovValueDTO dto = new LovValueDTO();
        dto.value = value;
        dto.meaning = meaning;
        dto.description = description;
        dto.tag = tag;
        dto.parentValue = parentValue;
        dto.orderSeq = orderSeq;
        return dto;
    }


    private String value;
    private String meaning;
    private String description;
    private String tag;
    private String parentValue;
    private Integer orderSeq;

    /**
     * 元数据
     */
    private Map<String, Object> metadata;

    /**
     * @return 值集值
     */
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return 含义
     */
    public String getMeaning() {
        return meaning;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    /**
     * @return 描述
     */
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return 标记
     */
    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    /**
     * @return 父级LOV值
     */
    public String getParentValue() {
        return parentValue;
    }

    public void setParentValue(String parentValue) {
        this.parentValue = parentValue;
    }

    /**
     * @return 排序号
     */
    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setOrderSeq(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public LovValueDTO setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LovValueDTO [value=").append(value).append(", meaning=").append(meaning)
                .append(", description=").append(description).append(", tag=").append(tag)
                .append(", parentValue=").append(parentValue).append(", orderSeq=").append(orderSeq)
                .append(", metadata=").append(metadata).append("]");
        return builder.toString();
    }

}
