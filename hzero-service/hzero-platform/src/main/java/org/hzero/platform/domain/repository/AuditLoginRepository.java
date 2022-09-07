package org.hzero.platform.domain.repository;

import java.time.LocalDate;
import java.util.List;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.api.dto.OnLineUserDTO;
import org.hzero.platform.domain.entity.AuditLogin;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 登录日志审计资源库
 *
 * @author xingxing.wu@hand-china.com 2018-12-26 15:17:47
 */
public interface AuditLoginRepository extends BaseRepository<AuditLogin> {
    /**
     * 模糊分页查询登录信息
     *
     * @param pageRequest 分页信息
     * @param auditLogin  模糊查询查询信息
     * @return 查询结果
     */
    Page<AuditLogin> pageAuditLogin(PageRequest pageRequest, AuditLogin auditLogin);

    /**
     * 模糊查询登录信息
     *
     * @param auditLogin 模糊查询信息
     * @return 查询结果
     */
    List<AuditLogin> listAuditLogin(AuditLogin auditLogin);

    /**
     * 查询获取可访问租户列表及登录日志
     *
     * @return List<AuditLogin>
     */
    List<AuditLogin> getSelfTenantsWithLogs();

    /**
     * 查询在线用户信息
     *
     * @param accessTokenList token列表
     * @return 用户信息
     */
    List<AuditLogin> listUserInfo(List<String> accessTokenList);

    /**
     * 查询租户名
     *
     * @param tenantIdList 租户ID
     * @return 租户列表
     */
    List<OnLineUserDTO> listUserTenant(List<Long> tenantIdList);

    /**
     * 查询日志
     *
     * @param time        时间
     * @param tenantId    租户ID
     * @param pageRequest 分页对象
     * @return 查询结果
     */
    Page<Long> listLogId(LocalDate time, Long tenantId, PageRequest pageRequest);

    /**
     * 批量登录信息
     *
     * @param auditLoginIds 登录信息ID集合
     */
    void batchDeleteById(List<Long> auditLoginIds);
}
