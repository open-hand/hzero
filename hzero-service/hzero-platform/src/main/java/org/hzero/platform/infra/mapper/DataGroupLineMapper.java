package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.DataGroup;
import org.hzero.platform.domain.entity.DataGroupLine;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 数据组行定义Mapper
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupLineMapper extends BaseMapper<DataGroupLine> {
    /**
     * 查询数据组行列表
     * @param dataGroupLine 查询条件
     * @return
     */
    List<DataGroupLine> listDataGroupLine(DataGroupLine dataGroupLine);
}
