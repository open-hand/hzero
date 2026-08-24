package org.hzero.platform.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.mybatis.base.BaseRepository;
import org.hzero.platform.domain.entity.FormLine;

import java.util.List;

/**
 * 表单配置行资源库
 *
 * @author xiaoyu.zhao@hand-china.com 2019-07-11 17:50:08
 */
public interface FormLineRepository extends BaseRepository<FormLine> {

    /**
     * 分页查询表单配置行信息
     *
     * @param pageRequest 分页参数
     * @param formLine    查询条件
     * @return Page<FormLine> 分页查询结果
     */
    Page<FormLine> pageFormLines(PageRequest pageRequest, FormLine formLine);

    /**
     * 校验重复性
     *
     * @param formLine 校验数据
     */
    void checkRepeat(FormLine formLine);
}
