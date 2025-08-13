package io.choerodon.mybatis.domain;

import javax.persistence.criteria.JoinType;

/**
 * <p>
 * description
 * </p>
 *
 * @author rui.shi@hand-china.com  2019/01/2019/1/30
 */
public class JoinTableSql {
    private JoinType joinType;
    private String Content;

    public JoinTableSql(JoinType joinType, String content) {
        this.joinType = joinType;
        Content = content;
    }

    public JoinType getJoinType() {
        return joinType;
    }

    public void setJoinType(JoinType joinType) {
        this.joinType = joinType;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }
}
