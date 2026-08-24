package org.hzero.iam.domain.repository;

import io.choerodon.core.domain.Page;
import io.choerodon.mybatis.pagehelper.domain.PageRequest;
import org.hzero.iam.api.dto.PermissionFieldResponse;
import org.hzero.iam.domain.entity.Field;
import org.hzero.mybatis.base.BaseRepository;

import java.util.List;

/**
 * 接口字段维护资源库
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
public interface FieldRepository extends BaseRepository<Field> {

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
     * 根据字段ID查询接口
     *
     * @param fieldId 字段ID
     * @return 字段
     */
    Field queryField(long fieldId);

    /**
     * 根据字段ID批量查询接口
     *
     * @param fieldIds 字段ID列表
     * @return 字段列表
     */
    List<Field> listField(List<Long> fieldIds);

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
}
