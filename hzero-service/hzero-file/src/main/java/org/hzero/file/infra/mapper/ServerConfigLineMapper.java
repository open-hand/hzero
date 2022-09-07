package org.hzero.file.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.file.domain.entity.ServerConfigLine;
import org.hzero.file.domain.vo.ServerVO;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 服务器上传配置明细Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2019-07-04 10:10:21
 */
public interface ServerConfigLineMapper extends BaseMapper<ServerConfigLine> {

    /**
     * 查询明细集合(服务器)
     *
     * @param configId 上传配置Id
     * @return 明细
     */
    List<ServerConfigLine> listConfigLineWithServer(@Param("configId") Long configId);

    /**
     * 查询明细(服务器)
     *
     * @param configLineId 上传配置明细Id
     * @return 明细
     */
    ServerConfigLine getConfigLineWithServer(@Param("configLineId") Long configLineId);

    /**
     * 查询明细集合(集群)
     *
     * @param configId 上传配置Id
     * @return 明细
     */
    List<ServerConfigLine> listConfigLineWithCluster(@Param("configId") Long configId);

    /**
     * 查询明细(集群)
     *
     * @param configLineId 上传配置明细Id
     * @return 明细
     */
    ServerConfigLine getConfigLineWithCluster(@Param("configLineId") Long configLineId);

    /**
     * 查询服务器信息
     *
     * @param serverCode 服务器编码
     * @param tenantId   租户
     * @return 服务器信息
     */
    ServerVO getServer(@Param("serverCode") String serverCode,
                       @Param("tenantId") Long tenantId);
}
