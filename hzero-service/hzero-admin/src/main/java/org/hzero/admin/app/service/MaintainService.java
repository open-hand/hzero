package org.hzero.admin.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.admin.domain.entity.Maintain;

import java.util.List;

/**
 * @author XCXCXCXCX
 * @date 2020/6/1 1:30 下午
 */
public interface MaintainService {
    /**
     * 更新运维状态，从UNUSED -> ACTIVE 或 从ACTIVE -> USED
     * @param maintainId
     */
    void updateState(Long maintainId, String from, String to);

    /**
     * 分页查询
     * @param setState
     * @param pageRequest
     * @return
     */
    Page<Maintain> page(PageRequest pageRequest, Maintain setState);

    /**
     * 根据主键查询
     * @param maintainId
     * @return
     */
    Maintain selectByPrimaryKey(Long maintainId);

    /**
     * 插入
     * @param maintain
     */
    void insertSelective(Maintain maintain);

    /**
     * 更新
     * @param maintain
     */
    void updateByPrimaryKeySelective(Maintain maintain);

    /**
     * 删除
     * @param maintainId
     */
    void deleteByPrimaryKey(Long maintainId);

    /**
     * 获取运维服务列表
     * @param maintainId
     * @return
     */
    List<String> getServices(Long maintainId);

}
