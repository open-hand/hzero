package org.hzero.file.infra.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

import com.jcraft.jsch.*;
import org.apache.commons.io.IOUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/08 9:06
 */
public class SftpClient {

    private Session session;
    /**
     * SFTP 登录用户名
     */
    private String username;
    /**
     * SFTP 登录密码
     */
    private String password;
    /**
     * 私钥
     */
    private String privateKey;
    /**
     * SFTP 服务器地址IP地址
     */
    private String hostname;
    /**
     * SFTP 端口
     */
    private int port;

    private ChannelSftp sftp;


    /**
     * 构造基于密码认证的sftp对象
     */
    public SftpClient(String hostname, Integer port, String username, String password) {
        this.username = username;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
    }

    /**
     * 构造基于秘钥认证的sftp对象
     */
    public SftpClient(String username, String privateKey, String host, Integer port) {
        this.username = username;
        this.privateKey = privateKey;
        this.hostname = host;
        this.port = port;
    }

    public SftpClient() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SftpClient.class);


    /**
     * 连接sftp服务器
     */
    private void initSftpClient() {
        try {
            JSch jsch = new JSch();
            if (privateKey != null) {
                // 设置私钥
                jsch.addIdentity(privateKey);
            }
            session = jsch.getSession(username, hostname, port);

            if (password != null) {
                session.setPassword(password);
            }
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FTP_CONNECTION, e);
        }
    }

    /**
     * 关闭连接 server
     */
    private void closeSftpClient() {
        if (sftp != null && sftp.isConnected()) {
            sftp.disconnect();
        }
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }


    /**
     * 文件上传
     *
     * @param local      本地文件路径
     * @param remotePath 远程文件夹路径
     * @param filename   文件名
     */
    public void upload(String local, String remotePath, String filename) {
        initSftpClient();
        try {
            createDirectory(remotePath);
            sftp.put(local, filename);
        } catch (SftpException e) {
            throw new CommonException(HfleMessageConstant.FTP_UPLOAD, e);
        } finally {
            closeSftpClient();
        }
    }

    /**
     * 文件上传
     *
     * @param pathname    上传路径
     * @param fileName    文件名
     * @param cover       是否覆盖文件
     * @param inputStream 输入流
     */
    public void uploadFile(String pathname, String fileName, Integer cover, InputStream inputStream) {
        initSftpClient();
        try {
            createDirectory(pathname);
            if (!Objects.equals(cover, BaseConstants.Flag.YES)) {
                for (Object item : sftp.ls(pathname)) {
                    String name = ((ChannelSftp.LsEntry) item).getFilename();
                    if (Objects.equals(name, fileName)) {
                        throw new CommonException(HfleMessageConstant.FTP_EXISTS);
                    }
                }
            }
            sftp.put(inputStream, fileName);
        } catch (SftpException e) {
            throw new CommonException(HfleMessageConstant.FTP_UPLOAD, e);
        } finally {
            closeSftpClient();
        }
    }

    /**
     * 创建并进入指定文件夹
     */
    private void createDirectory(String path) throws SftpException {
        // 文件路径分隔符
        String separator = HfleConstant.LINUX_SEPARATOR;
        if (!path.contains(HfleConstant.LINUX_SEPARATOR) && path.contains(HfleConstant.WINDOWS_SEPARATOR)) {
            separator = HfleConstant.WINDOWS_SEPARATOR;
        } else {
            Assert.isTrue(path.startsWith(HfleConstant.LINUX_SEPARATOR), HfleMessageConstant.FTP_PATH);
        }
        try {
            sftp.cd(path);
        } catch (SftpException e) {
            // 目录不存在，则创建文件夹
            String[] dirs = path.split(separator);
            StringBuilder tempPath = new StringBuilder();
            for (String dir : dirs) {
                tempPath.append(separator).append(dir);
                try {
                    sftp.cd(String.valueOf(tempPath));
                } catch (SftpException ex) {
                    sftp.mkdir(String.valueOf(tempPath));
                    sftp.cd(String.valueOf(tempPath));
                }
            }
        }
    }

    /**
     * 下载文件
     *
     * @param path     下载目录
     * @param fileName 文件名
     * @return 字节数组
     */
    public byte[] downloadFile(String path, String fileName) {
        initSftpClient();
        InputStream is = null;
        try {
            sftp.cd(path);
            is = sftp.get(fileName);
            return IOUtils.toByteArray(is);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FTP_DOWNLOAD, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.error("error", e);
            }
            closeSftpClient();
        }
    }


    /**
     * 删除文件
     *
     * @param path     路径
     * @param fileName 文件名
     */
    public void delete(String path, String fileName) {
        initSftpClient();
        try {
            sftp.cd(path);
            sftp.rm(fileName);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FTP_DELETE, e);
        } finally {
            closeSftpClient();
        }
    }
}
