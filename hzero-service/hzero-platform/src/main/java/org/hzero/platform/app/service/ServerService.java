package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.platform.domain.entity.Server;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 文件服务器应用服务
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
public interface ServerService {
    
    /**
     * 根据组织id分页查询fileServer
     *
     * @param Server 文件服务器信息
     * @param pageRequest   分页对象
     * @return Page 分页
     */
    Page<Server> findServers(Server Server, PageRequest pageRequest);

    /**
     * 查询hdspFileServer，
     *
     * @param organizationId 组织
     * @param id             主键
     * @return 查询出的client
     */
    Server findServerByServerId(Long organizationId, Long id);

    /**
     * 插入hdspFileServer，同时级联插入hdspFileServer role
     *
     * @param Server 要插入的Server
     * @return 插入后的hdspFileServer
     * @throws CommonException 插入出错
     */
    Server insertServer(Server Server);

    /**
     * 测试文件服务器
     *
     * @param Server 文件服务器查询条件
     * @return boolean
     */
    boolean checkServer(Server Server);

    /**
     * 更新Server，会级联更新roles，当roles不为null时
     *
     * @param fileServerUDTO Server
     * @return 更新后的hdspFileServer
     * @throws CommonException 更新失败
     */
    Server updateServer(Server Server);

    /**
     * 删除Server
     *
     */
    int deleteServer(Server server);

    List<Server> fetchCanAssignFileServers( Long tenantId,Long clusterId,Server server, PageRequest pageRequest);

    List<Server> selectAllWithClusterInfo(Server Server, PageRequest pageRequest);
    
    List<Server> selectByServerIdList(List<Long> serverIds);
    
    List<Server> selectByClusterIdList(List<Long> clusterIds);

}
