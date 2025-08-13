package org.hzero.boot.imported.domain.repository;

import java.util.Date;

import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 资源库
 *
 * @author shuangfei.zhu@hand-china.com 2018-12-19 14:57:57
 */
public interface ImportRepository extends BaseRepository<Import> {

    /**
     * 分页查询
     *
     * @param tenantId         租户
     * @param templateCode     模板编码
     * @param creationDateFrom 创建时间从
     * @param creationDateTo   创建时间至
     * @param pageRequest      分页
     * @return 查询结果
     */
    Page<Import> pageImportHistory(Long tenantId, String templateCode, Date creationDateFrom, Date creationDateTo, PageRequest pageRequest);

    /**
     * 查询状态
     *
     * @param batch 批次号
     * @return 状态
     */
    Import getStatus(String batch);
}
