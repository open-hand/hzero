package org.hzero.boot.imported.infra.execute;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.sql.DataSource;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.imported.app.service.*;
import org.hzero.boot.imported.config.ImportConfig;
import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.boot.imported.domain.entity.TemplatePage;
import org.hzero.boot.imported.domain.repository.ImportDataRepository;
import org.hzero.boot.imported.domain.repository.ImportRepository;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.boot.imported.infra.redis.AmountRedis;
import org.hzero.boot.imported.infra.registry.ImportRegistry;
import org.hzero.boot.imported.infra.util.StepUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.domain.Page;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 导入数据到正式表执行器
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/14 9:12
 */
public class DataImportExecute implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DataImportExecute.class);

    private final Template template;
    private final String batch;
    private final Import imported;
    private final ImportDataRepository dataRepository;
    private final ImportRepository importRepository;
    private final ImportConfig importConfig;
    private final Authentication authentication;
    private final Map<String, Object> args;
    private final DataSourceTransactionManager transactionManager;
    private final List<TransactionStatus> transactionList = new ArrayList<>();
    private Integer stepSize;
    private Integer readyCount = 0;
    /**
     * 出现异常的数据
     */
    private final List<ImportData> errorDataList = new ArrayList<>();

    public DataImportExecute(Template template,
                             Import imported,
                             ImportConfig importConfig,
                             ImportRepository importRepository,
                             ImportDataRepository dataRepository,
                             Authentication authentication,
                             Map<String, Object> args) {
        Assert.notNull(template, HimpBootConstants.ErrorCode.LOCAL_TEMPLATE_NOT_EXISTS);
        Assert.notEmpty(template.getTemplatePageList(), HimpBootConstants.ErrorCode.TEMPLATE_PAGE_NOT_EXISTS);
        this.template = template;
        this.batch = imported.getBatch();
        this.imported = imported;
        this.importConfig = importConfig;
        this.importRepository = importRepository;
        this.dataRepository = dataRepository;
        this.authentication = authentication;
        this.args = args;
        this.transactionManager = ApplicationContextHelper.getContext().getBean(DataSourceTransactionManager.class);
        if (transactionManager.getDataSource() == null) {
            this.transactionManager.setDataSource((DataSource) ApplicationContextHelper.getContext().getBean("dataSource"));
        }
    }

    @Override
    public void run() {
        try {
            if (SecurityContextHolder.getContext() != null) {
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
            // 记录数据总数
            int count = dataRepository.selectCount(new ImportData().setBatch(batch));
            AmountRedis.refreshCount(null, batch, count);
            // 进度刷新步长
            stepSize = StepUtils.getStepSize(count);
            long start = System.nanoTime();
            logger.debug("<<<<<<<<<<<<<<<     batch {} start to import , time consuming : {}    >>>>>>>>>>>>>>>", batch, start);
            template.getTemplatePageList().forEach(this::importData);
            logger.debug("<<<<<<<<<<<<<<<     batch {} import success , time consuming : {}     >>>>>>>>>>>>>>>>", batch, System.nanoTime() - start);
            if (CollectionUtils.isEmpty(errorDataList)) {
                // 提交事务
                transactionList.forEach(transactionManager::commit);
            } else {
                // 回滚事务
                transactionList.forEach(transactionManager::rollback);
                // 数据状态变更为导入失败
                PageRequest pageRequest = new PageRequest(0, importConfig.getBatchSize());
                Page<ImportData> dataList;
                do {
                    dataList = dataRepository.pageData(template.getTemplateCode(), batch, null, DataStatus.VALID_SUCCESS, pageRequest);
                    if (dataList.isEmpty()) {
                        break;
                    }
                    dataList.forEach(item -> dataRepository.fallback(item.getId(), DataStatus.IMPORT_FAILED));
                } while (!dataList.isEmpty());
                // 写入错误信息
                errorDataList.forEach(dataRepository::update);
            }
        } catch (Exception e) {
            logger.error("<<<<<<<<<<<<<<<     batch {} import failed     >>>>>>>>>>>>>>>", batch);
            logger.error("import failed", e);
        } finally {
            // 更新状态
            importRepository.updateOptional(imported.setStatus(HimpBootConstants.ImportStatus.IMPORTED), Import.FIELD_STATUS);
            // 清除进度缓存
            AmountRedis.clear(null, batch);
        }
    }

    private void importData(TemplatePage templatePage) {
        Object importService = getImportService(templatePage);
        if (importService == null) {
            errorImport(templatePage, "No import implementation found.");
        } else if (importService instanceof IDoImportService) {
            if (importService instanceof ImportHandler) {
                ImportHandler handler = (ImportHandler) importService;
                handler.setArgs(args);
                handler.setTemplate(templatePage);
            }
            singleImport(templatePage, (IDoImportService) importService);
        } else if (importService instanceof IBatchImportService) {
            if (importService instanceof BatchImportHandler) {
                BatchImportHandler handler = (BatchImportHandler) importService;
                handler.setArgs(args);
                handler.setTemplate(templatePage);
            }
            batchImport(templatePage, (IBatchImportService) importService);
        }
    }

    private void errorImport(TemplatePage templatePage, String msgCode) {
        dataRepository.updateFailed(template.getTemplateCode(), batch, templatePage.getSheetIndex(), DataStatus.IMPORT_FAILED, msgCode);
    }

    private void singleImport(TemplatePage templatePage, IDoImportService importService) {
        // 导入开始
        importService.onStart();
        // 逐条导入
        PageRequest pageRequest = new PageRequest(0, importConfig.getBatchSize());
        Page<ImportData> dataList;
        do {
            dataList = dataRepository.pageData(template.getTemplateCode(), batch, templatePage.getSheetIndex(), DataStatus.VALID_SUCCESS, pageRequest);
            if (dataList.isEmpty()) {
                break;
            }
            importData(dataList.getContent(), importService);
            // 刷新进度
            readyCount += dataList.size();
            AmountRedis.refresh(null, batch, readyCount);
        } while (!dataList.isEmpty());
        // 导入结束
        importService.onFinish();
        // 清除线程变量
        if (importService instanceof ImportHandler) {
            ((ImportHandler) importService).clear();
        }
    }

    private void batchImport(TemplatePage templatePage, IBatchImportService importService) {
        // 批量导入
        if (importService instanceof AbstractServerImportService) {
            ((AbstractServerImportService) importService).setTemplate(template).setCurrentTemplatePage(templatePage);
        }
        // 导入开始
        importService.onStart();
        // 获取批量
        int size = importService.getSize();
        if (size < BaseConstants.Digital.ONE) {
            // 不分批
            List<ImportData> dataList = dataRepository.listData(template.getTemplateCode(), batch, templatePage.getSheetIndex(), DataStatus.VALID_SUCCESS);
            batchImportData(dataList, importService);
            // 刷新就绪缓存
            AmountRedis.refresh(null, batch, dataList.size());
        } else {
            // 分批
            PageRequest pageRequest = new PageRequest(0, size);
            Page<ImportData> dataList;
            do {
                dataList = dataRepository.pageData(template.getTemplateCode(), batch, templatePage.getSheetIndex(), DataStatus.VALID_SUCCESS, pageRequest);
                batchImportData(dataList.getContent(), importService);
                // 刷新就绪缓存
                readyCount += dataList.size();
                AmountRedis.refresh(null, batch, readyCount);
            } while (!dataList.isEmpty());
        }
        // 导入结束
        importService.onFinish();
        // 清除线程变量
        if (importService instanceof BatchImportHandler) {
            ((BatchImportHandler) importService).clear();
        }
    }

    private Object getImportService(TemplatePage templatePage) {
        Object importService;
        if (HimpBootConstants.TemplateType.SERVER.equals(template.getTemplateType())) {
            importService = ImportRegistry.getDefaultServiceMap(HimpBootConstants.SERVICE_IMPORT, 0, StringUtils.EMPTY);
        } else {
            importService = ImportRegistry.getServiceMap(template.getTemplateCode(), DetailsHelper.getUserDetails().getTenantNum(), templatePage.getSheetIndex(), templatePage.getSheetName());
        }
        return importService;
    }

    /**
     * 逐条数据导入
     *
     * @param dataList         数据集合
     * @param iDoImportService 导入程序
     */
    private void importData(List<ImportData> dataList, IDoImportService iDoImportService) {
        // 记录事务
        boolean flag = importConfig.getTransactionControl();
        if (flag) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionList.add(transactionManager.getTransaction(def));
        }
        int index = -1;
        for (ImportData data : dataList) {
            index++;
            if (iDoImportService instanceof ImportHandler) {
                ((ImportHandler) iDoImportService).setContext(data);
            }
            try {
                boolean result = iDoImportService.doImport(data.getData());
                if (result) {
                    data.setDataStatus(DataStatus.IMPORT_SUCCESS).setErrorMsg(null);
                } else {
                    data.setDataStatus(DataStatus.IMPORT_FAILED);
                }
            } catch (CommonException e) {
                logger.error("exception", e);
                data.setDataStatus(DataStatus.IMPORT_FAILED).addErrorMsg(StringUtils.isNotBlank(e.getCode()) ? MessageAccessor.getMessage(e.getCode(), e.getParameters()).desc() : e.toString());
                errorDataList.add(data);
            } catch (Exception e) {
                logger.error("exception", e);
                data.setDataStatus(DataStatus.IMPORT_FAILED).addErrorMsg(e.toString());
                errorDataList.add(data);
            } finally {
                dataRepository.update(data);
                // 刷新进度
                if (index > 0 && index % stepSize == 0) {
                    AmountRedis.refreshReady(null, batch, stepSize);
                }
            }
        }
    }

    private void batchImportData(List<ImportData> dataList, IBatchImportService iBatchImportService) {
        // 记录事务
        boolean flag = importConfig.getTransactionControl();
        if (flag) {
            DefaultTransactionDefinition def = new DefaultTransactionDefinition();
            def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);
            transactionList.add(transactionManager.getTransaction(def));
        }
        try {
            if (iBatchImportService instanceof BatchImportHandler) {
                ((BatchImportHandler) iBatchImportService).setContextList(dataList);
            }
            List<String> data = dataList.stream().map(ImportData::getData).collect(Collectors.toList());
            boolean result = iBatchImportService.doImport(data);
            if (result) {
                dataList.forEach(item -> {
                    if (item.getDataStatus() == DataStatus.VALID_SUCCESS) {
                        item.setDataStatus(DataStatus.IMPORT_SUCCESS).setErrorMsg(null);
                    }
                });
            } else {
                dataList.forEach(item -> {
                    if (item.getDataStatus() == DataStatus.VALID_SUCCESS) {
                        item.setDataStatus(DataStatus.IMPORT_FAILED);
                    }
                });
            }
        } catch (CommonException e) {
            logger.error("exception", e);
            dataList.forEach(item -> errorDataList.add(item.setDataStatus(DataStatus.IMPORT_FAILED).addErrorMsg(StringUtils.isNotBlank(e.getCode()) ? MessageAccessor.getMessage(e.getCode(), e.getParameters()).desc() : e.toString())));
        } catch (Exception e) {
            logger.error("exception", e);
            dataList.forEach(item -> errorDataList.add(item.setDataStatus(DataStatus.IMPORT_FAILED).addErrorMsg(e.toString())));
        } finally {
            dataList.forEach(dataRepository::update);
        }
    }
}
