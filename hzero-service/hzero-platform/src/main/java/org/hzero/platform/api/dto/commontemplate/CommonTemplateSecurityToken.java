package org.hzero.platform.api.dto.commontemplate;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModelProperty;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.CommonTemplate;

/**
 * 通用模板安全token实现
 *
 * @author bergturing 2020/08/04 11:38
 */
public class CommonTemplateSecurityToken implements SecurityToken {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @ApiModelProperty(
            hidden = true
    )
    private String _token;

    @Override
    public String get_token() {
        return this._token;
    }

    @Override
    public void set_token(String tokenValue) {
        this._token = tokenValue;
    }

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return CommonTemplate.class;
    }
}
