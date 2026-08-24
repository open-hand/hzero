package org.hzero.platform.app.service;

import java.util.List;

import org.hzero.boot.platform.entity.dto.EntityColumnProcessType;
import org.hzero.platform.domain.entity.EntityColumn;
import org.hzero.platform.domain.entity.EntityTable;

/**
 * description
 *
 * @author xingxing.wu@hand-china.com 2019/07/19 15:01
 */
public interface EntityRegistOperateService {
    /**
     * 处理entityTable的新增
     *
     * @param entityTableList entityTable对象
     */
    void doEntityTableInsert(List<EntityTable> entityTableList);

    /**
     * 处理entityTable的删除
     *
     * @param entityTableList entityTable对象
     */
    void doEntityTableDelete(List<EntityTable> entityTableList);

    /**
     * 处理entityTable的修改
     *
     * @param entityTableList entityTable对象
     */
    void doEntityTableUpdate(List<EntityTable> entityTableList);

    /**
     * 处理entityColumn的增删改
     *
     * @param entityColumnList entityColumn对象
     * @param processType      操作类型
     */
    void doEntityColumnOperate(List<EntityColumn> entityColumnList, EntityColumnProcessType processType);
}
