package org.hzero.message.domain.repository;

import org.hzero.message.domain.entity.EmailFilter;
import org.hzero.mybatis.base.BaseRepository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;

/**
 * 邮箱账户黑白名单资源库
 *
 * @author shuangfei.zhu@hand-china.com 2019-06-04 14:23:17
 */
public interface EmailFilterRepository extends BaseRepository<EmailFilter> {

    /**
     * 分页查询
     *
     * @param pageRequest 分页
     * @param address     地址
     * @param serverId    邮箱配置Id
     * @return 查询结果
     */
    Page<EmailFilter> pageEmailFilter(PageRequest pageRequest, String address, Long serverId);
}
