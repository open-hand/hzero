package org.hzero.report.infra.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.report.infra.config.ReportConfig;
import org.hzero.report.infra.constant.HrptConstants;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * JSON转HTML模板使用的map结构
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/03 13:36
 */
public class MapUtils {

    private MapUtils() {
    }

    public static Map<String, Object> jsonToMap(String jsonString) {
        Map<String, Object> result = new HashMap<>(16);
        Object json = JSONObject.parse(jsonString);
        List<JSONObject> mapList = new ArrayList<>();
        if (json instanceof JSONArray) {
            JSONArray jsonArray = (JSONArray) json;
            mapList.addAll(jsonArray.toJavaList(JSONObject.class));
        } else if (json instanceof JSONObject) {
            mapList.add((JSONObject) json);
        }
        result.put(HrptConstants.DataXmlAttr.DEFAULT_DS, mapList);
        // 如果配置了二维码地址，插入二维码变量
        ReportConfig reportConfig = ApplicationContextHelper.getContext().getBean(ReportConfig.class);
        if (StringUtils.isNotBlank(reportConfig.getQrCodeUrl())) {
            result.put(HrptConstants.QR_CODE_URL, reportConfig.getQrCodeUrl());
        }
        return result;
    }
}
