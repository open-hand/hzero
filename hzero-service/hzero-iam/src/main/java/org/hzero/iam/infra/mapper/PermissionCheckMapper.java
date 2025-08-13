package org.hzero.iam.infra.mapper;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.PermissionCheck;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 缺失权限mapper
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 */
public interface PermissionCheckMapper extends BaseMapper<PermissionCheck> {

    /**
     * 查询缺失权限
     *
     * @param permissionCheck 查询条件
     * @return 缺失权限记录
     */
    List<PermissionCheck> selectPermissionCheck(PermissionCheck permissionCheck);

    /**
     * 缺失权限明细
     *
     * @param permissionCheckId 缺失权限ID
     * @return PermissionCheck
     */
    PermissionCheck selectPermissionDetail(@Param("permissionCheckId") Long permissionCheckId);

    /**
     * 根据日期查询缺失权限ID
     *
     * @param localDate 日期
     * @return permissionCheckIds
     */
    List<Long> selectPermissionCheckId(@Param("localDate") LocalDate localDate, @Param("checkState") String checkState);

    /**
     * 根据Id批量删除缺失权限记录
     *
     * @param permissionCheckIds 缺失权限ID
     */
    void batchDeleteById(@Param("permissionCheckIds") List<Long> permissionCheckIds);

    /**
     * 
     * 获取缺失权限菜单权限集信息
     * @param permissionCodes 权限编码集合
     * @return 缺失权限菜单权限集信息
     */
    List<PermissionCheck> selectMenuPermissionSet(@Param("permissionCodes") Set<String> permissionCodes);
}
