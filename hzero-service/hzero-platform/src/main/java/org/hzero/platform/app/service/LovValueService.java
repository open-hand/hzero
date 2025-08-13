/**
 *
 */
package org.hzero.platform.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.platform.api.dto.LovValueDTO;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 值集值App服务
 *
 * @author gaokuo.dai@hand-china.com 2018年6月25日下午6:37:16
 */
public interface LovValueService {

    /**
     * 查询值集值
     *
     * @param lovCode  值集代码
     * @param tenantId 租户ID
     * @param tag 值tag
     * @return 值集值
     */
    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag);

    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag, String lang);

    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag, Lov lov);

    List<LovValueDTO> queryLovValue(String lovCode, Long tenantId, String tag, Lov lov, String lang);

    /**
     * 分页获取值集值
     *
     * @param lovValue    值集
     * @param pageRequest 分
     * @return 值集值
     */
    Page<LovValueDTO> pageLovValue(LovValue lovValue, PageRequest pageRequest);

    /**
     * 根据值集代码和父值集值查询子值集值
     *
     * @param lovCode     值集代码
     * @param parentValue 父值集值
     * @param tenantId    租户ID
     * @return 子值集值
     */
    List<LovValueDTO> queryLovValueByParentValue(String lovCode, String parentValue, Long tenantId);

    /**
     * 根据tag和值集代码查询值集值
     *
     * @param lovCode  值集代码
     * @param tag      tag
     * @param tenantId 租户ID
     * @return tag中的值集值
     */
    List<LovValueDTO> queryLovValueByTag(String lovCode, String tag, Long tenantId);

    /**
     * 批量根据值集代码查询值集值<br>
     * 例:<br/>
     * queryMap = {"a": "LOV_CODE_A", "b": "LOV_CODE_B"}<br/>
     * result = {"a": [...(lov values by LOV_CODE_A)], "b": [...(lov values by LOV_CODE_B)]}
     *
     * @param queryMap 查询条件,key为返回时的key, value为值集代码
     * @param tenantId 租户ID,全局查询时可空
     * @return 参见示例
     */
    Map<String, List<LovValueDTO>> batchQueryLovValue(Map<String, String> queryMap, Long tenantId, String lang);

    /**
     * 插入值集值
     *
     * @param lovValue
     * @return
     */
    LovValue addLovValue(LovValue lovValue);

    /**
     * 按主键更新值集值
     *
     * @param lovValue
     * @return
     */
    LovValue updateLovValue(LovValue lovValue);

    /**
     * 获得父子值集树<br/>
     * 查询参数Map中,key为值集代码,value为该代码在父子树结构中的顺序序号<br/>
     * 祖先的序号需要小于子孙的序号<br/>
     * 例如A为B的父值集,B为C的父值集,则传入参数应该为<br/>
     * {"A" = "1", "B" = "2", "C" = "3"}<br/>
     * 序号不要求连续,只需要嫩判断大小关系即可
     *
     * @param queryMap 查询条件
     * @param tenantId 租户ID
     * @return 树
     */
    List<LovValueDTO> queryLovValueTree(Map<String, String> queryMap, Long tenantId);

    /**
     * 批量删除值集值
     *
     * @param lovValues 值集值集合
     */
    void batchDeleteLovValuesByPrimaryKey(List<LovValue> lovValues);
}
