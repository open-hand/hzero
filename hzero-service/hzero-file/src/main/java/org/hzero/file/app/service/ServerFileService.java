package org.hzero.file.app.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hzero.file.domain.entity.File;
import org.hzero.file.domain.vo.ServerVO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 服务器文件应用服务
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/05 9:47
 */
public interface ServerFileService {


    /**
     * 查询服务器文件
     *
     * @param tenantId 租户
     * @param uuid     附件Id
     * @return 文件信息
     */
    List<File> listFile(Long tenantId, String uuid);

    /**
     * 上传文件到服务器
     *
     * @param tenantId      租户
     * @param configCode    服务器上传配置编码
     * @param path          文件存储路径
     * @param fileName      文件名
     * @param cover         是否覆盖已有文件
     * @param multipartFile 文件
     * @return 这批文件的uuid
     */
    String uploadFile(Long tenantId, String configCode, String path, String fileName, Integer cover, MultipartFile multipartFile);

    /**
     * 更新文件
     *
     * @param tenantId   租户
     * @param serverCode 服务器编码
     * @param url        存储路径
     * @param file       文件
     */
    void updateFile(Long tenantId, String serverCode, String url, MultipartFile file);

    /**
     * 下载文件
     *
     * @param tenantId   租户
     * @param serverCode 服务器上编码
     * @param url        文件的完整路径
     * @param request    request
     * @param response   response
     */
    void downloadFile(Long tenantId, String serverCode, String url, HttpServletRequest request, HttpServletResponse response);

    /**
     * 删除文件
     *
     * @param tenantId   租户
     * @param serverCode 服务器编码
     * @param url        存储路径
     */
    void deleteFile(Long tenantId, String serverCode, String url);

    /**
     * 获取目标服务器列表
     *
     * @param tenantId   租户Id
     * @param configCode 服务器配置编码
     * @return 服务器列表
     */
    List<ServerVO> getServerList(Long tenantId, String configCode);
}
