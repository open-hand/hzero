package org.hzero.file.app.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.codec.Charsets;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.FilenameUtils;
import org.hzero.core.util.ResponseUtils;
import org.hzero.core.util.UUIDUtils;
import org.hzero.file.app.service.CapacityUsedService;
import org.hzero.file.app.service.ServerFileService;
import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.entity.ServerConfig;
import org.hzero.file.domain.entity.ServerConfigLine;
import org.hzero.file.domain.repository.FileRepository;
import org.hzero.file.domain.repository.ServerConfigLineRepository;
import org.hzero.file.domain.repository.ServerConfigRepository;
import org.hzero.file.domain.vo.ServerVO;
import org.hzero.file.infra.constant.FileServiceType;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.file.infra.feign.PlatformRemoteService;
import org.hzero.file.infra.util.FtpClient;
import org.hzero.file.infra.util.SftpClient;
import org.hzero.mybatis.common.Criteria;
import org.hzero.mybatis.helper.DataSecurityHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/05 9:48
 */
@Service
public class ServerFileServiceImpl implements ServerFileService {

    private static final Logger logger = LoggerFactory.getLogger(ServerFileServiceImpl.class);

    private final ServerConfigRepository serverConfigRepository;
    private final ServerConfigLineRepository serverConfigLineRepository;
    private final PlatformRemoteService platformRemoteService;
    private final FileRepository fileRepository;
    private final CapacityUsedService residualCapacityService;

    @Autowired
    public ServerFileServiceImpl(ServerConfigRepository serverConfigRepository,
                                 ServerConfigLineRepository serverConfigLineRepository,
                                 PlatformRemoteService platformRemoteService,
                                 FileRepository fileRepository,
                                 CapacityUsedService residualCapacityService) {
        this.serverConfigRepository = serverConfigRepository;
        this.serverConfigLineRepository = serverConfigLineRepository;
        this.platformRemoteService = platformRemoteService;
        this.fileRepository = fileRepository;
        this.residualCapacityService = residualCapacityService;
    }

