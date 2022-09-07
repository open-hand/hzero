package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.api.dto.DomainDTO;
import org.hzero.iam.domain.entity.Domain;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 二级域名分配Mapper
 *
 * @author minghui.qiu@hand-china.com 2019-06-27 20:50:16
 */
public interface DomainMapper extends BaseMapper<Domain> {
    
    /**
     * 根据参数查询单据类型定义
     * 
     * @param  查询参数
     * @return 单据数据列表
     */
    List<Domain> selectByOptions(Domain domain);
    
    Domain selectByDomainId(@Param(value="domainId") Long domainId);

    /**
     * 获取所有域名需缓存的值
     *
     * @return 查询结果
     */
    List<DomainDTO> getAllDomain();
}
