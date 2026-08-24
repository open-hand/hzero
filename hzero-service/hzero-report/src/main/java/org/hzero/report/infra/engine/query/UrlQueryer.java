package org.hzero.report.infra.engine.query;

import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.properties.CoreProperties;
import org.hzero.core.util.Regexs;
import org.hzero.core.util.ResponseUtils;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.*;
import org.hzero.report.infra.util.CustomTokenUtils;
import org.hzero.report.infra.util.ElementUtils;
import org.hzero.report.infra.util.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * url数据集查询实现
 *
 * @author shuangfei.zhu@hand-china.com 2019/12/03 20:42
 */
public class UrlQueryer implements Query {

    private static final Logger logger = LoggerFactory.getLogger(UrlQueryer.class);

    private final ReportParameter parameter;
    private final List<MetaDataColumn> metaDataColumns;

    public UrlQueryer(ReportParameter parameter) {
        this.parameter = parameter;
        this.metaDataColumns = this.parameter == null ? new ArrayList<>() : new ArrayList<>(this.parameter.getMetaColumns());
    }

    @Override
    public List<MetaDataColumn> getMetaDataColumns() {
        return this.metaDataColumns;
    }

    /**
     * 解析元数据列(url类型不支持自动解析，需要手动录入)
     *
     * @param sqlText url
     * @return List<MetaDataColumn>
     */
    @Override
    public List<MetaDataColumn> parseMetaDataColumns(String sqlText) {
        return Collections.emptyList();
    }

    /**
     * 解析查询参数项(url类型不支持自动解析sql，构建下拉选)
     *
     * @param sqlText url
     * @return List<ReportQueryParamItem>
     */
    @Override
    public List<ReportQueryParamItem> parseQueryParamItems(String sqlText) {
        return Collections.emptyList();
    }


