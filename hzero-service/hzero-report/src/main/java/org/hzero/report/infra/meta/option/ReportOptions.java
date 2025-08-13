package org.hzero.report.infra.meta.option;

import java.io.Serializable;

import lombok.*;

/**
 * 报表配置选项(report表的options字段)持久化类（用于构建JSON配置字符）
 *
 * @author xianzhi.chen@hand-china.com 2018年10月22日下午4:33:13
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ReportOptions implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7546812078339500310L;
    /**
     * 布局形式.H:横向;V:纵向
     */
    private String layout;
    /**
     * 统计列布局形式.H:横向;V:纵向
     */
    private String statColumnLayout;
}
