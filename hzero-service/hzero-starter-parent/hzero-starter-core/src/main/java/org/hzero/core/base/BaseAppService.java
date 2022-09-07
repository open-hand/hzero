package org.hzero.core.base;

import io.choerodon.core.exception.ExceptionResponse;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;
import org.hzero.core.message.MessageAccessor;
import org.hzero.core.util.ValidUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Validator;
import java.util.Collection;
import java.util.Locale;

/**
 * 基础 AppService
 *
 * @author gaokuo.dai@hand-china.com 2018年6月22日下午5:13:50
 */
public class BaseAppService {
    @Autowired
    private Validator validator;

    /**
     * 验证单个对象
     *
     * @param object 验证对象
     * @param groups 验证分组
     * @param <T>    Bean 泛型
     */
    protected <T> void validObject(T object, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, object, groups);
    }

    /**
     * 验证单个对象
     *
     * @param object  验证对象
     * @param groups  验证分组
     * @param process 异常信息处理
     * @param <T>     Bean 泛型
     */
    protected <T> void validObject(T object, ValidUtils.ValidationResult process, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, object, process, groups);
    }

    /**
     * 迭代验证集合元素，使用默认异常信息处理
     *
     * @param collection Bean集合
     * @param groups     验证组
     * @param <T>        Bean 泛型
     */
    protected <T> void validList(Collection<T> collection, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, collection, groups);
    }

    /**
     * 迭代验证集合元素，使用默认异常信息处理
     *
     * @param collection Bean集合
     * @param groups     验证组
     * @param process    异常信息处理
     * @param <T>        Bean 泛型
     */
    protected <T> void validList(Collection<T> collection, ValidUtils.ValidationResult process, @SuppressWarnings("rawtypes") Class... groups) {
        ValidUtils.valid(validator, collection, process, groups);
    }

    /**
     * @param code 编码
     * @return 获取多语言消息
     */
    protected String getMessage(String code) {
        return MessageAccessor.getMessage(code, locale()).desc();
    }

    /**
     * @param code 编码
     * @param args 参数
     * @return 获取多语言消息
     */
    protected String getMessage(String code, Object ... args) {
        return MessageAccessor.getMessage(code, args, locale()).desc();
    }

    /**
     * 返回用户的语言类型
     *
     * @return Locale
     */
    protected Locale locale() {
        CustomUserDetails details = DetailsHelper.getUserDetails();
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        if (details != null && Locale.US.toString().equals(details.getLanguage())) {
            locale = Locale.US;
        }
        return locale;
    }

    /**
     * @param code 消息编码
     * @return ExceptionResponse
     */
    protected ExceptionResponse getExceptionResponse(String code) {
        return new ExceptionResponse(code);
    }

    /**
     * @param code 消息编码
     * @param args 参数
     * @return ExceptionResponse
     */
    protected ExceptionResponse getExceptionResponse(String code, Object ... args) {
        return new ExceptionResponse(MessageAccessor.getMessage(code, args));
    }
}
