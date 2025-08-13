package org.hzero.platform.domain.repository;

import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.PermissionRangeExcl;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * 屏蔽范围黑名单资源库
 *
 * @author qingsheng.chen@hand-china.com 2020-06-10 10:17:25
 */
public interface PermissionRangeExclRepository extends BaseRepository<PermissionRangeExcl> {

    List<PermissionRangeExcl> listExcl(Set<Long> rangeIds);
}
