package org.hzero.platform.infra.annocations;

import java.lang.annotation.*;

import org.springframework.stereotype.Component;

/**
 * 数据源类型
 *
 * @author xiaoyu.zhao@hand-china.com
 */
@Inherited
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DatasourceType {

    /**
     * 数据源类型
     *
     * @return 类型
     */
    String[] value();

}
