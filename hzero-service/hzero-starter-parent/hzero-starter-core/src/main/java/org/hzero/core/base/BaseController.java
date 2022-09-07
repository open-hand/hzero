package org.hzero.core.base;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * 基础Controller
 *
 * @author gaokuo.dai@hand-china.com 2018年6月22日下午5:13:50
 */
public class BaseController extends BaseAppService {



    /**
     * 控制层请求String字符串去除前后空格 主要用于get搜索接口请求参数 post/put请求对象里面的String字段无法操作
     */
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        StringTrimmerEditor stringTrimmer = new StringTrimmerEditor(true);
        binder.registerCustomEditor(String.class, stringTrimmer);
    }

}
