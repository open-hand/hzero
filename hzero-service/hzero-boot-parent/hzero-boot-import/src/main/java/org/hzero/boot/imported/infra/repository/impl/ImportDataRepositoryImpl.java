package org.hzero.boot.imported.infra.repository.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.repository.ImportDataRepository;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.mapper.ImportDataMapper;
import org.hzero.boot.platform.lov.annotation.ProcessLovValue;
import org.hzero.mybatis.base.impl.BaseRepositoryImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.PageHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;


/**
 * 数据导入RepositoryImpl
 *
 * @author : fan.wang03@hand-china.com
 */
@Component
public class ImportDataRepositoryImpl extends BaseRepositoryImpl<ImportData> implements ImportDataRepository {

    @Autowired
    private ImportDataMapper importDataMapper;

    @Override
    @ProcessLovValue
    public Page<ImportData> pageData(String templateCode, String batch, Integer sheetIndex, DataStatus status, PageRequest pageRequest) {
        Page<ImportData> page = PageHelper.doPageAndSort(pageRequest, () -> importDataMapper.selectByBatch(new ImportData()
                .setTemplateCode(templateCode).setBatch(batch).setDataStatus(status).setSheetIndex(sheetIndex)));
        List<ImportData> list = page.getContent();
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                if (StringUtils.isBlank(item.getErrorMsg())) {
                    item.setInfo(item.getBackInfo());
                } else {
                    item.setInfo(item.getErrorMsg());
                }
            });
            page.setContent(list);
        }
        return page;
    }

    @Override
    public List<ImportData> listData(String templateCode, String batch, Integer sheetIndex, DataStatus status) {
        List<ImportData> list = importDataMapper.selectByBatch(new ImportData().setTemplateCode(templateCode)
                .setBatch(batch).setDataStatus(status).setSheetIndex(sheetIndex));
        if (CollectionUtils.isNotEmpty(list)) {
            list.forEach(item -> {
                if (StringUtils.isBlank(item.getErrorMsg())) {
                    item.setInfo(item.getBackInfo());
                } else {
                    item.setInfo(item.getErrorMsg());
                }
            });
        }
        return list;
    }

    @Override
    public void deleteByBatch(String batch) {
        importDataMapper.deleteByBatch(batch);
    }

    @Override
    public void updateFailed(String templateCode, String batch, Integer sheetIndex, DataStatus status, String desc) {
        importDataMapper.updateFailed(templateCode, batch, sheetIndex, status, desc);
    }

    @Override
    public void fallback(Long id, DataStatus status) {
        importDataMapper.fallback(id, status);
    }

    @Override
    public void update(ImportData data) {
        updateOptional(data, ImportData.FIELD_DATA_STATUS, ImportData.FIELD_ERROR_MSG, ImportData.FIELD_BACK_INFO);
    }
}