package org.hzero.boot.iam.field.handler;

import org.hzero.boot.iam.field.dto.FieldPermission;

import java.util.List;

/**
 * 字段权限处理接口
 *
 * @author qingsheng.chen@hand-china.com
 */
public interface FieldPermissionHandler {

    /**
     * 字段权限处理排序，返回越小越早执行
     *
     * @return 执行顺序
     */
    default int getOrder() {
        return 0;
    }

    /**
     * 入参拦截处理
     *
     * @param fieldPermissionList 字段权限列表
     * @param args                入参
     */
    default void beforeProcess(List<FieldPermission> fieldPermissionList, Object[] args) {

    }

    /**
     * 响应结果拦截处理
     *
     * @param fieldPermissionList 字段权限列表
     * @param arg                 返回结果
     */
    default void afterProcess(List<FieldPermission> fieldPermissionList, Object arg) {

    }
}
