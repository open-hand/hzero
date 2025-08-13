package org.hzero.platform.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.platform.domain.entity.DataGroup;

/**
 * 数据组定义应用服务
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupService {

    /**
     * 分页查询数据组
     * @param pageRequest 分页条件
     * @param dataGroup 查询条件
     * @return
     */
    Page<DataGroup> pageDataGroupByCondition(PageRequest pageRequest,DataGroup dataGroup);

    /**
     * 删除数据组
     * @param dataGroup
     */
    void deleteDataGroup(DataGroup dataGroup);

    /**
     * 创建数据组
     * @param dataGroup 数据组
     * @return
     */
    DataGroup createDataGroup(DataGroup dataGroup);

    /**
     * 数据组详情查询
     * @param dataGroup
     * @return
     */
    DataGroup detailDataGroup(DataGroup dataGroup);
}
