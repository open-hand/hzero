package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.Lov;
import org.hzero.platform.domain.entity.LovValue;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p><b>name</b> LovMapper</p>
 * <p><b>description</b> 值集值Mapper</p>
 *
 * @author gaokuo.dai@hand-china.com    2018年6月5日下午7:45:46
 * @version 1.0
 */
public interface LovValueMapper extends BaseMapper<LovValue> {

    /**
     * 根据条件查询待插入缓存的数据
     *
     * @param queryParam 查询条件
     * @return 值集值
     */
    List<LovValue> listLovValueForCache(@Param("queryParam") LovValue queryParam, @Param("lang") String lang);

    /**
     * 根据头信息更新行
     *
     * @param header 头信息
     * @return 更新的行数
     */
    int updateLovValueByHeaderInfo(Lov header);

    /**
     * 根据头ID删除行
     *
     * @param lovId lovId
     * @return 被删除的数量
     */
    int deleteByLovId(@Param("lovId") Long lovId);

    /**
     * 根据头ID查询行信息
     *
     * @param lovId    值集ID
     * @param tenantId 租户ID
     * @param value    值
     * @param meaning  含义
     * @return 固定值集值
     */
    List<LovValue> selectLovValueByLovId(@Param("lovId") Long lovId,
                                         @Param("tenantId") Long tenantId,
                                         @Param("value") String value,
                                         @Param("meaning") String meaning);

    /**
     * 查询与给定代码重复的数据库记录数
     *
     * @param lovValue 查询条件
     * @return 重复的数据库记录数
     */
    int selectRepeatCodeCount(LovValue lovValue);

    /**
     * 查询数据组维度值
     *
     * @param lovId    值集ID
     * @param tenantId 租户
     * @return 值集值
     */
    List<LovValue> selectLovValueForDataGroup(@Param("lovId") Long lovId, @Param("tenantId") Long tenantId);

}
