package org.hzero.iam.infra.mapper;

import java.util.List;
import java.util.Set;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.Label;

/**
 * Mapper
 *
 * @author xiaoyu.zhao@hand-china.com 2020-02-25 14:22:16
 */
public interface LabelMapper extends BaseMapper<Label> {

    /**
     * 根据条件查询标签列表
     *
     * @param label 查询条件
     * @return 返回结果
     */
    List<Label> pageLabelList(Label label);

    /**
     * 根据标签类型获取标签列表
     *
     * @param type 标签类型
     * @return 标签数据
     */
    List<Label> getLabelListByType(@Param("type") String type);

    /**
     * 根据标签编码和标签类型查询
     */
    List<Label> getLabelListByTypeAndNames(@Param("type") String type, @Param("labelName") String[] labelName);

    int countRoleTplLabel(@Param("list") Set<Long> ids);
}
