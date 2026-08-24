package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.DataGroup;
import org.hzero.platform.domain.entity.DataGroupDtl;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 数据组明细定义Mapper
 *
 * @author jianbo.li@hand-china.com 2019-07-17 15:48:48
 */
public interface DataGroupDtlMapper extends BaseMapper<DataGroupDtl> {
    /**
     *  查询数据分组明细
     * @param dataGroupDtl 查询条件
     * @return
     */
    List<DataGroupDtl> listDataGroupDtl(DataGroupDtl dataGroupDtl);
}
