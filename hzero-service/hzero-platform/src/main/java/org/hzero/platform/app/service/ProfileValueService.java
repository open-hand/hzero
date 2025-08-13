package org.hzero.platform.app.service;

import java.util.List;

import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.domain.entity.ProfileValue;

/**
 * @author yunxiang.zhou01 @hand-china.com 2018/06/05
 */
public interface ProfileValueService {

    /**
     * 根据配置维护主键profileId查询
     *
     * @param pageRequest 分页工具类
     * @param profileId   配置维护主键profileId
     * @return List<ProfileValue>
     * @author yunxiang.zhou01@hand-china.com 2018-06-07 19:48
     */
    List<ProfileValue> listByProfileId(PageRequest pageRequest, Long profileId);

    /**
     * 根据配置维护值名得到当前用户最低层次的配置维护值 默认 平台级 > 租户级 > 角色级 > 用户级
     *
     * @param level       应用维度
     * @param profileName 配置维护值名
     * @param userId      用户id
     * @param roleId      角色id
     * @param tenantId    租户id
     * @return 配置维护值
     * @author yunxiang.zhou01@hand-china.com 2018-06-07 20:20
     */
    String getProfileValueByProfileName(String level, String profileName, Long userId, Long roleId, Long tenantId);

    /**
     * 从数据库删除后，也从redis中删除
     *
     * @param profileValueId 配置维护值id
     */
    void delete(Long profileValueId, Long organizationId);
}
