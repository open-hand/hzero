package org.hzero.report.infra.engine.data;

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.util.VelocityUtils;

/**
 * 报表SQL或URL模版类
 *
 * @author xianzhi.chen@hand-china.com 2018年10月17日下午8:54:47
 */
public class ReportSqlTemplate {

    /**
     * sql或url
     */
    private String template;
    private final Map<String, Object> parameters;
    private static final Pattern PATTERN = Pattern.compile("\\{.*?}");


    public ReportSqlTemplate(String template, Map<String, Object> parameters) {
        this.template = template;
        this.parameters = parameters;
    }

    public String execute() {
        String sqlType = Dataset.checkSqlType(template);
        if (!Objects.equals(sqlType, HrptConstants.DataSetType.TYPE_A)) {
            return VelocityUtils.parse(this.template, this.parameters);
        }
        // 正则匹配替换路径参数
        Matcher titleMatcher = PATTERN.matcher(template);
        while (titleMatcher.find()) {
            String arg = titleMatcher.group();
            String param = arg.substring(1, arg.length() - 1);
            if (Objects.equals(param, HrptConstants.PermissionParam.ORGANIZATION_ID) || Objects.equals(param, HrptConstants.PermissionParam.TENANT_ID)) {
                // 若自己指定，使用自己指定的，不指定使用用户信息
                if (parameters.containsKey(param)) {
                    template = template.replace(arg, String.valueOf(parameters.get(param)));
                } else {
                    template = template.replace(arg, String.valueOf(parameters.get(HrptConstants.PermissionParam.PREFIX + HrptConstants.PermissionParam.TENANT_ID)));
                }
            } else {
                template = template.replace(arg, String.valueOf(parameters.get(param)));
            }
        }

        // 根据参数和url，组装完整url
        parameters.forEach(((name, value) -> {
            String str = String.valueOf(value);
            if (StringUtils.isNotBlank(str) && !name.startsWith(HrptConstants.FixedParam.PREFIX) && !name.startsWith(HrptConstants.PermissionParam.PREFIX)) {
                template = addParam(template, name, str);
            }
        }));
        return template;
    }

    private String addParam(String url, String name, String value) {
        if (url.contains(BaseConstants.Symbol.QUESTION)) {
            return url + "&" + name + "=" + value;
        } else {
            return url + "?" + name + "=" + value;
        }
    }
}
