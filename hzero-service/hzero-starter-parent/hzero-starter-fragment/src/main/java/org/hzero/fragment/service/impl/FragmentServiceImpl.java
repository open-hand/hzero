package org.hzero.fragment.service.impl;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;
import org.hzero.fragment.config.FragmentConfig;
import org.hzero.fragment.service.FileHandler;
import org.hzero.fragment.service.FragmentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/18 14:07
 */
@Service
public class FragmentServiceImpl implements FragmentService {

    private static final Logger logger = LoggerFactory.getLogger(FragmentServiceImpl.class);

    public static final String ROOT = System.getProperty("user.dir") + File.separator + "file";
    public static final String TEMP = "temp";
    public static final String REAL = "real";

    @Autowired
    private FragmentConfig fragmentConfig;

    private String getRootPath() {
        String rootPath = fragmentConfig.getRootPath();
        if (StringUtils.isBlank(rootPath)) {
            return ROOT;
        }
        return rootPath;
    }

    @Override
    public Integer checkMd5(String chunk, String chunkSize, String guid) {
        // 分片上传路径
        String tempPath = getRootPath() + File.separator + TEMP;
        File checkFile = new File(tempPath + File.separator + guid + File.separator + chunk);
        // 如果当前分片存在，并且长度等于上传的大小
        if (checkFile.exists() && checkFile.length() == Integer.parseInt(chunkSize)) {
            return BaseConstants.Flag.YES;
        } else {
            return BaseConstants.Flag.NO;
        }
    }

    @Override
    public void upload(MultipartFile file, Integer chunk, String guid) {
        String filePath = getRootPath() + File.separator + TEMP + File.separator + guid;
        File tempFile = new File(filePath);
        if (!tempFile.exists()) {
            Assert.isTrue(tempFile.mkdirs(), "Create file directory error.");
        }
        RandomAccessFile raFile = null;
        if (chunk == null) {
            chunk = 0;
        }
        try (BufferedInputStream inputStream = new BufferedInputStream(file.getInputStream())) {
            File dirFile = new File(filePath, String.valueOf(chunk));
            //以读写的方式打开目标文件
            raFile = new RandomAccessFile(dirFile, "rw");
            raFile.seek(raFile.length());
            byte[] buf = new byte[1024];
            int length;
            while ((length = inputStream.read(buf)) != -1) {
                raFile.write(buf, 0, length);
            }
        } catch (Exception e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        } finally {
            if (raFile != null) {
                try {
                    raFile.close();
                } catch (IOException e) {
                    logger.warn(e.getMessage());
                }
            }
        }
    }

    @Override
    public Pair<String, String> combineBlock(String guid, String fileName) {
        // 分片文件临时目录
        String tempDir = getRootPath() + File.separator + TEMP + File.separator + guid;
        File tempPath = new File(tempDir);
        // 真实上传路径
        File realPath = new File(getRootPath() + File.separator + REAL);
        if (!realPath.exists()) {
            Assert.isTrue(realPath.mkdirs(), "Create file directory error.");
        }
        String filePath = getRootPath() + File.separator + REAL + File.separator + fileName;
        File realFile = new File(filePath);
        // 文件追加写入
        FileChannel fcIn;
        try (FileOutputStream os = new FileOutputStream(realFile, true);
             FileChannel fcOut = os.getChannel()) {
            logger.info("file start to merge, filename : {}, MD5 : {}", fileName, guid);
            if (!tempPath.exists()) {
                throw new CommonException("read file error");
            }
            // 获取临时目录下的所有文件
            File[] tempFiles = tempPath.listFiles();
            Assert.notNull(tempFiles, BaseConstants.ErrorCode.ERROR);
            // 按名称排序
            Arrays.sort(tempFiles, Comparator.comparingInt(o -> Integer.parseInt(o.getName())));
            // 每次读取2MB大小，字节读取
            // 设置缓冲区为2MB
            ByteBuffer buffer = ByteBuffer.allocate(2 * 1024 * 1024);
            for (File file : tempFiles) {
                FileInputStream fis = new FileInputStream(file);
                fcIn = fis.getChannel();
                if (fcIn.read(buffer) != -1) {
                    buffer.flip();
                    while (buffer.hasRemaining()) {
                        fcOut.write(buffer);
                    }
                }
                buffer.clear();
                fis.close();
            }
            logger.info("file merged successfully!  filename : {}, MD5 : {}", fileName, guid);
            return Pair.of(filePath, tempDir);
        } catch (IOException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    @Override
    public String combineUpload(String guid, Long tenantId, String filename, Map<String, String> params) {
        if (params == null) {
            params = new HashMap<>(1);
        }
        Pair<String, String> pair = combineBlock(guid, filename);
        String filePath = pair.getFirst();
        String tempDtr = pair.getSecond();
        try {
            // 上传文件到对象存储
            Map<String, FileHandler> map = ApplicationContextHelper.getContext().getBeansOfType(FileHandler.class);
            String url = null;
            if (!map.isEmpty()) {
                for (FileHandler handler : map.values()) {
                    url = handler.process(tenantId, filename, filePath, new FileInputStream(new File(filePath)), params);
                }
            }
            // 删除分片
            deleteFile(new File(tempDtr));
            return url;
        } catch (Exception ex) {
            logger.error("exception");
            return null;
        } finally {
            // 删除文件
            deleteFile(new File(filePath));
        }
    }

    /**
     * 删除文件
     *
     * @param file 文件
     */
    private void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File f : files) {
                deleteFile(f);
            }
        }
        file.delete();
    }
}
