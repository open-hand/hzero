package org.hzero.boot.imported.infra.mapper;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.boot.imported.domain.entity.Import;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * Mapper
 *
 * @author shuangfei.zhu@hand-china.com 2018-12-19 14:57:57
 */
public interface ImportMapper extends BaseMapper<Import> {

    /**
     * 查询导入记录
     *
     * @param tenantId         租户
     * @param templateCode     模板编码
     * @param creationDateFrom 创建时间从
     * @param creationDateTo   创建时间至
     * @return 导入记录
     */
    List<Import> listImport(@Param("tenantId") Long tenantId,
                            @Param("templateCode") String templateCode,
                            @Param("creationDateFrom") Date creationDateFrom,
                            @Param("creationDateTo") Date creationDateTo);
}
