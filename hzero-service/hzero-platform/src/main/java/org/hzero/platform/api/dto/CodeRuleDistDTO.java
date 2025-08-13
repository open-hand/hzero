package org.hzero.platform.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.hzero.mybatis.domian.SecurityToken;
import org.hzero.platform.domain.entity.CodeRuleDist;

import java.util.List;

/**
 * <p>
 * 编码规则分配DTO
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/02 16:31
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CodeRuleDistDTO extends CodeRuleDist {

    private String levelCodeDescription;
    private String levelValueDescription;
    private List<CodeRuleDetailDTO> codeRuleDetailDTOList;

    @Override
    public Class<? extends SecurityToken> associateEntityClass() {
        return (Class<? extends SecurityToken>) this.getClass().getSuperclass();
    }

    @Override
    public String toString() {
        return "CodeRuleDistDTO{" + ", levelCodeDescription='"
                + levelCodeDescription + '\'' + ", levelValueDescription='" + levelValueDescription + '\''
                + '}';
    }

    public String getLevelCodeDescription() {
        return levelCodeDescription;
    }

    public void setLevelCodeDescription(String levelCodeDescription) {
        this.levelCodeDescription = levelCodeDescription;
    }

    public String getLevelValueDescription() {
        return levelValueDescription;
    }

    public void setLevelValueDescription(String levelValueDescription) {
        this.levelValueDescription = levelValueDescription;
    }

    public List<CodeRuleDetailDTO> getCodeRuleDetailDTOList() {
        return codeRuleDetailDTOList;
    }

    public void setCodeRuleDetailDTOList(List<CodeRuleDetailDTO> codeRuleDetailDTOList) {
        this.codeRuleDetailDTOList = codeRuleDetailDTOList;
    }
}