    @Override
    public List<File> listFile(Long tenantId, String uuid) {
        return fileRepository.selectOptional(
                new File().setAttachmentUuid(uuid).setTenantId(tenantId).setSourceType(String.valueOf(FileServiceType.SERVER.getValue())),
                new Criteria().select(
                        File.FIELD_ATTACHMENT_UUID,
                        File.FIELD_FILE_URL,
                        File.FIELD_FILE_NAME,
                        File.FIELD_SERVER_CODE));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String uploadFile(Long tenantId, String configCode, String path, String fileName, Integer cover, MultipartFile multipartFile) {
        if (StringUtils.isBlank(fileName)) {
            fileName = multipartFile.getOriginalFilename();
        }
        if (StringUtils.isBlank(path)) {
            path = StringUtils.EMPTY;
        }
        try (InputStream inputStream = multipartFile.getInputStream()) {
            List<ServerVO> serverList = getServerList(tenantId, configCode);
            String uuid = UUIDUtils.generateTenantUUID(tenantId);
            for (ServerVO item : serverList) {
                if (Objects.equals(item.getEnabledFlag(), BaseConstants.Flag.YES)) {
                    Assert.notNull(item.getRootDir(), BaseConstants.ErrorCode.DATA_NOT_EXISTS);
                    // 根据根路径判断服务器类型
                    String separator = HfleConstant.LINUX_SEPARATOR;
                    String rootDir = item.getRootDir();
                    if (!rootDir.contains(HfleConstant.LINUX_SEPARATOR) && rootDir.contains(HfleConstant.WINDOWS_SEPARATOR)) {
                        separator = HfleConstant.WINDOWS_SEPARATOR;
                    }
                    String filePath = rootDir + path;
                    // 记录文件
                    File file = new File().setAttachmentUuid(uuid)
                            .setFileSize(multipartFile.getSize())
                            .setFileUrl(filePath + separator + fileName)
                            .setTenantId(tenantId)
                            .setBucketName(HfleConstant.DEFAULT_ATTACHMENT_UUID)
                            .setFileName(fileName)
                            .setFileType(multipartFile.getContentType())
                            .setFileKey(filePath + separator + fileName)
                            .setServerCode(item.getServerCode())
                            .setSourceType(String.valueOf(FileServiceType.SERVER.getValue()));
                    fileRepository.insertSelective(file);
                    upload(filePath, fileName, inputStream, cover, item);
                    // 修改租户已使用存储容量
                    residualCapacityService.refreshCache(tenantId, multipartFile.getSize());
                }
            }
            return uuid;
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    private void upload(String filePath, String fileName, InputStream inputStream, Integer cover, ServerVO server) {
        String password = server.getLoginEncPwd();
        String protocol = server.getProtocolCode();
        switch (protocol) {
            case HfleConstant.Protocol.FTP:
                FtpClient ftpClient = new FtpClient(server.getIp(), server.getPort(), server.getLoginUser(), password);
                ftpClient.uploadFile(filePath, fileName, cover, inputStream);
                break;
            case HfleConstant.Protocol.SFTP:
                SftpClient sftpClient = new SftpClient(server.getIp(), server.getPort(), server.getLoginUser(), password);
                sftpClient.uploadFile(filePath, fileName, cover, inputStream);
                break;
            case HfleConstant.Protocol.CPT:
                // todo 待添加支持
            default:
                throw new CommonException(HfleMessageConstant.UNSUPPORTED_PROTOCOL, protocol);
        }
    }

    @Override
    public void updateFile(Long tenantId, String serverConfigCode, String path, MultipartFile file) {

    }

    @Override
    public void downloadFile(Long tenantId, String serverCode, String url, HttpServletRequest request, HttpServletResponse response) {
        ServerVO server = serverConfigLineRepository.getServer(serverCode, tenantId);
        if (server == null || !Objects.equals(server.getEnabledFlag(), BaseConstants.Flag.YES)) {
            throw new CommonException(BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        }
        String password;
        String separator = HfleConstant.LINUX_SEPARATOR;
        if (!url.contains(HfleConstant.LINUX_SEPARATOR) && url.contains(HfleConstant.WINDOWS_SEPARATOR)) {
            separator = HfleConstant.WINDOWS_SEPARATOR;
        }
        String[] str = url.split(separator);
        String fileName = str[str.length - 1];
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length - 1; i++) {
            if (StringUtils.isNotBlank(str[i])) {
                sb.append(separator).append(str[i]);
            }
        }
        byte[] data = null;
        try {
            // 解密密码
            password = DataSecurityHelper.decrypt(server.getLoginEncPwd());
        } catch (Exception e) {
            password = server.getLoginEncPwd();
            logger.warn("Password decrypt failed!");
        }
        try {
            switch (server.getProtocolCode()) {
                case HfleConstant.Protocol.FTP:
                    FtpClient ftpClient = new FtpClient(server.getIp(), server.getPort(), server.getLoginUser(), password);
                    data = ftpClient.downloadFile(String.valueOf(sb), fileName);
                    break;
                case HfleConstant.Protocol.SFTP:
                    SftpClient sftpClient = new SftpClient(server.getIp(), server.getPort(), server.getLoginUser(), password);
                    data = sftpClient.downloadFile(String.valueOf(sb), fileName);
                    break;
                case HfleConstant.Protocol.CPT:
                    // todo 待添加支持
                default:
                    break;
            }
            if (data != null) {
                // 构建response
                response.reset();
                response.setHeader("Content-Disposition", "attachment;filename=" + FilenameUtils.encodeFileName(request, fileName));
                response.setContentType("multipart/form-data");
                response.addHeader("Content-Length", "" + data.length);
                response.setHeader("Pragma", "public");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
                response.setDateHeader("Expires", (System.currentTimeMillis() + 1000));

                IOUtils.write(data, response.getOutputStream());
            }
        } catch (Exception e) {
            logger.error("Error downloadFile.", e);
            writeException(response, HfleMessageConstant.FTP_DOWNLOAD);
        }
    }

    @SuppressWarnings("all")
    private void writeException(HttpServletResponse response, String exceptionCode) {
        try {
            response.setStatus(HttpStatus.OK.value());
            response.setContentType(MediaType.TEXT_HTML_VALUE);
            response.setCharacterEncoding(Charsets.UTF_8.displayName());
            response.getWriter().write(String.format(
                    "<script>const msg = { type: 'templateExportError', message: '%s' };%nwindow.parent.postMessage(msg, '*');</script>",
                    MessageAccessor.getMessage(exceptionCode).desc()));
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }

    @Override
    public void deleteFile(Long tenantId, String serverConfigCode, String path) {

    }

    @Override
    public List<ServerVO> getServerList(Long tenantId, String configCode) {
        // 查询服务器上传配置
        ServerConfig serverConfig = serverConfigRepository.selectOne(new ServerConfig().setConfigCode(configCode).setTenantId(tenantId));
        Assert.notNull(serverConfig, BaseConstants.ErrorCode.DATA_NOT_EXISTS);
        Long configId = serverConfig.getConfigId();
        String defaultRootDir = serverConfig.getRootDir();
        List<ServerConfigLine> lineList;
        // 数据源Id集合
        List<Long> idList = new ArrayList<>();
        // 数据源Id, 根目录
        Map<Long, String> rootDirMap = new HashMap<>(16);
        // 服务器集合
        List<ServerVO> serverList;
        switch (serverConfig.getSourceType()) {
            case HfleConstant.SourceType.SERVER:
                lineList = serverConfigLineRepository.listConfigLineWithServer(configId);
                // 获取服务器配置集合
                for (ServerConfigLine item : lineList) {
                    idList.add(item.getSourceId());
                    rootDirMap.put(item.getSourceId(), StringUtils.isBlank(item.getRootDir()) ? defaultRootDir : item.getRootDir());
                }
                serverList = ResponseUtils.getResponse(platformRemoteService.listByServerIds(tenantId, idList), new TypeReference<List<ServerVO>>() {
                });
                for (ServerVO item : serverList) {
                    item.setRootDir(rootDirMap.get(item.getServerId()));
                    try {
                        // 解密密码
                        item.setLoginEncPwd(DataSecurityHelper.decrypt(item.getLoginEncPwd()));
                    } catch (Exception e) {
                        logger.warn("Password decrypt failed!");
                    }
                }
                break;
            case HfleConstant.SourceType.CLUSTER:
                lineList = serverConfigLineRepository.listConfigLineWithCluster(configId);
                // 获取服务器配置集合
                for (ServerConfigLine item : lineList) {
                    idList.add(item.getSourceId());
                    rootDirMap.put(item.getSourceId(), StringUtils.isBlank(item.getRootDir()) ? defaultRootDir : item.getRootDir());
                }
                serverList = ResponseUtils.getResponse(platformRemoteService.listByClusterIds(tenantId, idList), new TypeReference<List<ServerVO>>() {
                });
                serverList.forEach(item -> {
                    item.setRootDir(rootDirMap.get(item.getClusterId()));
                    try {
                        // 解密密码
                        item.setLoginEncPwd(DataSecurityHelper.decrypt(item.getLoginEncPwd()));
                    } catch (Exception e) {
                        logger.warn("Password decrypt failed!");
                    }
                });
                break;
            default:
                throw new CommonException(HfleMessageConstant.SOURCE_TYPE);
        }
        return serverList;
    }
}
