package io.choerodon.swagger.annotation;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author superlee
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CustomPageRequest {

    ApiImplicitParams apiImplicitParams() default @ApiImplicitParams({
            @ApiImplicitParam(name = "page", value = "page", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "size", value = "size", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "sort", value = "sort", allowMultiple = true, dataType = "string", paramType = "query")
    });


}
