package org.hzero.iam.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.hzero.iam.api.dto.MenuPermissionSetDTO;
import org.hzero.iam.domain.entity.PermissionCheck;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 缺失权限
 *
 * @author KAIBING.JIANG@HAND-CHINA.COM
 */
public interface PermissionCheckRepository extends BaseRepository<PermissionCheck> {

    /**
     * 分页查询缺失权限
     *
     * @param permissionCheck permissionCheck
     * @param pageRequest     pageRequest
     * @return 缺失权限列表
     */
    Page<PermissionCheck> selectPermissionCheck(PermissionCheck permissionCheck, PageRequest pageRequest);

    /**
     * 缺失权限明细
     *
     * @param permissionCheckId 缺失权限ID
     * @return 缺失权限
     */
    PermissionCheck selectPermissionDetail(Long permissionCheckId);

    /**
     * 根据日期查询缺失权限ID
     *
     * @param localDate
     * @param pageRequest
     * @return
     */
    Page<Long> listPermissionCheckId(LocalDate localDate, String checkState, PageRequest pageRequest);

    /**
     * 根据ID批量删除缺失权限记录
     *
     * @param permissionCheckIds
     */
    void batchDeleteById(List<Long> permissionCheckIds);

    /**
     * 
     * 根据权限编码集合获取缺失权限菜单及权限集信息
     * @param menuPermissionSetDTO 权限集合
     * @return 缺失权限信息
     */
    List<PermissionCheck> selectMenuPermissionSet(MenuPermissionSetDTO menuPermissionSetDTO);

}
