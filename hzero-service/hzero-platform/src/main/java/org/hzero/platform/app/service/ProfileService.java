package org.hzero.platform.app.service;

import java.util.List;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.domain.entity.Profile;

/**
 * 配置维护业务层接口
 *
 * @author yunxiang.zhou01 @hand-china.com 2018-06-05
 */
public interface ProfileService {

    /**
     * 根据条件查询配置维护dto
     *
     * @param pageRequest 分页工具类
     * @param profile 配置维护
     * @return 配置维护dto list
     */
    List<Profile> list(PageRequest pageRequest, Profile profile);

    /**
     * 新增更新保存值集
     *
     * @param profile 配置维护
     * @return 保存之后的配置维护
     */
    Profile insertOrUpdate(Profile profile);

    /**
     * 删除
     *
     * @param profileId 应用维护
     */
    void delete(Long profileId, Long tenantId, Boolean isPlatform);
}
