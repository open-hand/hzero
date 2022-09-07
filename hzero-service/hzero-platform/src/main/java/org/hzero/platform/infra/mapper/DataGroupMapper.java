package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.DataGroup;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 数据组定义Mapper
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupMapper extends BaseMapper<DataGroup> {

    /**
     * 查询数据组列表
     * @param dataGroup 查询条件
     * @return
     */
    List<DataGroup> listDataGroup(DataGroup dataGroup);
}
