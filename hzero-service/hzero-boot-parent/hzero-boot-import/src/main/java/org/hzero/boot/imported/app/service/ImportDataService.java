package org.hzero.boot.imported.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import org.hzero.boot.imported.api.dto.ImportDTO;
import org.hzero.boot.imported.domain.entity.Import;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 通用导入服务
 *
 * @author shuangfei.zhu@hand-china.com 2018/12/07 10:29
 */
public interface ImportDataService {

    /**
     * 从文件导入临时表（excel/csv）
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param param        自定义参数
     * @param file         文件
     * @return 批次号
     * @throws IOException IOException
     */
    String uploadData(Long tenantId, String templateCode, String param, MultipartFile file) throws IOException;

    /**
     * 从文件导入临时表（excel/csv）
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param param        自定义参数
     * @param inputStream  文件流
     * @param filename     文件名
     * @return 批次号
     */
    String uploadData(Long tenantId, String templateCode, String param, InputStream inputStream, String filename);

    /**
     * 从excel导入临时表(同步)
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param param        自定义参数
     * @param file         excel
     * @return 批次号
     */
    String syncUploadData(Long tenantId, String templateCode, String param, MultipartFile file);

    /**
     * 从文件导入临时表（excel/csv）
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param param        自定义参数
     * @param sheetIndex   页下标
     * @param data         json数据
     * @return 批次号
     */
    String syncUploadData(Long tenantId, String templateCode, String param, Integer sheetIndex, JsonNode data);

    /**
     * 查询已上传数据
     *
     * @param templateCode 模板编码
     * @param batch        批次号
     * @param sheetIndex   页下标
     * @param status       状态
     * @param pageRequest  分页
     * @return 上传数据
     */
    Page<ImportData> pageData(String templateCode, String batch, Integer sheetIndex, DataStatus status, PageRequest pageRequest);

    /**
     * 校验临时表数据
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param batch        批次号
     * @param args         自定义参数
     * @return 状态信息
     */
    Import validateData(Long tenantId, String templateCode, String batch, Map<String, Object> args);

    /**
     * 校验临时表数据(同步)
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param batch        批次号
     * @param args         自定义参数
     * @return 状态信息
     */
    Import syncValidateData(Long tenantId, String templateCode, String batch, Map<String, Object> args);

    /**
     * 校验JSON数据
     *
     * @param template   模板
     * @param sheetIndex sheet页下标
     * @param jsonData   导入数据
     */
    void syncValidateJsonData(Template template, Integer sheetIndex, String jsonData);

    /**
     * 导入正式表
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param batch        批次号
     * @param args         自定义参数
     * @return 状态信息
     */
    ImportDTO importData(Long tenantId, String templateCode, String batch, Map<String, Object> args);

    /**
     * 导入正式表(同步)
     *
     * @param tenantId     租户Id
     * @param templateCode 模板编码
     * @param batch        批次号
     * @param args         自定义参数
     * @return 状态信息
     */
    ImportDTO syncImportData(Long tenantId, String templateCode, String batch, Map<String, Object> args);

    /**
     * 修改临时表数据
     *
     * @param id   临时数据Id
     * @param data 数据
     * @return 修改结果
     */
    String updateImportData(Long id, String data);

    /**
     * 根据主键删除
     *
     * @param id 主键
     */
    void deleteById(Long id);

    /**
     * 数据清理
     *
     * @param importList 导入记录
     */
    void clearData(List<Import> importList);

    /**
     * Excel数据导出
     *
     * @param tenantId     租户
     * @param templateCode 模板编码
     * @param batch        批次号
     * @param sheetIndex   sheet下标
     * @param status       数据状态
     * @param response     response
     */
    void exportExcelData(Long tenantId, String templateCode, String batch, Integer sheetIndex, DataStatus status, HttpServletResponse response);

    /**
     * Csv数据导出
     *
     * @param tenantId     租户
     * @param templateCode 模板编码
     * @param batch        批次号
     * @param sheetIndex   sheet下标
     * @param status       数据状态
     * @param response     response
     */
    void exportCsvData(Long tenantId, String templateCode, String batch, Integer sheetIndex, DataStatus status, HttpServletResponse response);
}
