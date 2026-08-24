package org.hzero.report.infra.util;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.hzero.report.infra.config.ReportConfig;
import org.hzero.report.infra.constant.HrptConstants;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * json转xml
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/02 20:27
 */
public class ElementUtils {

    private ElementUtils() {
    }

    public static Document jsonToXml(String jsonString) {
        Object json = JSONObject.parse(jsonString);
        Document doc = DocumentHelper.createDocument();
        Element root = doc.addElement(HrptConstants.DataXmlAttr.DEFAULT_DS);
        // 如果配置了二维码地址，插入二维码变量
        ReportConfig reportConfig = ApplicationContextHelper.getContext().getBean(ReportConfig.class);
        if (StringUtils.isNotBlank(reportConfig.getQrCodeUrl())) {
            root.addElement(HrptConstants.QR_CODE_URL).addText(reportConfig.getQrCodeUrl());
        }
        toXml(json, root, null);
        return doc;
    }

    /**
     * 将json字符串转换成xml
     *
     * @param jsonElement   待解析json对象元素
     * @param parentElement 上一层xml的dom对象
     * @param name          父节点
     */
    private static void toXml(Object jsonElement, Element parentElement, String name) {
        if (jsonElement instanceof JSONArray) {
            // json数组数据，需继续解析
            JSONArray jsonArray = (JSONArray) jsonElement;
            for (Object arrayElement : jsonArray) {
                toXml(arrayElement, parentElement, name);
            }
        } else if (jsonElement instanceof JSONObject) {
            // json对象字符串，需要继续解析
            JSONObject jsonObject = (JSONObject) jsonElement;
            Element currentElement = null;
            if (name != null) {
                currentElement = parentElement.addElement(name);
            }
            Set<Map.Entry<String, Object>> set = jsonObject.entrySet();
            for (Map.Entry<String, Object> s : set) {
                toXml(s.getValue(), currentElement != null ? currentElement : parentElement, s.getKey());
            }
        } else {
            // 说明是一个键值对,可以作为节点插入了
            addAttribute(parentElement, name, jsonElement == null ? "" : jsonElement.toString());
        }
    }

    /**
     * @param element 父节点
     * @param name    子节点的名字
     * @param value   子节点的值
     */
    private static void addAttribute(Element element, String name, String value) {
        // 增加子节点，并为子节点赋值
        Element el = element.addElement(name);
        el.addText(value);
    }
}
