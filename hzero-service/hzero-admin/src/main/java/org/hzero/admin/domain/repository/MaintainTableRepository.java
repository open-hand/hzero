package org.hzero.admin.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.MaintainTable;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 11:44 上午
 */
public interface MaintainTableRepository extends BaseRepository<MaintainTable> {
    /**
     * 根据serviceCode、tableName分页查询
     * @param maintainId
     * @param serviceCode
     * @param tableName
     * @return
     */
    Page<MaintainTable> pageMaintainTable(Long maintainId,String serviceCode, String tableName, PageRequest pageRequest);

    /**
     * 根据maintainId、serviceCode查询
     * @param maintainId
     * @param serviceCode
     * @return
     */
    List<MaintainTable> selectMaintainTables(Long maintainId, String serviceCode);

    /**
     * 根据maintainId、tableName查询
     * @param serviceCode
     * @param tableName
     * @return
     */
    MaintainTable selectMaintainTable(Long maintainId, String serviceCode, String tableName);
}
