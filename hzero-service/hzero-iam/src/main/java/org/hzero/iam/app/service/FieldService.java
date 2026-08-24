package org.hzero.iam.app.service;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.PermissionFieldResponse;
import org.hzero.iam.domain.entity.Field;

import java.util.List;

/**
 * 接口字段维护应用服务
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
public interface FieldService {

    /**
     * 分页查询接口列表
     *
     * @param serviceName 服务名称
     * @param method      请求方式
     * @param path        请求路径
     * @param description 请求描述
     * @param includeAll  默认查询的的API是维护了字段的
     * @param roleId      角色ID，只筛选该角色能够查看的接口
     * @param userId      用户ID，只筛选该用户能够查看的接口
     * @param pageRequest 分页条件
     * @return 接口列表
     */
    Page<PermissionFieldResponse> pageApi(String serviceName, String method, String path, String description, boolean includeAll, Long roleId, Long userId, PageRequest pageRequest);

    /**
     * 分页查询接口字段
     *
     * @param permissionId     接口ID
     * @param fieldName        字段名称
     * @param fieldType        字段类型
     * @param fieldDescription 字段描述
     * @param pageRequest      分页条件
     * @return 字段列表
     */
    Page<Field> pageField(long permissionId, String fieldName, String fieldType, String fieldDescription, PageRequest pageRequest);

    /**
     * 新建字段维护
     *
     * @param field 字段维护内容
     * @return 字段维护
     */
    Field createField(Field field);

    /**
     * 批量新建字段维护
     *
     * @param fieldList 字段维护内容列表
     * @return 字段维护内容列表
     */
    List<Field> createField(List<Field> fieldList);

    /**
     * 更新字段维护
     *
     * @param field 字段维护内容
     * @return 字段维护内容
     */
    Field updateField(Field field);

    /**
     * 批量更新字段维护
     *
     * @param fieldList 字段维护内容列表
     * @return 字段维护内容列表
     */
    List<Field> updateField(List<Field> fieldList);

    /**
     * 删除字段维护
     *
     * @param field 字段维护
     */
    void deleteField(Field field);

    /**
     * 批量删除字段维护
     *
     * @param fieldList 字段维护列表
     */
    void deleteField(List<Field> fieldList);
}
