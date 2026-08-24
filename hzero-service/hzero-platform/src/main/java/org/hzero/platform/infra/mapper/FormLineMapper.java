package org.hzero.platform.infra.mapper;

import org.hzero.platform.domain.entity.FormLine;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 表单配置行Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
public interface FormLineMapper extends BaseMapper<FormLine> {

    /**
     * 查询表单配置行信息
     *
     * @param formLine 查询条件
     * @return 查询结果
     */
    List<FormLine> selectFormLines(FormLine formLine);
}
