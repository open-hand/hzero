package org.hzero.boot.imported.app.service;

import java.util.Map;

import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.TemplatePage;
import org.hzero.boot.imported.domain.vo.DataContext;
import org.hzero.core.convert.CommonConverter;

/**
 * 校验器
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/05 19:10
 */
public abstract class ValidatorHandler {

    /**
     * 数据对象
     */
    private final ThreadLocal<ImportData> threadContext = new ThreadLocal<>();

    /**
     * @deprecated Non-thread safe method, getContext() method is recommended.You can also use the addErrorMsg() method directly
     */
    @Deprecated
    protected DataContext context;

    /**
     * 自定义参数
     */
    private final ThreadLocal<Map<String, Object>> args = new ThreadLocal<>();
    /**
     * 导入目标
     */
    private final ThreadLocal<TemplatePage> templatePage = new ThreadLocal<>();


    public ImportData getContext() {
        return threadContext.get();
    }

    public DataContext getDataContext() {
        return this.context;
    }

    public ValidatorHandler setContext(ImportData context) {
        this.threadContext.set(context);
        this.context = CommonConverter.beanConvert(DataContext.class, context);
        return this;
    }

    public void addErrorMsg(String msg) {
        threadContext.get().addErrorMsg(msg);
    }

    public void addBackInfo(String backInfo) {
        threadContext.get().addBackInfo(backInfo);
    }

    public Map<String, Object> getArgs() {
        return args.get();
    }

    public ValidatorHandler setArgs(Map<String, Object> args) {
        this.args.set(args);
        return this;
    }

    public TemplatePage getTemplatePage() {
        return templatePage.get();
    }

    public ValidatorHandler setTemplatePage(TemplatePage templatePage) {
        this.templatePage.set(templatePage);
        return this;
    }

    public void clear() {
        threadContext.remove();
        args.remove();
        templatePage.remove();
    }

    /**
     * 自定义校验
     *
     * @param data 数据
     * @return 是否校验成功
     */
    public abstract boolean validate(String data);
}
