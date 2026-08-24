package org.hzero.file.infra.repository.impl;

import java.util.Date;

import org.hzero.file.domain.entity.FileEditLog;
import org.hzero.file.domain.repository.FileEditLogRepository;
import org.hzero.file.infra.mapper.FileEditLogMapper;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 文件编辑日志 资源库实现
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-28 11:36:32
 */
@Component
public class FileEditLogRepositoryImpl extends BaseRepositoryImpl<FileEditLog> implements FileEditLogRepository {

    @Autowired
    private FileEditLogMapper fileEditLogMapper;

    @Override
    public Page<FileEditLog> pageEditLog(PageRequest pageRequest, Long tenantId, String fileName, String realName, String editType, Date changeDateFrom, Date changeDateTo) {
        return PageHelper.doPageAndSort(pageRequest, () ->
                fileEditLogMapper.listFileEditLog(tenantId, fileName, realName, editType, changeDateFrom, changeDateTo));
    }
}
