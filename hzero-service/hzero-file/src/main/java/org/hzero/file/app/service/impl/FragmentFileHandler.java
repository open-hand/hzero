package org.hzero.file.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.UUIDUtils;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.FileService;
import org.hzero.file.app.service.ServerFileService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.vo.ServerVO;
import org.hzero.file.infra.constant.FileServiceType;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.util.FtpClient;
import org.hzero.file.infra.util.SftpClient;
import org.hzero.fragment.service.FileHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/30 19:33
 */
@Component
public class FragmentFileHandler implements FileHandler {

    private static final Logger logger = LoggerFactory.getLogger(FragmentFileHandler.class);

    private static final String CONFIG_CODE = "configCode";
    private static final String PATH = "path";

    private static final String BUCKET_NAME = "bucketName";
    private static final String DIRECTORY = "directory";
    private static final String STORAGE_CODE = "storageCode";

    private final FileService fileService;
    private final ServerFileService serverFileService;
    private final FileRepository fileRepository;
    private final CapacityUsedService residualCapacityService;

    @Autowired
    public FragmentFileHandler(FileService fileService,
                               ServerFileService serverFileService,
                               FileRepository fileRepository,
                               CapacityUsedService residualCapacityService) {
        this.fileService = fileService;
        this.serverFileService = serverFileService;
        this.fileRepository = fileRepository;
        this.residualCapacityService = residualCapacityService;
    }

    @Override
    public String process(Long tenantId, String filename, String filePath, InputStream inputStream, Map<String, String> params) {
        if (params.containsKey(CONFIG_CODE)) {
            // 服务器文件上传
            try {
                // 配置编码
                String configCode = params.get(CONFIG_CODE);
                // 服务器文件存储路径
                String path = params.getOrDefault(PATH, StringUtils.EMPTY);
                String contentType = Files.probeContentType(Paths.get(filePath));
                List<ServerVO> serverList = serverFileService.getServerList(tenantId, configCode);
                String uuid = UUIDUtils.generateTenantUUID(tenantId);
                Long size = (long) inputStream.available();
                for (ServerVO item : serverList) {
                    if (Objects.equals(item.getEnabledFlag(), BaseConstants.Flag.YES)) {
                        String rootDir = item.getRootDir();
                        Assert.notNull(rootDir, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
                        // 根据根路径判断服务器类型
                        String separator = HfleConstant.LINUX_SEPARATOR;
                        if (!rootDir.contains(HfleConstant.LINUX_SEPARATOR) && rootDir.contains(HfleConstant.WINDOWS_SEPARATOR)) {
                            separator = HfleConstant.WINDOWS_SEPARATOR;
                        }
                        String realPath = rootDir + path;
                        // 记录文件
                        File file = new File().setAttachmentUuid(uuid)
                                .setFileSize(size)
                                .setFileUrl(realPath + separator + filename)
                                .setTenantId(tenantId)
                                .setBucketName(HfleConstant.DEFAULT_ATTACHMENT_UUID)
                                .setFileName(filename)
                                .setFileType(StringUtils.isBlank(contentType) ? HfleConstant.DEFAULT_MULTI_TYPE : contentType)
                                .setFileKey(realPath + separator + filename)
                                .setServerCode(item.getServerCode())
                                .setSourceType(String.valueOf(FileServiceType.SERVER.getValue()));
                        fileRepository.insertSelective(file);
                        uploadLocalFile(filePath, realPath, filename, item);
                        // 修改租户已使用存储容量
                        residualCapacityService.refreshCache(tenantId, size);
                    }
                }
                return uuid;
            } catch (Exception e) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("InputStream close failed");
                }
            }
        }
        if (params.containsKey(BUCKET_NAME)) {
            String bucketName = params.get(BUCKET_NAME);
            String directory = params.getOrDefault(DIRECTORY, null);
            String storageCode = params.getOrDefault(STORAGE_CODE, null);
            // 对象存储上传
            try {
                return fileService.uploadFragmentFile(tenantId, bucketName, directory, filename, storageCode, filePath, (long) inputStream.available());
            } catch (IOException e) {
                throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE, e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.error("InputStream close failed");
                }
            }
        }
        throw new CommonException(HfleMessageConstant.ERROR_FILE_UPDATE);
    }

    private void uploadLocalFile(String local, String remotePath, String filename, ServerVO server) {
        String password = server.getLoginEncPwd();
        switch (server.getProtocolCode()) {
            case HfleConstant.Protocol.FTP:
                FtpClient ftpClient = new FtpClient(server.getIp(), server.getPort(), server.getLoginUser(), password);
                ftpClient.upload(local, remotePath, filename);
                break;
            case HfleConstant.Protocol.SFTP:
                SftpClient sftpClient = new SftpClient(server.getIp(), server.getPort(), server.getLoginUser(), password);
                sftpClient.upload(local, remotePath, filename);
                break;
            case HfleConstant.Protocol.CPT:
                // todo 待添加支持
            default:
                break;
        }
    }
}
