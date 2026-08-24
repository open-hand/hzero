package org.hzero.platform.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.Config;
import org.hzero.platform.domain.vo.TitleConfigCacheVO;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * <p>
 * 系统配置mapper
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/06/19 11:42
 */
public interface ConfigMapper extends BaseMapper<Config> {

    /**
     * 根据租户id查询租户级的配置，如果没有配置则引用平台级的配置，如果有则采用自己的
     *
     * @param tenantId 租户id
     * @return 系统配置list
     */
    List<Config> selectConfigByTenantId(Long tenantId);

    /**
     * 查询config多语言Map值
     *
     * @param configId 查询条件
     * @return 查询结果
     */
    List<TitleConfigCacheVO> selectLanguageValueMap(@Param("configId") Long configId);
}
