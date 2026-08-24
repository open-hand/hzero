package org.hzero.iam.infra.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.choerodon.mybatis.common.BaseMapper;

import org.hzero.iam.domain.entity.Field;

/**
 * 接口字段维护Mapper
 *
 * @author qingsheng.chen@hand-china.com 2019-07-09 14:32:17
 */
public interface FieldMapper extends BaseMapper<Field> {
    /**
     * 查询接口字段
     *
     * @param permissionId     接口ID
     * @param fieldName        字段名称
     * @param fieldType        字段类型
     * @param fieldDescription 字段描述
     * @return 字段列表
     */
    List<Field> listField(@Param("permissionId") long permissionId,
                          @Param("fieldName") String fieldName,
                          @Param("fieldType") String fieldType,
                          @Param("fieldDescription") String fieldDescription);

    /**
     * 根据字段ID批量查询接口
     *
     * @param fieldIds 字段ID列表
     * @return 字段列表
     */
    List<Field> listFieldByPrimaryKeys(@Param("fieldIds") List<Long> fieldIds);

    /**
     * 根据字段ID查询接口
     *
     * @param fieldId 字段ID
     * @return 字段
     */
    Field queryField(@Param("fieldId") long fieldId);

    /**
     * 查询安全组中可添加的接口字段（过滤已经分配的）
     *
     * @param permissionId     接口ID
     * @param fieldName        字段名称
     * @param fieldType        字段类型
     * @param fieldDescription 字段描述
     * @return 字段列表
     */
    List<Field> listSecGrpAssignableField(@Param("secGrpId") long secGrpId,
                          @Param("permissionId") long permissionId,
                          @Param("fieldName") String fieldName,
                          @Param("fieldType") String fieldType,
                          @Param("fieldDescription") String fieldDescription);
}
