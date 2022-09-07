package io.choerodon.core.base;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import org.hzero.core.base.BaseAppService;

/**
 * @author superleader8@gmail.com
 */
public class BaseController extends BaseAppService {

    /**
     *  控制层请求String字符串去除前后空格
     *  主要用于get搜索接口请求参数
     *  post/put请求对象里面的String字段无法操作
     * @param binder    视图数据绑定器
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmer);
    }
}
