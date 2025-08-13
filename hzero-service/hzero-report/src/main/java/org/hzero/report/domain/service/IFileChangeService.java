package org.hzero.report.domain.service;

import java.io.InputStream;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/03/20 15:46
 */
public interface IFileChangeService {

    /**
     * 文件类型转换
     *
     * @param tenantId 租户Id
     * @param url      文件url
     * @return inputStream
     */
    InputStream changeToRtf(Long tenantId, String url);
}
