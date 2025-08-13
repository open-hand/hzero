package org.hzero.boot.platform.lov.adapter;

import java.util.List;
import java.util.Map;

import org.hzero.boot.platform.lov.dto.LovDTO;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.boot.platform.lov.dto.LovViewDTO;

/**
 * 值集值接口适配器,用以支持值集值查询
 *
 * @author gaokuo.dai@hand-china.com 2018年6月30日下午6:29:08
 */
public interface LovAdapter {

    /**
     * <p>根据值集代码查询值集头信息</p>
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集可不传此参数)
     * @return 查询到的值集头信息, 无权访问时范返回null
     */
    default LovDTO queryLovInfo(String lovCode, Long tenantId) {
        return queryLovInfo(lovCode, tenantId, null, false);
    }

    /**
     * <p>根据值集代码查询值集头信息</p>
     *
     * @param lovCode     值集代码
     * @param tenantId    租户ID(全局值集可不传此参数)
     * @param lang        语言
     * @param publicQuery 是否为公开API查询
     * @return 查询到的值集头信息, 无权访问时范返回null
     */
    LovDTO queryLovInfo(String lovCode, Long tenantId, String lang, Boolean publicQuery);

    /**
     * <p>根据值集视图代码查询值集视图头信息</p>
     *
     * @param lovViewCode 值集视图代码
     * @param tenantId    租户ID(全局值集可不传此参数)
     * @return 查询到的值集头信息, 无权访问时范返回null
     */
    LovViewDTO queryLovViewInfo(String lovViewCode, Long tenantId);

    /**
     * <p>根据值集代码查询值集值</p>
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集可不传此参数)
     * @return 查询到的值集值, 无权访问时返回null
     */
    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId);

    /**
     * <p>根据值集代码查询值集值</p>
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集可不传此参数)
     * @param lang     语言
     * @return 查询到的值集值, 无权访问时返回null
     */
    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String lang);

    /**
     * <p>根据值集代码查询值集值</p>(固定值集直接查询，sql和url值集执行翻译sql)
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集可不传此参数)
     * @param params   参数
     * @return 查询到的值集值, 无权访问时返回null
     */
    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, List<String> params);

    /**
     * <p>根据值集代码查询值集值</p>(固定值集直接查询，sql和url值集执行翻译sql)
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集可不传此参数)
     * @param params   参数
     * @param lang     语言
     * @return 查询到的值集值, 无权访问时返回null
     */
    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, List<String> params, String lang);

    /**
     * <p>根据值集代码查询值集值</p>(固定值集直接查询，sql和url值集执行翻译sql)
     *
     * @param lovCode     值集代码
     * @param tenantId    租户ID(全局值集可不传此参数)
     * @param queryParams 普通查询参数
     * @param params      值字段参数
     * @return 查询到的值集值, 无权访问时返回null
     */
    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, Map<String, Object> queryParams, List<String> params);

    /**
     * <p>根据值集代码查询值集值</p>(固定值集直接查询，sql和url值集执行翻译sql)
     *
     * @param lovCode     值集代码
     * @param tenantId    租户ID(全局值集可不传此参数)
     * @param queryParams 普通查询参数
     * @param params      值字段参数
     * @param lang        语言
     * @return 查询到的值集值, 无权访问时返回null
     */
    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, Map<String, Object> queryParams, List<String> params, String lang);

    /**
     * <p>根据值集代码和值集value查询对应的meaning</p>
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集可不传此参数)
     * @param value    值集的value
     * @return 查询到的值集值, 无权访问时返回null
     */
    String queryLovMeaning(String lovCode, Long tenantId, String value);

    /**
     * <p>根据值集代码和值集value查询对应的meaning</p>
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID(全局值集可不传此参数)
     * @param value    值集的value
     * @param lang     语言
     * @return 查询到的值集值, 无权访问时返回null
     */
    String queryLovMeaning(String lovCode, Long tenantId, String value, String lang);
}
