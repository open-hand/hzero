package org.hzero.platform.infra.mapper;

import org.apache.ibatis.annotations.Param;
import org.hzero.platform.domain.entity.FormHeader;
import io.choerodon.mybatis.common.BaseMapper;

import java.util.List;

/**
 * 表单配置头Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
public interface FormHeaderMapper extends BaseMapper<FormHeader> {

    /**
     * 查询表单头列表
     *
     * @param formHeader 查询条件
     * @return 列表参数
     */
    List<FormHeader> selectFormHeaders(FormHeader formHeader);

    /**
     * 查询表单详情列表
     *
     * @param formHeaderId 查询条件
     * @return FormHeader
     */
    FormHeader selectOneFormHeaderById(@Param("formHeaderId") Long formHeaderId);
}
