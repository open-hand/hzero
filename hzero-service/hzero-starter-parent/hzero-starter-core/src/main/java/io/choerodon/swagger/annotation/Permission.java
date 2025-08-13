package io.choerodon.swagger.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import io.swagger.annotations.Api;
import org.springframework.http.HttpMethod;

import io.choerodon.core.iam.ResourceLevel;

import org.hzero.core.swagger.PermissionStatus;

/**
 * 权限定义注释
 * <p>
 * 加入权限状态配置
 *
 * @author xausky
 * @author bojiangzhou
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {

    /**
     * API编码，默认为 {@link Api#tags()} + 方法名 组成 API编码
     *
     * @return 权限编码
     */
    String code() default "";

    /**
     * 角色
     * 
     * @return 角色数组
     */
    String[] roles() default {};

    /**
     * 级别
     * 
     * @return 级别
     */
    ResourceLevel level() default ResourceLevel.PROJECT;

    /**
     * 登陆后即可拥有的权限
     * 
     * @return 是否登录可访问接口
     */
    boolean permissionLogin() default false;

    /**
     * 公共权限，不需要登录就可访问
     * 
     * @return 是否为公开接口
     */
    boolean permissionPublic() default false;

    /**
     * 只能服务内部调用，不能通过网关访问
     * 
     * @return 是否为内部接口
     */
    boolean permissionWithin() default false;

    /**
     * 需验证签名才可访问的接口
     *
     * @return 是否为签名接口
     */
    boolean permissionSign() default false;

    /**
     * API标签
     *
     * @see ApiTag
     * @return API标签
     */
    String[] tags() default {};

    /**
     * 接口变更状态，默认未做任何变更 <p></p>
     * 如：<i>/v1/xx</i> 升级至 <i>/v2/xx</i>，需在 <i>/v2/xx</i> 上配置 status = {@link PermissionStatus#UPGRADE}，并配置原API
     *
     * @return 变更状态
     * @see Permission#upgradeApiPath()
     * @see Permission#upgradeApiMethod()
     */
    PermissionStatus[] status() default { PermissionStatus.NONE };

    /**
     * 升级的API
     */
    String upgradeApiPath() default "";

    /**
     * 升级的API方法
     */
    HttpMethod upgradeApiMethod() default HttpMethod.GET;

}
