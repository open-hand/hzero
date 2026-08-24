package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.DataGroup;

import java.util.List;

/**
 * 数据组定义资源库
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupRepository extends BaseRepository<DataGroup> {
    /**
     * 分页查询数据组
     * @param pageRequest 分页条件
     * @param dataGroup 查询条件
     * @return
     */
    Page<DataGroup> pageDataGroup(PageRequest pageRequest,DataGroup dataGroup);
}
