package org.hzero.boot.imported.domain.repository;

import java.util.List;

import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.infra.enums.DataStatus;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 导入数据仓储
 *
 * @author baichunqiang.ai@hand-china.com
 */
public interface ImportDataRepository extends BaseRepository<ImportData> {

    /**
     * 查询
     *
     * @param templateCode 模板编码
     * @param batch        批量
     * @param sheetIndex   页下标
     * @param status       状态
     * @param pageRequest  分页
     * @return 数据
     */
    Page<ImportData> pageData(String templateCode, String batch, Integer sheetIndex, DataStatus status, PageRequest pageRequest);

    /**
     * 查询
     *
     * @param templateCode 模板编码
     * @param batch        批量
     * @param sheetIndex   页下标
     * @param status       状态
     * @return 数据
     */
    List<ImportData> listData(String templateCode, String batch, Integer sheetIndex, DataStatus status);

    /**
     * 根据批号删除
     *
     * @param batch 批号
     */
    void deleteByBatch(String batch);

    /**
     * 数据标记失败
     *
     * @param templateCode 模板编码
     * @param batch        批号
     * @param sheetIndex   页下标
     * @param status       数据状态
     * @param desc         失败描述
     */
    void updateFailed(String templateCode, String batch, Integer sheetIndex, DataStatus status, String desc);

    /**
     * 回滚修改数据状态
     *
     * @param id     主键
     * @param status 数据状态
     */
    void fallback(Long id, DataStatus status);

    void update(ImportData data);
}
