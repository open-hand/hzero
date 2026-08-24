package org.hzero.file.infra.util;

import org.hzero.file.infra.constant.HfleMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.choerodon.core.exception.CommonException;

/**
 * 文件头工具类
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/06 14:28
 */
public class FileHeaderUtils {

    private FileHeaderUtils() {
    }

    // todo 验证使用content_type是否可以验证文件格式

    private static final Logger logger = LoggerFactory.getLogger(FileHeaderUtils.class);
    private static final Map<String, String> FILE_TYPE_MAP = new HashMap<>(16);

    static {
        getAllFileType(); // 初始化文件类型信息
    }


    private static void getAllFileType() {
        FILE_TYPE_MAP.put("47494638396126026f01", "gif");
        FILE_TYPE_MAP.put("49492a00227105008037", "tif");
        FILE_TYPE_MAP.put("424d228c010000000000", "bmp");
        FILE_TYPE_MAP.put("424d8240090000000000", "bmp");
        FILE_TYPE_MAP.put("424d8e1b030000000000", "bmp");
        FILE_TYPE_MAP.put("41433130313500000000", "dwg");
        FILE_TYPE_MAP.put("3c21444f435459504520", "html");
        FILE_TYPE_MAP.put("3c21646f637479706520", "htm");
        FILE_TYPE_MAP.put("48544d4c207b0d0a0942", "css");
        FILE_TYPE_MAP.put("696b2e71623d696b2e71", "js");
        FILE_TYPE_MAP.put("7b5c727466315c616e73", "rtf");
        FILE_TYPE_MAP.put("38425053000100000000", "psd");
        FILE_TYPE_MAP.put("46726f6d3a203d3f6762", "eml");
        FILE_TYPE_MAP.put("5374616E64617264204A", "mdb");
        FILE_TYPE_MAP.put("252150532D41646F6265", "ps");
        FILE_TYPE_MAP.put("255044462d312e350d0a", "pdf");
        FILE_TYPE_MAP.put("2e524d46000000120001", "rmvb");
        FILE_TYPE_MAP.put("464c5601050000000900", "flv");
        FILE_TYPE_MAP.put("00000020667479706d70", "mp4");
        FILE_TYPE_MAP.put("49443303000000002176", "mp3");
        FILE_TYPE_MAP.put("000001ba210001000180", "mpg");
        FILE_TYPE_MAP.put("3026b2758e66cf11a6d9", "wmv");
        FILE_TYPE_MAP.put("52494646e27807005741", "wav");
        FILE_TYPE_MAP.put("52494646d07d60074156", "avi");
        FILE_TYPE_MAP.put("4d546864000000060001", "mid");
        FILE_TYPE_MAP.put("504b0304140000000800", "zip");
        FILE_TYPE_MAP.put("526172211a0700cf9073", "rar");
        FILE_TYPE_MAP.put("235468697320636f6e66", "ini");
        FILE_TYPE_MAP.put("504b03040a0000000000", "jar");
        FILE_TYPE_MAP.put("4d5a9000030000000400", "exe");
        FILE_TYPE_MAP.put("3c25402070616765206c", "jsp");
        FILE_TYPE_MAP.put("4d616e69666573742d56", "mf");
        FILE_TYPE_MAP.put("3c3f786d6c2076657273", "xml");
        FILE_TYPE_MAP.put("494e5345525420494e54", "sql");
        FILE_TYPE_MAP.put("7061636b616765207765", "java");
        FILE_TYPE_MAP.put("406563686f206f66660d", "bat");
        FILE_TYPE_MAP.put("1f8b0800000000000000", "gz");
        FILE_TYPE_MAP.put("6c6f67346a2e726f6f74", "properties");
        FILE_TYPE_MAP.put("cafebabe0000002e0041", "class");
        FILE_TYPE_MAP.put("49545346030000006000", "chm");
        FILE_TYPE_MAP.put("04000000010000001300", "mxp");
        FILE_TYPE_MAP.put("504b0304140006000800", "docx");
        FILE_TYPE_MAP.put("d0cf11e0a1b11ae10000", "wps");
        FILE_TYPE_MAP.put("6431303a637265617465", "torrent");
    }

    public static String getFileHeader(MultipartFile multipartFile) {
        try (InputStream is = multipartFile.getInputStream()){
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            return bytesToHexString(b);
        } catch (Exception e) {
            logger.error("Get file header failed");
            return null;
        }
    }


    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static void checkFileType(String fileCode, String suffix) {
        List<String> typeList = new ArrayList<>();
        for (Map.Entry<String, String> entry : FILE_TYPE_MAP.entrySet()) {
            // 验证前5个字符比较
            if (entry.getKey().substring(0, 5).equalsIgnoreCase(fileCode.substring(0, 5))) {
                typeList.add(entry.getValue());
            }
        }
        // map中有该文件类型，但是没有匹配成功，抛出异常
        if (FILE_TYPE_MAP.containsValue(suffix) && !typeList.contains(suffix)) {
            logger.warn("File header type should be {}, but found {}", typeList, suffix);
            throw new CommonException(HfleMessageConstant.ERROR_LOAD_FILE_TYPE);
        }
    }
}
