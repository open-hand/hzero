package org.hzero.boot.platform.lov.handler.impl;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.lov.constant.LovConstants;
import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.handler.LovMeaningSqlTransformer;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.FieldNameUtils;

import io.choerodon.core.exception.CommonException;

/**
 * 含义查询值集SQL改写器默认实现<br/>
 * 基于java正则表达式
 *
 * @author gaokuo.dai@hand-china.com 2018年9月8日下午10:42:01
 */
public class DefaultLovMeaningSqlTransformer implements LovMeaningSqlTransformer {
    /**
     * select部分
     */
    private static final Pattern SELECT_PATTERN = Pattern.compile("select.*?from", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);
    /**
     * where部分
     */
    private static final Pattern WHERE_PATTERN = Pattern.compile("where.*$", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL);

    @Override
    public String doTransform(String originSql, LovDTO lov) {
        if (lov == null || StringUtils.isAnyBlank(originSql, lov.getValueField(), lov.getDisplayField())) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
        }

        String selectStr = "select "
                + StringUtils.EMPTY
                + FieldNameUtils.camel2Underline(lov.getValueField(), false)
                + " as " + LovConstants.Field.IDENTITY
                + ", " + StringUtils.EMPTY
                + FieldNameUtils.camel2Underline(lov.getDisplayField(), false)
                + " as " + LovConstants.Field.MEANING
                + " from";

        String whereStr = "where 1 = 1 <if test=\"identities != null and identities != '' \"> and "
                + StringUtils.EMPTY
                + FieldNameUtils.camel2Underline(lov.getValueField(), false) +
                " in (\\#{identities})</if>";
        return WHERE_PATTERN.matcher(SELECT_PATTERN.matcher(originSql).replaceAll(selectStr)).replaceAll(whereStr);
    }

}
