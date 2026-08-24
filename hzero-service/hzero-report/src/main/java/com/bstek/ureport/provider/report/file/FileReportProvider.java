package com.bstek.ureport.provider.report.file;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import com.bstek.ureport.exception.ReportException;
import com.bstek.ureport.provider.report.ReportFile;
import com.bstek.ureport.provider.report.ReportProvider;
import org.apache.commons.io.IOUtils;
import org.hzero.boot.file.FileClient;
import org.hzero.common.HZeroService;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.report.domain.entity.Report;
import org.hzero.report.domain.entity.UreportFile;
import org.hzero.report.domain.repository.ReportRepository;
import org.hzero.report.domain.repository.UreportFileRepository;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * ureport 文件存储
 *
 * @author shuangfei.zhu@hand-china.com 2020/08/03 15:16
 */
public class FileReportProvider implements ReportProvider {

    private volatile static ReportRepository reportRepository;
    private volatile static UreportFileRepository fileRepository;
    private volatile static FileClient fileClient;

    private ReportRepository getReportRepository() {
        if (null == reportRepository) {
            synchronized (FileReportProvider.class) {
                if (null == reportRepository) {
                    reportRepository = ApplicationContextHelper.getContext().getBean(ReportRepository.class);
                }
            }
        }
        return reportRepository;
    }

    private UreportFileRepository getFileRepository() {
        if (null == fileRepository) {
            synchronized (FileReportProvider.class) {
                if (null == fileRepository) {
                    fileRepository = ApplicationContextHelper.getContext().getBean(UreportFileRepository.class);
                }
            }
        }
        return fileRepository;
    }

    private FileClient getFileClient() {
        if (null == fileClient) {
            synchronized (FileReportProvider.class) {
                if (null == fileClient) {
                    fileClient = ApplicationContextHelper.getContext().getBean(FileClient.class);
                }
            }
        }
        return fileClient;
    }

    public static final String PREFIX = "hzero:";
    private static final String DIRECTORY = "hrpt03";
    private String fileStoreDir;
    private boolean disabled;

    @Override
    public InputStream loadReport(String filename) {
        if (filename.startsWith(PREFIX)) {
            filename = filename.substring(PREFIX.length());
        }
        Long tenantId = currentTenantId();
        UreportFile param = new UreportFile().setFileName(filename).setTenantId(tenantId);
        UreportFile report = getFileRepository().selectOne(param);
        if (report == null) {
            throw new ReportException(MessageAccessor.getMessage(BaseConstants.ErrorCode.DATA_NOT_EXISTS).desc());
        }
        try {
            return getFileClient().downloadFile(report.getTenantId(), HZeroService.Report.BUCKET_NAME, report.getFileUrl());
        } catch (CommonException e) {
            throw new ReportException(MessageAccessor.getMessage(e.getCode()).desc());
        }
    }

    private Long currentTenantId() {
        return BaseConstants.DEFAULT_TENANT_ID;
    }

    @Override
    public void deleteReport(String filename) {
        if (filename.startsWith(PREFIX)) {
            filename = filename.substring(PREFIX.length());
        }
        Long tenantId = currentTenantId();
        UreportFile param = new UreportFile().setFileName(filename).setTenantId(tenantId);
        UreportFile report = getFileRepository().selectOne(param);
        if (report != null) {
            try {
                getFileClient().deleteFileByUrl(report.getTenantId(), HZeroService.Report.BUCKET_NAME, Collections.singletonList(report.getFileUrl()));
            } finally {
                getFileRepository().deleteByPrimaryKey(report);
            }
        }
    }

    @Override
    public List<ReportFile> getReportFiles() {
        // 禁止查看报表列表
//        Long tenantId = currentTenantId();
//        List<UreportFile> fileList = getFileRepository().select(new UreportFile().setTenantId(tenantId));
//        if (CollectionUtils.isEmpty(fileList)) {
//            return Collections.emptyList();
//        }
//        List<ReportFile> list = new ArrayList<>();
//        fileList.forEach(item -> list.add(new ReportFile(item.getFileName(), item.getLastUpdateDate())));
//        list.sort((f1, f2) -> f2.getUpdateDate().compareTo(f1.getUpdateDate()));
//        return list;
        return Collections.emptyList();
    }

    @Override
    public String getName() {
        return "系统存储";
    }

    @Override
    public void saveReport(String filename, String content) {
        if (filename.startsWith(PREFIX)) {
            filename = filename.substring(PREFIX.length());
        }
        Long tenantId = currentTenantId();
        // 判断对应报表定义是否存在
        int count = getReportRepository().selectCount(new Report().setReportCode(filename).setTenantId(tenantId));
        if (count <= 0) {
            throw new ReportException("未找到关联的报表定义");
        }
        UreportFile param = new UreportFile().setFileName(filename).setTenantId(tenantId);
        UreportFile report = getFileRepository().selectOne(param);
        if (report == null) {
            // 新建
            String url;
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                IOUtils.write(content, outStream, StandardCharsets.UTF_8);
                url = getFileClient().uploadFile(tenantId, HZeroService.Report.BUCKET_NAME, DIRECTORY, filename + ".xml", outStream.toByteArray());
            } catch (Exception ex) {
                throw new ReportException(ex);
            }
            getFileRepository().insertSelective(new UreportFile().setTenantId(tenantId).setFileName(filename).setFileUrl(url));
        } else {
            // 更新
            String url;
            try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()) {
                IOUtils.write(content, outStream, StandardCharsets.UTF_8);
                url = getFileClient().updateFile(tenantId, HZeroService.Report.BUCKET_NAME, report.getFileUrl(), outStream.toByteArray());
            } catch (Exception ex) {
                throw new ReportException(ex);
            }
            getFileRepository().updateByPrimaryKey(report.setFileUrl(url));
        }
    }

    @Override
    public boolean disabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setFileStoreDir(String fileStoreDir) {
        this.fileStoreDir = fileStoreDir;
    }

    @Override
    public String getPrefix() {
        return PREFIX;
    }
}
