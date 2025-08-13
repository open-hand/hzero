package org.hzero.iam.infra.mapper;

import io.choerodon.mybatis.common.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.hzero.iam.domain.entity.LabelRel;

import java.util.List;

/**
 * 标签关系表Mapper
 *
 * @author bo.he02@hand-china.com 2020-04-26 10:04:19
 */
public interface LabelRelMapper extends BaseMapper<LabelRel> {
    /**
     * 通过主键批量删除数据
     *
     * @param ids 主键s
     */
    void batchDeleteByIds(@Param("ids") List<Long> ids);
}
