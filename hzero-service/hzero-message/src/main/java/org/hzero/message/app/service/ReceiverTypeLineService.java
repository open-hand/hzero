package org.hzero.message.app.service;

import org.hzero.message.domain.entity.ReceiverTypeLine;
import org.hzero.message.domain.entity.Unit;
import org.hzero.message.domain.entity.UserGroup;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 接收组行应用服务
 *
 * @author liufanghan
 */
public interface ReceiverTypeLineService {

    /**
     * 分页获取接收者类型行
     *
     * @param pageRequest    分页
     * @param receiverTypeId 接收组ID
     * @return 查询结果
     */
    Page<ReceiverTypeLine> listReceiverTypeLine(PageRequest pageRequest, Long receiverTypeId);

    /**
     * 删除接收组行
     *
     * @param receiverLineList 接收组行
     */
    void deleteUserGroup(List<ReceiverTypeLine> receiverLineList);

    /**
     * 创建接收组行
     *
     * @param receiverTypeId       接收组ID
     * @param receiverTypeLineList 接收组行
     * @return 接收组行
     */
    List<ReceiverTypeLine> createReceiverTypeLine(long receiverTypeId, List<ReceiverTypeLine> receiverTypeLineList);

    /**
     * 分页获取用户组
     *
     * @param pageRequest    分页
     * @param receiverTypeId 接收组ID
     * @param groupName      用户组名称
     * @param groupCode      用户组编码
     * @return 用户组
     */
    Page<UserGroup> listUserGroups(PageRequest pageRequest, long receiverTypeId, String groupName, String groupCode);

    /**
     * 分页获取组织
     *
     * @param pageRequest    分页
     * @param receiverTypeId 接收组ID
     * @param unitName       组织ID
     * @param unitCode       组织编码
     * @return 组织
     */
    Page<Unit> listUnits(PageRequest pageRequest, long receiverTypeId, String unitName, String unitCode);

}
