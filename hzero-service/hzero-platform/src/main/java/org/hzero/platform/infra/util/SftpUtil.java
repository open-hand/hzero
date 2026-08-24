package org.hzero.platform.infra.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.hzero.platform.domain.entity.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.hzero.platform.infra.constant.Constants;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SftpUtil implements AutoCloseable {
    private Logger logger = LoggerFactory.getLogger(SftpUtil.class);

    private static final String SPLIT_PATTERN = "/";

    private Session session = null;
    private ChannelSftp channel = null;


    /**
     * 连接sftp服务器
     *
     * @param serverIP 服务IP
     * @param port     端口
     * @param userName 用户名
     * @param password 密码
     * @throws JSchException JSchException
     */
    public void connectServer(String serverIP,
                               int port,
                               String userName,
                               String password) throws JSchException {
        JSch jsch = new JSch();
        // 根据用户名，主机ip，端口获取一个Session对象
        session = jsch.getSession(userName, serverIP, port);
        // 设置密码
        session.setPassword(password);
        // 为Session对象设置properties
        Properties config = new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);
        // 通过Session建立链接
        session.connect();
        // 打开SFTP通道
        channel = (ChannelSftp) session.openChannel("sftp");
        // 建立SFTP通道的连接
        channel.connect();
    }

    /**
     * @param info 服务器信息
     * @throws JSchException JSchException
     * @author lei.xie@hand-china.com
     * @date 2018-12-20 10:45:14
     */
    public void connectServer(Server info) throws JSchException {
        String userName = info.getLoginUser();
        String ip = info.getIp();
        int port = info.getPort();
        String loginEncPwd = info.getLoginEncPwd();
        connectServer(ip, port, userName, loginEncPwd);
    }

    /**
     * 自动关闭资源
     */
    @Override
    public void close() {
        if (channel != null) {
            channel.disconnect();
        }
        if (session != null) {
            session.disconnect();
        }
    }

    /**
     * @param path path
     * @return List<ChannelSftp.LsEntry>
     * @throws SftpException s
     * @author zhilong.deng@hand-china.com
     * @date 2018/7/16 21:57
     */
    public List<ChannelSftp.LsEntry> getDirList(String path) throws SftpException {
        List<ChannelSftp.LsEntry> list = new ArrayList<>();
        if (channel != null) {
            Vector vv = channel.ls(path);
            if (vv == null || vv.size() == 0) {
                return list;
            } else {
                Object[] aa = vv.toArray();
                for (Object obj : aa) {
                    ChannelSftp.LsEntry temp = (ChannelSftp.LsEntry) obj;
                    list.add(temp);
                }
            }
        }
        return list;
    }

    /**
     * @param path   path
     * @param suffix suffix
     * @return List<ChannelSftp.LsEntry>
     * @throws SftpException s
     * @author zhilong.deng@hand-china.com
     * @date 2018/7/16 21:57
     */
    public List<ChannelSftp.LsEntry> getFiles(String path, String suffix) throws SftpException {
        List<ChannelSftp.LsEntry> list = new ArrayList<>();
        if (channel != null) {
            channel.ls(path, lsEntry -> {
                if (!lsEntry.getAttrs().isDir() && lsEntry.getFilename().endsWith(suffix)) {
                    list.add(lsEntry);
                }
                return 0;
            });

        }
        return list;
    }

    /**
     * 下载文件
     *
     * @param remotePathFile 远程文件
     * @param localPathFile  本地文件[绝对路径]
     * @throws SftpException SftpException
     * @throws IOException   IOException
     */
    public void downloadFile(String remotePathFile, String localPathFile) throws SftpException, IOException {
        try (FileOutputStream os = new FileOutputStream(new File(localPathFile))) {
            if (channel == null) {
                throw new IOException("sftp server not login");
            }
            channel.get(remotePathFile, os);
        }
    }

    /**
     * @param pathFile 远程文件
     * @return InputStream InputStream
     * @throws SftpException SftpException
     */
    public InputStream getFileInputStream(String pathFile) throws SftpException {
        return channel.get(pathFile);
    }

    /**
     * 上传文件
     *
     * @param remoteFile 远程文件
     * @param localFile  l
     * @throws SftpException s
     * @throws IOException   i
     */
    public void uploadFileWithStr(String remoteFile, String localFile) throws SftpException, IOException {
        try (FileInputStream in = new FileInputStream(new File(localFile))) {
            if (channel == null) {
                throw new IOException("sftp server not login");
            }
            channel.put(in, remoteFile);
        }
    }

    /**
     * 上传文件
     *
     * @param remoteFile 远程文件
     * @param in         InputStream
     * @throws SftpException s
     * @throws IOException   i
     */
    public void uploadFile(String remoteFile, InputStream in) throws SftpException, IOException {
        if (channel == null) {
            throw new IOException("sftp server not login");
        }
        channel.put(in, remoteFile);
    }

    /**
     * 获取文件大小
     *
     * @param filePath filePath
     * @return Long
     * @throws SftpException SftpException
     * @throws IOException   IOException
     */
    public Long getFileSize(String filePath) throws SftpException, IOException {
        if (channel == null) {
            throw new IOException("sftp server not login");
        }
        return channel.lstat(filePath).getSize();
    }

    /**
     * 移动文件
     *
     * @param sourcePath sourcePath
     * @param targetPath targetPath
     * @throws SftpException SftpException
     * @throws IOException   IOException
     */
    public void rename(String sourcePath, String targetPath) throws SftpException, IOException {
        if (channel == null) {
            throw new IOException("sftp server not login");
        }
        channel.rename(sourcePath, targetPath);
    }

    /**
     * 删除服务器文件
     *
     * @param filePath filePath
     * @throws SftpException SftpException
     * @throws IOException   IOException
     */
    public void remove(String filePath) throws SftpException, IOException {
        if (channel == null) {
            throw new IOException("sftp server not login");
        }

        try {
            channel.rename(filePath, filePath + ".bak");
            channel.rm(filePath);
        } catch (SftpException e) {
            logger.error("文件不存在");
        }
    }

    /**
     * 创建目录
     *
     * @param path 目录
     * @param dir  目录
     * @throws SftpException s
     * @throws IOException   i
     */
    public synchronized void mkdir(String path, String dir) throws SftpException, IOException {
        StringBuilder builder = new StringBuilder();
        builder.append(path);
        if (channel == null) {
            throw new IOException("sftp server not login");
        }
        for (String d : dir.split(SPLIT_PATTERN)) {
            builder.append(Constants.Server.SEPARATOR).append(d);
            try {
                channel.ls(builder.toString());
            } catch (SftpException e) {
                channel.mkdir(builder.toString());
            }
        }
    }

    /**
     * 创建目录
     *
     * @param dir  目录
     * @throws SftpException s
     * @throws IOException   i
     */
    public synchronized void mkdir(String dir) throws SftpException, IOException {
        StringBuilder builder = new StringBuilder();
        if (channel == null) {
            throw new IOException("sftp server not login");
        }
        for (String path : dir.split(Constants.Server.SEPARATOR)) {
            builder.append(Constants.Server.SEPARATOR).append(path);
            try {
                channel.ls(builder.toString());
            } catch (SftpException e) {
                channel.mkdir(builder.toString());
            }
        }
    }

}