package org.hzero.platform.infra.mapper;

import java.time.LocalDate;
import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.api.dto.OnLineUserDTO;
import org.hzero.platform.domain.entity.AuditLogin;

import io.choerodon.mybatis.common.BaseMapper;

/**
 * 登录日志审计Mapper
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
public interface AuditLoginMapper extends BaseMapper<AuditLogin> {

    /**
     * 模糊查询登录信息
     *
     * @param auditLogin 擦护心查询信息
     * @return 查询结果
     */
    List<AuditLogin> listAuditLogin(AuditLogin auditLogin);

    /**
     * 查询获取可访问租户列表及登录日志
     *
     * @param userId 当前用户Id
     * @return List<AuditLogin>
     */
    List<AuditLogin> selectSelfTenantsWithLogs(@Param("userId") Long userId);

    /**
     * 查询在线用户信息
     *
     * @param accessTokenList token列表
     * @return 用户信息
     */
    List<AuditLogin> listUserInfo(@Param("accessTokenList") List<String> accessTokenList);

    /**
     * 查询租户名
     *
     * @param tenantIdList 租户ID
     * @return 租户列表
     */
    List<OnLineUserDTO> listUserTenant(@Param("tenantIdList") List<Long> tenantIdList);

    /**
     * 根据时间查询登录信息
     *
     * @param time     时间
     * @param tenantId 租户ID
     * @return 查询结果
     */
    List<Long> listLogIdByTime(@Param("time") LocalDate time,
                               @Param("tenantId") Long tenantId);

    /**
     * 批量删除
     *
     * @param auditLoginIds 登录信息ID集合
     */
    void batchDeleteById(@Param("auditLoginIds") List<Long> auditLoginIds);
}
