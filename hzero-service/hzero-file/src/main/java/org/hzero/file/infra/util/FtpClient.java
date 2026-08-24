package org.hzero.file.infra.util;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.hzero.core.base.BaseConstants;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;


/**
 * 类说明：文件上传下载工具类
 *
 * @author shuangfei.zhu@hand-china.com 2019/07/01 20:04
 */
public class FtpClient {

    private static final Logger logger = LoggerFactory.getLogger(FtpClient.class);

    private final String hostname;
    private final Integer port;
    private final String username;
    private final String password;

    public FtpClient(String hostname, Integer port, String username, String password) {
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    private FTPClient client = null;

    /**
     * 初始化ftp服务器
     */
    private void initFtpClient() {
        client = new FTPClient();
        client.setControlEncoding(BaseConstants.DEFAULT_CHARSET);
        try {
            // 连接ftp服务器
            client.connect(hostname, port);
            // 登录ftp服务器
            client.login(username, password);
            // 是否成功登录服务器
            int replyCode = client.getReplyCode();
            if (!FTPReply.isPositiveCompletion(replyCode)) {
                throw new CommonException(HfleMessageConstant.FTP_CONNECTION);
            }
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FTP_CONNECTION, e);
        }
    }

    /**
     * 关闭客户端连接(工具中所有public方法都要调用)
     */
    private void closeFtpClient() {
        if (client.isConnected()) {
            try {
                client.logout();
                client.disconnect();
            } catch (IOException e) {
                logger.warn("ftpClient close failed!");
            }
        }
    }

    /**
     * 断点续传
     *
     * @param local      本地文件路径
     * @param remotePath 远程路径
     * @param filename   文件名
     */
    public void upload(String local, String remotePath, String filename) {
        try {
            client.initiateListParsing();
            // 设置PassiveMode传输
            client.enterLocalPassiveMode();
            // 设置以二进制流的方式传输
            client.setFileType(FTP.BINARY_FILE_TYPE);
            client.setControlEncoding("GBK");
            // 创建并进入目录
            createDirectory(remotePath);

            // 检查远程是否存在文件
            FTPFile[] files = client.listFiles(new String(filename.getBytes("GBK"), StandardCharsets.ISO_8859_1));
            if (files.length == 1) {
                long remoteSize = files[0].getSize();
                File f = new File(local);
                long localSize = f.length();
                if (remoteSize == localSize) {
                    return;
                }
                // 尝试移动文件内读取指针,实现断点续传
                uploadFile(filename, f, remoteSize);
            } else {
                throw new CommonException(BaseConstants.ErrorCode.ERROR);
            }
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FTP_UPLOAD, e);
        }
    }

    private void uploadFile(String remoteFile, File localFile, long remoteSize) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(localFile, "r");
        OutputStream out = client.appendFileStream(new String(remoteFile.getBytes("GBK"), StandardCharsets.ISO_8859_1));
        // 断点续传
        if (remoteSize > 0) {
            client.setRestartOffset(remoteSize);
            raf.seek(remoteSize);
        }
        byte[] bytes = new byte[1024];
        int c;
        while ((c = raf.read(bytes)) != -1) {
            out.write(bytes, 0, c);
        }
        out.flush();
        raf.close();
        out.close();
    }

    /**
     * 上传文件
     *
     * @param pathname    ftp服务保存地址
     * @param fileName    上传到ftp的文件名
     * @param cover       是否覆盖文件
     * @param inputStream 输入文件流
     */
    public void uploadFile(String pathname, String fileName, Integer cover, InputStream inputStream) {
        initFtpClient();
        try {
            client.setFileType(FTP.BINARY_FILE_TYPE);
            createDirectory(pathname);
            FTPFile[] ftpFileArr = client.listFiles(pathname);
            if (!Objects.equals(cover, BaseConstants.Flag.YES)) {
                for (FTPFile item : ftpFileArr) {
                    if (Objects.equals(item.getName(), fileName)) {
                        throw new CommonException(HfleMessageConstant.FTP_EXISTS);
                    }
                }
            }
            client.storeFile(fileName, inputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new CommonException(HfleMessageConstant.FTP_UPLOAD, e);
        } finally {
            closeFtpClient();
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("IO close failed!");
                }
            }
        }
    }

    /**
     * 改变目录路径
     */
    private boolean changeWorkingDirectory(String directory) throws IOException {
        boolean flag;
        flag = client.changeWorkingDirectory(directory);
        return flag;
    }

    /**
     * 创建并进入指定文件夹
     */
    private void createDirectory(String path) throws IOException {
        // 文件路径分隔符
        String separator = HfleConstant.LINUX_SEPARATOR;
        if (!path.contains(HfleConstant.LINUX_SEPARATOR) && path.contains(HfleConstant.WINDOWS_SEPARATOR)) {
            separator = HfleConstant.WINDOWS_SEPARATOR;
        } else {
            Assert.isTrue(path.startsWith(HfleConstant.LINUX_SEPARATOR), HfleMessageConstant.FTP_PATH);
        }
        // 根目录无需创建
        if (Objects.equals(path, HfleConstant.LINUX_SEPARATOR)) {
            changeWorkingDirectory(path);
            return;
        }
        // 如果远程目录不存在，则递归创建远程服务器目录
        if (!changeWorkingDirectory(path)) {
            String[] dirs = path.split(separator);
            StringBuilder pathDir = new StringBuilder();
            for (String item : dirs) {
                pathDir.append(separator).append(item);
                String p = String.valueOf(pathDir);
                if (!existPath(String.valueOf(pathDir))) {
                    // 创建文件夹
                    makeDirectory(p);
                }
                changeWorkingDirectory(p);
            }
        }
    }

    /**
     * 判断ftp服务器文件路径是否存在
     */
    private boolean existPath(String path) throws IOException {
        FTPFile[] ftpFileArr = client.listFiles(path);
        return ftpFileArr.length > 0;
    }

    /**
     * 创建目录
     */
    private void makeDirectory(String dir) {
        try {
            client.makeDirectory(dir);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FTP_MKDIR, e);
        }
    }

    /**
     * 下载文件
     *
     * @param path     路径
     * @param fileName 文件名
     * @return 文件流
     */
    public byte[] downloadFile(String path, String fileName) {
        InputStream is = null;
        try {
            initFtpClient();
            // 切换目录
            client.changeWorkingDirectory(path);
            FTPFile[] ftpFiles = client.listFiles();
            for (FTPFile file : ftpFiles) {
                if (fileName.equalsIgnoreCase(file.getName())) {
                    is = client.retrieveFileStream(file.getName());
                }
            }
            client.logout();
            return IOUtils.toByteArray(Objects.requireNonNull(is));
        } catch (IOException e) {
            throw new CommonException(HfleMessageConstant.FTP_DOWNLOAD, e);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                logger.error("error", e);
            }
            closeFtpClient();
        }
    }

    /**
     * 删除文件
     *
     * @param path     路径
     * @param fileName 文件名
     */
    public void deleteFile(String path, String fileName) {
        try {
            initFtpClient();
            // 切换FTP目录
            client.changeWorkingDirectory(path);
            client.dele(fileName);
        } catch (Exception e) {
            throw new CommonException(HfleMessageConstant.FTP_DELETE, e);
        } finally {
            closeFtpClient();
        }
    }
}