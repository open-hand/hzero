package org.hzero.fragment.service;

import java.util.Map;

import org.hzero.core.util.Pair;
import org.springframework.web.multipart.MultipartFile;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/18 13:54
 */
public interface FragmentService {

    /**
     * 验证文件分片是否需要上传
     *
     * @param chunk     当前分片
     * @param chunkSize 分片大小
     * @param guid      当前文件的MD5值
     * @return 是否存在
     */
    Integer checkMd5(String chunk, String chunkSize, String guid);

    /**
     * 上传文件分片
     *
     * @param file  分片
     * @param chunk 当前分片编号
     * @param guid  当前文件的MD5值
     */
    void upload(MultipartFile file, Integer chunk, String guid);

    /**
     * 合并文件
     *
     * @param guid     当前文件的MD5值
     * @param fileName 文件名
     * @return 合并后文件地址, 分片文件夹
     */
    Pair<String, String> combineBlock(String guid, String fileName);

    /**
     * 合并文件
     *
     * @param guid     当前文件的MD5值
     * @param tenantId 租户Id
     * @param filename 文件名
     * @param params   参数
     * @return 合并后文件地址, 分片文件夹
     */
    String combineUpload(String guid, Long tenantId, String filename, Map<String, String> params);
}
