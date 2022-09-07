package org.hzero.iam.infra.constant;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * API 标签标记
 * </p>
 *
 * @author xiaoyu.zhao@hand-china.com
 */
public enum ApiTypeTag {
    /**
     * 页面API标签标记
     */
    PAGE_TAG("PAGE"),
    /**
     * 后端API标签标记
     */
    BACKEND_TAG("BACKEND");

    private String value;

    ApiTypeTag(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static String[] getApiTagValues() {
        List<String> resultArray = new ArrayList<>();
        for (ApiTypeTag apiTypeTag : ApiTypeTag.values()) {
            resultArray.add(apiTypeTag.getValue());
        }
        return resultArray.toArray(new String[values().length]);
    }
}
