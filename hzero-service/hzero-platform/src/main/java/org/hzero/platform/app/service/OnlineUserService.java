package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.platform.api.dto.OnLineUserDTO;
import org.hzero.platform.api.dto.OnlineUserCountDTO;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 在线用户
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/12 13:40
 */
public interface OnlineUserService {

    /**
     * 分页查询在线用户
     *
     * @param page     页码
     * @param size     每页数量
     * @param tenantId 租户ID
     * @return 分页数据
     */
    Page<OnLineUserDTO> pageOnlineUser(int page, int size, Long tenantId);

    /**
     * 查询在线用户
     *
     * @param tenantId 租户Id
     * @return 在线用户
     */
    List<OnLineUserDTO> listOnlineUser(Long tenantId);

    /**
     * 查询在线账户数量
     *
     * @return 在线数量
     */
    Integer countOnline();

    /**
     * 统计在线用户
     *
     * @return 统计结果
     */
    List<OnlineUserCountDTO> countOnlineUser();

    /**
     * 在线时长人员列表
     *
     * @param pageRequest 分页请求
     * @param hour        在线时长
     * @return 在线人员信息
     */
    Page<OnLineUserDTO> pageWithHour(PageRequest pageRequest, String hour);
}
