package org.hzero.file.domain.repository;

import java.util.Date;

import org.hzero.file.domain.entity.FileEditLog;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 文件编辑日志资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-28 11:36:32
 */
public interface FileEditLogRepository extends BaseRepository<FileEditLog> {

    /**
     * 分页查询
     *
     * @param pageRequest    分页
     * @param tenantId       租户Id
     * @param fileName       文件名
     * @param realName       提交人
     * @param editType       编辑类型
     * @param changeDateFrom 编辑时间从
     * @param changeDateTo   编辑时间至
     * @return 查询结果
     */
    Page<FileEditLog> pageEditLog(PageRequest pageRequest, Long tenantId, String fileName, String realName, String editType, Date changeDateFrom, Date changeDateTo);
}