    /**
     * 获取元数据行信息
     *
     * @return List<MetaDataRow>
     */
    @Override
    public List<MetaDataRow> getMetaDataRows() {
        String url = this.prePageUrl(this.parameter);
        try {
            String data = ResponseUtils.getResponse(getResult(url), String.class);
            if (isString(data)) {
                // 异步请求，api返回uuid
                return Collections.singletonList(new MetaDataRow().setAsyncReportUuid(data));
            }
            ObjectMapper objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
            JsonNode jsonNode = objectMapper.readTree(data);
            if (jsonNode.isArray()) {
                List<JSONObject> list = objectMapper.readValue(data, new TypeReference<List<JSONObject>>() {
                });
                return this.getMetaDataRows(list, this.getColumns(this.parameter.getMetaColumns()));
            } else {
                Page<JSONObject> page = objectMapper.readValue(data, new TypeReference<Page<JSONObject>>() {
                });
                return this.getMetaDataRows(page.getContent(), this.getColumns(this.parameter.getMetaColumns()));
            }
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID, e);
        }
    }

    /**
     * 检查字符串是否普通字符串, 大小写、数字、下划线、点
     *
     * @param data 接口返回数据
     * @return 是返回true, 不是返回false
     */
    public static boolean isString(String data) {
        return Pattern.matches(Regexs.CODE, data);
    }

    /**
     * 获取列集合
     */
    private List<MetaDataColumn> getColumns(List<MetaDataColumn> metaDataColumns) {
        return metaDataColumns.stream().filter(x -> !HrptConstants.ColumnType.COMPUTED.equals(x.getType())).collect(Collectors.toList());
    }

    /**
     * 获取元数据行
     */
    protected List<MetaDataRow> getMetaDataRows(List<JSONObject> response, List<MetaDataColumn> sqlColumns) {
        List<MetaDataRow> rows = new ArrayList<>();
        for (JSONObject object : response) {
            MetaDataRow row = new MetaDataRow();
            for (MetaDataColumn column : sqlColumns) {
                // 隐藏的列不生成到path中
                if (column.getHidden() == BaseConstants.Flag.NO) {
                    String value = object.getString(column.getName());
                    row.add(new MetaDataCell(column, column.getName(), value));
                }
            }
            rows.add(row);
        }
        return rows;
    }

    private String prePageUrl(ReportParameter parameter) {
        // 分页信息
        SqlPageInfo pageInfo = this.parameter.getSqlPageInfo();
        String url = parameter.getSqlText();
        return prePageUrl(url, pageInfo.getPage(), pageInfo.getSize());
    }

    private String prePageUrl(String url, int page, int size) {
        if (url.contains(BaseConstants.Symbol.QUESTION)) {
            return url + "&page=" + page + "&size=" + size;
        } else {
            return url + "?page=" + page + "&size=" + size;
        }
    }

    /**
     * 获取XML数据
     */
    @Override
    public Document getMetaDataXml(String sqlType, String url) {
        String result = docGetResult(url);
        // 给获取到的数据添加前缀和后缀,然后转换成xml
        return ElementUtils.jsonToXml(result);
    }

    private String docGetResult(String url) {
        String data = ResponseUtils.getResponse(getResult(url), String.class);
        String result;
        // 判断类型
        try {
            // 分页数据
            Page<JSONObject> page = ApplicationContextHelper.getContext().getBean(ObjectMapper.class).readValue(data, new TypeReference<Page<JSONObject>>() {
            });
            Map<String, Object> map = new HashMap<>(16);
            map.put(HrptConstants.DataXmlAttr.DEFAULT_ROW, page.getContent());
            result = JSON.toJSONString(map);
        } catch (Exception e) {
            try {
                // list数据
                List<JSONObject> list = ApplicationContextHelper.getContext().getBean(ObjectMapper.class).readValue(data, new TypeReference<List<JSONObject>>() {});
                Map<String, Object> map = new HashMap<>(16);
                map.put(HrptConstants.DataXmlAttr.DEFAULT_ROW, list);
                result = JSON.toJSONString(map);
            } catch (Exception ex) {
                // 单个对象数据
                result = data;
            }
        }
        return result;
    }

    /**
     * 获取Map数据
     */
    @Override
    public Map<String, Object> getMetaDataMap(String sqlType, String url) {
        String result = docGetResult(url);
        return MapUtils.jsonToMap(result);
    }

    /**
     * 请求接口获取数据
     *
     * @param url url
     * @return 数据
     */
    private ResponseEntity<String> getResult(String url) {
        url = preToken(url);
        logger.info("Dataset url: {}", url);
        try {
            RestTemplate restTemplate = ApplicationContextHelper.getContext().getBean(RestTemplate.class);
            ResponseEntity<String> response;
            try {
                // 当前没有jwt，自己组装
                if (RequestContextHolder.getRequestAttributes() == null) {
                    HttpHeaders requestHeaders = new HttpHeaders();
                    requestHeaders.add("jwt_token", getJwtToken());
                    HttpEntity<String> requestEntity = new HttpEntity<>(null, requestHeaders);
                    response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
                } else {
                    response = restTemplate.getForEntity(url, String.class);
                }
            } catch (Exception e) {
                logger.warn("Internal call failed. url : {}", url, e);
                response = new RestTemplate().getForEntity(url, String.class);
            }
            return response;
        } catch (Exception e) {
            throw new CommonException(HrptMessageConstants.REQUEST_URL, e);
        }
    }

    private String getJwtToken() throws IOException {
        ObjectMapper objectMapper = ApplicationContextHelper.getContext().getBean(ObjectMapper.class);
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        CoreProperties coreProperties = ApplicationContextHelper.getContext().getBean(CoreProperties.class);
        Signer signer = new MacSigner(coreProperties.getOauthJwtKey());
        return "Bearer " + JwtHelper.encode(objectMapper.writeValueAsString(userDetails), signer).getEncoded();
    }

    /**
     * 拼接accessToken
     */
    private String preToken(String url) {
        String token = CustomTokenUtils.getToken();
        if (StringUtils.isBlank(token)) {
            return url;
        }
        if (url.contains(BaseConstants.Symbol.QUESTION)) {
            return url + "&access_token=" + token;
        } else {
            return url + "?access_token=" + token;
        }
    }

    /**
     * 获取元数据总条数
     *
     * @return 行条数
     */
    @Override
    public long getMetaDataCount() {
        return getMetaDataCount(this.parameter.getSqlText());
    }

    /**
     * 获取数据总数
     */
    @Override
    public long getMetaDataCount(String url) {
        // 获取数据总数，分页设置为0， 10
        String pageUrl = prePageUrl(url, 0, 10);
        ResponseEntity<String> responseEntity = getResult(pageUrl);
        String data = ResponseUtils.getResponse(responseEntity, String.class);
        try {
            Page<JSONObject> page = ApplicationContextHelper.getContext().getBean(ObjectMapper.class).readValue(data, new TypeReference<Page<JSONObject>>() {
            });
            return page.getTotalElements();
        } catch (Exception e) {
            try {
                // list数据
                List<JSONObject> list = ApplicationContextHelper.getContext().getBean(ObjectMapper.class).readValue(data, new TypeReference<List<JSONObject>>() {
                });
                return list.size();
            } catch (Exception ex) {
                // 单个对象数据
                return 1;
            }
        }
    }
}
