package org.hzero.platform.api.dto;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hzero.platform.domain.entity.LovValue;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * 值集值DTO
 *
 * @author gaokuo.dai@hand-china.com 2018年6月27日下午4:11:14
 */
@ApiModel("值集值DTO")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LovValueDTO extends AuditDomain{
    
    public static final String FIELD_CHILDREN = "children";
    public static final String FIELD_METADATA = "metadata";

    /**
     * 快速创建
     * @param value 值集值
     * @param meaning 含义
     * @param description 描述
     * @param tag 标记
     * @param parentValue 父值集值
     * @return
     */
    public static LovValueDTO build(String value, String meaning, String description, String tag, String parentValue, Integer orderSeq) {
        LovValueDTO dto = new LovValueDTO();
        dto.value = value;
        dto.meaning = meaning;
        dto.description = description;
        dto.tag = tag;
        dto.orderSeq = orderSeq;
        dto.parentValue = parentValue;
        return dto;
    }

    @ApiModelProperty("值集值")
    private String value;
    @ApiModelProperty("含义")
    private String meaning;
    @ApiModelProperty("描述")
    private String description;
    @ApiModelProperty("标记")
    private String tag;
    @ApiModelProperty("父级值集值")
    private String parentValue;
    @ApiModelProperty("排序号")
    private Integer orderSeq;
    @ApiModelProperty("子值集值")
    private List<LovValueDTO> children;
    @ApiModelProperty("元数据")
    private String metadata;
    
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
    /**
     * @return 子值集列表
     */
    public List<LovValueDTO> getChildren() {
        return children;
    }
    public void setChildren(List<LovValueDTO> children) {
        this.children = children;
    }
    /**
     * @return 元数据
     */
    public String getMetadata() {
        return metadata;
    }
    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    /**
     * DTO转化为Map
     * 
     * @return
     */
    public Map<String, Object> toMap(){
        Map<String, Object> map = new HashMap<>();
        if(this.value != null) {
            map.put(LovValue.FIELD_VALUE, this.value);
        }
        if(this.meaning != null) {
            map.put(LovValue.FIELD_MEANING, this.meaning);
        }
        if(this.description != null) {
            map.put(LovValue.FIELD_DESCRIPTION, this.description);
        }
        if(this.tag != null) {
            map.put(LovValue.FIELD_TAG, this.tag);
        }
        if(this.parentValue != null) {
            map.put(LovValue.FIELD_PARENT_VALUE, this.parentValue);
        }
        if(this.orderSeq != null) {
            map.put(LovValue.FIELD_ORDER_SEQ, this.orderSeq);
        }
        if(this.children != null) {
            map.put(FIELD_CHILDREN, this.children);
        }
        if(this.metadata != null) {
            map.put(FIELD_METADATA, this.metadata);
        }
        return map;
    }
    
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("LovValueDTO [value=").append(value).append(", meaning=").append(meaning)
                        .append(", description=").append(description).append(", tag=").append(tag)
                        .append(", parentValue=").append(parentValue).append(", children=").append(children)
                        .append(", orderSeq=").append(orderSeq).append(", metadata=").append(metadata).append("]");
        return builder.toString();
    }
    
}
