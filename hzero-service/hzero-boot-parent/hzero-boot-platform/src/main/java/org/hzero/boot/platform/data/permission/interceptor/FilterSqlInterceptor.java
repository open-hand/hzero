package org.hzero.boot.platform.data.permission.interceptor;

import net.sf.jsqlparser.schema.Table;
import org.hzero.boot.platform.data.permission.vo.PermissionRangeVO;

import io.choerodon.core.oauth.CustomUserDetails;

/**
 * 屏蔽规则拦截器
 *
 * @author gaokuo.dai@hand-china.com 2018年8月14日下午5:37:04
 */
public interface FilterSqlInterceptor {

    /**
     * 拦截处理筛选sql
     *
     * @param userDetails     用户信息
     * @param table           屏蔽表
     * @param permissionRange 屏蔽规则
     * @return 拦截之后的屏蔽规则，返回null该规则不生效
     */
    PermissionRangeVO process(CustomUserDetails userDetails, Table table, PermissionRangeVO permissionRange);

}
