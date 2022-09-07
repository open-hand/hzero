package org.hzero.fragment.service;

import java.io.InputStream;
import java.util.Map;

/**
 * 文件处理
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/18 14:14
 */
public interface FileHandler {

    /**
     * 文件处理
     *
     * @param tenantId    租户Id
     * @param filename    文件名
     * @param filePath    文件本地路径
     * @param inputStream 文件流
     * @param params      参数
     * @return string
     */
    default String process(Long tenantId, String filename, String filePath, InputStream inputStream, Map<String, String> params) {
        return null;
    }
}
