package org.hzero.boot.imported.infra.mapper;


import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.infra.enums.DataStatus;

import io.choerodon.mybatis.common.BaseMapper;


/**
 * 导入数据Mapper接口
 *
 * @author chuanqiang.bai@hand-china.com
 */
public interface ImportDataMapper extends BaseMapper<ImportData> {

    /**
     * 查询
     *
     * @param importData 对象
     * @return 数据集
     */
    List<ImportData> selectByBatch(ImportData importData);

    /**
     * 根据批号删除
     *
     * @param batch 批号
     */
    void deleteByBatch(@Param("batch") String batch);

    /**
     * 数据标记失败
     *
     * @param templateCode 模板编码
     * @param batch        批号
     * @param sheetIndex   页下标
     * @param status       数据状态
     * @param desc         失败描述
     */
    void updateFailed(@Param("templateCode") String templateCode,
                      @Param("batch") String batch,
                      @Param("sheetIndex") Integer sheetIndex,
                      @Param("status") DataStatus status,
                      @Param("desc") String desc);

    /**
     * 回滚修改数据状态
     *
     * @param id     主键
     * @param status 数据状态
     */
    void fallback(@Param("id") Long id,
                  @Param("status") DataStatus status);
}
