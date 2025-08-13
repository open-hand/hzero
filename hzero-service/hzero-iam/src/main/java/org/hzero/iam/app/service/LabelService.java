package org.hzero.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.domain.entity.Label;

import java.util.List;

/**
 * 应用服务
 *
 * @author xiaoyu.zhao@hand-china.com 2020-02-25 14:22:16
 */
public interface LabelService {

    /**
     * 分页获取标签列表
     *
     * @param pageRequest 分页参数
     * @param label       标签对象查询条件
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
     * @param type 标签类型
     * @return 满足条件的标签
     */
    List<Label> getLabelListByType(String type);

    /**
     * 获取API标签标记值
     */
    String[] getApiTagSign();
}
