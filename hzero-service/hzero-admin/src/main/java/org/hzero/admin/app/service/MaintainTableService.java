package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.api.dto.Report;
import org.hzero.admin.domain.entity.MaintainTable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 1:54 下午
 */
public interface MaintainTableService {
    /**
     * 根据maintainId、serviceCode、tableName分页查询
     * @param maintainId
     * @param serviceCode
     * @param tableName
     * @param pageRequest
     * @return
     */
    Page<MaintainTable> page(Long maintainId, String serviceCode, String tableName, PageRequest pageRequest);

    /**
     * 以服务为维度，启用运维
     * @param maintainId
     * @param serviceCode
     * @return
     */
    Report enable(Long maintainId, String serviceCode);

    /**
     * 以服务为维度，禁用运维
     * @param maintainId
     * @param serviceCode
     * @return
     */
    Report disable(Long maintainId, String serviceCode);

    /**
     * 插入
     * @param maintainTable
     */
    void insert(MaintainTable maintainTable);

    /**
     * 更新
     * @param maintainTable
     */
    void updateByPrimaryKey(MaintainTable maintainTable);

    /**
     * 删除
     * @param maintainId
     * @param maintainTableId
     */
    void deleteByPrimaryKey(Long maintainId, Long maintainTableId);

    /**
     * 批量更新
     * @param maintainTables
     */
    void batchUpdate(List<MaintainTable> maintainTables);

    /**
     * 模版下载
     * @param response
     */
    void downloadTemplate(HttpServletResponse response);

    /**
     * 导入数据
     * @param multipartFile
     */
    void importData(Long maintainId, MultipartFile multipartFile);
}
