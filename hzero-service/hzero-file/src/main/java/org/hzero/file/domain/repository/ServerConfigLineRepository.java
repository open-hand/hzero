package org.hzero.file.domain.repository;

import java.util.List;

import org.hzero.file.domain.entity.ServerConfigLine;
import org.hzero.file.domain.vo.ServerVO;
import org.hzero.mybatis.base.BaseRepository;

/**
 * 服务器上传配置明细资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-04 10:10:21
 */
public interface ServerConfigLineRepository extends BaseRepository<ServerConfigLine> {

    /**
     * 查询明细集合(服务器)
     *
     * @param configId 上传配置Id
     * @return 明细
     */
    List<ServerConfigLine> listConfigLineWithServer(Long configId);

    /**
     * 查询明细(服务器)
     *
     * @param configLineId 上传配置明细Id
     * @return 明细
     */
    ServerConfigLine getConfigLineWithServer(Long configLineId);

    /**
     * 查询明细集合(集群)
     *
     * @param configId 上传配置Id
     * @return 明细
     */
    List<ServerConfigLine> listConfigLineWithCluster(Long configId);

    /**
     * 查询明细(集群)
     *
     * @param configLineId 上传配置明细Id
     * @return 明细
     */
    ServerConfigLine getConfigLineWithCluster(Long configLineId);

    /**
     * 查询服务器信息
     *
     * @param serverCode 服务器编码
     * @param tenantId   租户
     * @return 服务器信息
     */
    ServerVO getServer(String serverCode, Long tenantId);
}
