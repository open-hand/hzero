package org.hzero.admin.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.admin.domain.entity.MaintainTable;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 11:37 上午
 */
public interface MaintainTableMapper extends BaseMapper<MaintainTable> {

    /**
     * 查询运维表，查询条件：maintainId、serviceCodeLike and tableNameLike
     * @param serviceCode
     * @param tableName
     * @return list
     */
    List<MaintainTable> listMaintainTable(@Param("maintainId") Long maintainId,
                                          @Param("serviceCode") String serviceCode,
                                          @Param("tableName") String tableName);

    /**
     * 查询运维表，查询条件：maintainId and serviceCode
     * @param maintainId
     * @param serviceCode
     * @return list
     */
    List<MaintainTable> selectMaintainTables(@Param("maintainId") Long maintainId,
                                             @Param("serviceCode") String serviceCode);

    /**
     * 查询唯一的运维表，查询条件：serviceId and tableName
     * @param serviceCode
     * @param tableName
     * @return list
     */
    MaintainTable selectMaintainTable(@Param("maintainId") Long maintainId,
                                      @Param("serviceCode") String serviceCode,
                                      @Param("tableName") String tableName);
}
