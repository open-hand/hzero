package org.hzero.message.domain.repository;

import org.hzero.message.domain.entity.ReceiverTypeLine;
import org.hzero.message.domain.entity.Unit;
import org.hzero.message.domain.entity.UserGroup;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 接收者类型行服务接口
 *
 * @author minghui.qiu@hand-china.com
 * @date 2019-06-12 09:03:01
 */
public interface ReceiverTypeLineRepository extends BaseRepository<ReceiverTypeLine> {

    /**
     * 分页获取消息接收者行
     *
     * @param pageRequest    分页
     * @param receiverTypeId 接收组ID
     * @return 接收组行
     */
    Page<ReceiverTypeLine> listReceiveTypeLine(PageRequest pageRequest, Long receiverTypeId);

    /**
     * 获取用户组
     *
     * @param pageRequest    分页
     * @param receiverTypeId 接收组ID
     * @param groupName      用户组名
     * @param groupCode      用户组编码
     * @return 用户组
     */
    Page<UserGroup> listUserGroups(PageRequest pageRequest, long receiverTypeId, String groupName, String groupCode);


    /**
     * 分页获取组织
     *
     * @param pageRequest    分页
     * @param receiverTypeId 接收组ID
     * @param unitName       组织名
     * @param unitCode       组织编码
     * @return 组织
     */
    Page<Unit> listUnits(PageRequest pageRequest, long receiverTypeId, String unitName, String unitCode);

    /**
     * 获取原消息接收者行
     *
     * @param receiverTypeId 接收组ID
     * @return 接收组行
     */
    List<ReceiverTypeLine> listOldReceiveTypeLine(Long receiverTypeId);
}

