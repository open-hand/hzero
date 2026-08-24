package org.hzero.iam.domain.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.domain.entity.Label;

import java.util.List;
import java.util.Set;

/**
 * 标签领域Service
 *
 * @author xiaoyu.zhao@hand-china.com
 */
public interface LabelDomainService {

    /**
     * 分页查询标签信息列表
     *
     * @param pageRequest 分页参数
     * @param label       查询条件
     * @return 查询结果
     */
    Page<Label> pageLabelList(PageRequest pageRequest, Label label);

    /**
     * 根据主键查询标签详情信息
     *
     * @param id 主键Id
     * @return 查询结果
     */
    Label getLabelDetail(Long id);

    /**
     * 新增标签内容
     *
     * @param label 新增对象
     * @return 新增结果
     */
    Label createLabel(Label label);

    /**
     * 修改标签内容
     *
     * @param label 修改后的标签内容
     * @return 标签修改结果
     */
    Label updateLabel(Label label);

    /**
     * 根据标签类型获取标签列表
     *
     * @param type  标签类型
     * @return 满足条件的标签
     */
    List<Label> getLabelListByType(String type);

    /**
     * 根据标签编码和标签类型查询
     *
     * @param type      标签类型
     * @param labelName 标签名称
     * @return 满足条件的标签
     */
    List<Label> getLabelListByTypeAndNames(String type, String[] labelName);

    /**
     * 根据标签编码和标签类型查询
     *
     * @param type 标签的类型
     * @param tag  标签的tag
     * @return 满足条件的标签名称
     */
    Set<String> getLabelListByTypeAndTag(String type, String tag);
}
