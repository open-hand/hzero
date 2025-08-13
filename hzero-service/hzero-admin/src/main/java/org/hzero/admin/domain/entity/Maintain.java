package org.hzero.admin.domain.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.starter.keyencrypt.core.Encrypt;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 11:16 上午
 */
@ApiModel("在线运维")
@VersionAudit
@ModifyAudit
@Table(name = "hadm_maintain")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Maintain extends AuditDomain {

    public static final String FIELD_MAINTAIN_ID = "maintainId";


    @Encrypt
    @Id
    @GeneratedValue
    @ApiModelProperty("主键ID")
    private Long maintainId;

    @ApiModelProperty("从..版本到..版本的升级运维规则")
    @Length(max = 60)
    @NotEmpty
    private String maintainVersion;

    @ApiModelProperty("运维规则的描述，备注信息")
    @Length(max = 240)
    private String description;

    @ApiModelProperty("运维规则的状态，包括UNUSED、ACTIVE、USED")
    @LovValue(value = "HADM.MAINTAIN_STATE", meaningField = "stateMeaning")
    @NotEmpty
    private String state;

    @Transient
    private String stateMeaning;

    public Long getMaintainId() {
        return maintainId;
    }

    public Maintain setMaintainId(Long maintainId) {
        this.maintainId = maintainId;
        return this;
    }

    public String getMaintainVersion() {
        return maintainVersion;
    }

    public Maintain setMaintainVersion(String maintainVersion) {
        this.maintainVersion = maintainVersion;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Maintain setDescription(String description) {
        this.description = description;
        return this;
    }

    public String getState() {
        return state;
    }

    public Maintain setState(String state) {
        if (state == null) {
            return this;
        }
        State.valueOf(state);
        this.state = state;
        return this;
    }

    public String getStateMeaning() {
        return stateMeaning;
    }

    public void setStateMeaning(String stateMeaning) {
        this.stateMeaning = stateMeaning;
    }

    public enum State {
        /**
         * 未使用
         */
        UNUSED,
        /**
         * 正在使用
         */
        ACTIVE,
        /**
         * 使用过
         */
        USED
    }

}
