package org.hzero.platform.app.service.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.platform.encrypt.EncryptClient;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.hzero.platform.app.service.ServerService;
import org.hzero.platform.domain.entity.Server;
import org.hzero.platform.domain.entity.ServerAssign;
import org.hzero.platform.domain.repository.ServerAssignRepository;
import org.hzero.platform.domain.repository.ServerRepository;
import org.hzero.platform.infra.constant.HpfmMsgCodeConstants;
import org.hzero.platform.infra.util.SftpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.jcraft.jsch.JSchException;
import org.springframework.util.CollectionUtils;

import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
/**
 * 文件服务器应用服务默认实现
 *
 * @author minghui.qiu@hand-china.com 2019-07-02 14:33:33
 */
@Service
public class ServerServiceImpl implements ServerService {
    @Autowired
    private ServerRepository serverRepository;
    @Autowired
    private ServerAssignRepository serverAssignRepository;
    @Autowired
    private EncryptClient encryptClient;

    @Override
    public Page<Server> findServers(Server server, PageRequest pageRequest) {
        return  PageHelper.doPageAndSort(pageRequest, () -> serverRepository.selectServerList(server));
    }


    @Override
    public Server findServerByServerId(Long organizationId, Long id) {
        Server dto = new Server();
        dto.setServerId(id);
        dto.setTenantId(organizationId);
        List<Server> servers = serverRepository.selectServerList(dto);
        Assert.notNull(servers, String.format("server not exists with tenantId=%s and serverId=%s", organizationId, id));
        return servers.get(0);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Server insertServer(Server dto) {
        Server serverCodeCriteria = new Server().setServerCode(dto.getServerCode());
        if (serverRepository.selectCount(serverCodeCriteria) > 0) {
            throw new DuplicateKeyException("hpfm.error.server.code.exists");
        }
        
        if (StringUtils.isNotBlank(dto.getLoginEncPwd())) {
        	String password = encryptClient.decrypt(dto.getLoginEncPwd());
        	dto.setLoginEncPwd(password);
            DataSecurityHelper.open();
            //dto.setLoginEncPwd(stringEncryptor.encrypt(dto.getLoginPassword()));
        }
        serverRepository.insertSelective(dto);
        return dto;
    }

    @Override
    public boolean checkServer(Server dto) {
        DataSecurityHelper.open();
        Server server = serverRepository.selectOne(dto);
        try (SftpUtil util = new SftpUtil()) {
            DataSecurityHelper.open();
            //dto.setLoginEncPwd(dto.getLoginPassword());
            //String decryptPassword = stringEncryptor.decrypt(server.getLoginEncPwd());
            util.connectServer(server.getIp(), server.getPort(), server.getLoginUser(), server.getLoginEncPwd());
        } catch (JSchException e) {
            throw new CommonException("hpfm.error.server.check.fail");
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Server updateServer(Server dto) {
        //Server server = new Server();
        if (StringUtils.isNotBlank(dto.getLoginEncPwd())) {
        	String password = encryptClient.decrypt(dto.getLoginEncPwd());
        	dto.setLoginEncPwd(password);
            DataSecurityHelper.open();
            //dto.setLoginEncPwd(stringEncryptor.encrypt(dto.getLoginPassword()));
        }
        try {
            if (serverRepository.updateByPrimaryKeySelective(dto) > 0) {
                Server server = serverRepository.selectByPrimaryKey(dto.getServerId());
                return server;
            }
            return null;
        } catch (DuplicateKeyException e) {
            //log.error("Insert DuplicateKeyException Occurred!!", e);
            throw new CommonException(e.getCause().getLocalizedMessage());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteServer(Server server) {
        // 校验服务是否已经应用在集群服务器配置下，若应用则删除集群与服务器的关联关系
        ServerAssign serverAssign = new ServerAssign();
        serverAssign.setServerId(server.getServerId());
        List<ServerAssign> serverAssigns = serverAssignRepository.select(serverAssign);
        // 删除服务器定义
        int deleteFlag = serverRepository.deleteByPrimaryKey(server.getServerId());
        if (!CollectionUtils.isEmpty(serverAssigns)) {
            deleteFlag = serverAssignRepository.batchDeleteByPrimaryKey(serverAssigns);
        }
        return deleteFlag;
    }

    @Override
    public List<Server> fetchCanAssignFileServers(Long tenantId,Long clusterId,Server server, PageRequest pageRequest) {
        return PageHelper.doPageAndSort(pageRequest, () -> serverRepository.fetchCanAssignFileServers(tenantId,clusterId,server));
    }

    @Override
    public List<Server> selectAllWithClusterInfo(Server Server, PageRequest pageRequest){
        return PageHelper.doPageAndSort(pageRequest, () -> serverRepository.selectAllWithClusterInfo(Server));
    }


    @Override
    public List<Server> selectByServerIdList(List<Long> serverIds) {
        return serverRepository.selectByServerIdList(serverIds);
    }


    @Override
    public List<Server> selectByClusterIdList(List<Long> clusterIds) {
        return serverRepository.selectByClusterIdList(clusterIds);
    }

}
