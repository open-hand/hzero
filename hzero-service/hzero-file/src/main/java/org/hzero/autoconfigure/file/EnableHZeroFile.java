package org.hzero.autoconfigure.file;

import java.lang.annotation.*;

import org.springframework.context.annotation.Import;

/**
 * <p>
 * 引入HZero File 包
 * </p>
 *
 * @author qingsheng.chen 2018/11/10 星期六 11:18
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Import(FileAutoConfiguration.class)
public @interface EnableHZeroFile {
}
