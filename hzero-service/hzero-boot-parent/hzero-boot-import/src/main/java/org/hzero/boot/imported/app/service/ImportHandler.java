package org.hzero.boot.imported.app.service;

import java.util.Map;

import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.TemplatePage;

/**
 * 单条导入抽象类
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/13 14:38
 */
public abstract class ImportHandler implements IDoImportService {

    /**
     * 数据对象
     */
    private final ThreadLocal<ImportData> context = new ThreadLocal<>();
    /**
     * 自定义参数
     */
    private final ThreadLocal<Map<String, Object>> args = new ThreadLocal<>();
    /**
     * sheet页模板定义
     */
    private final ThreadLocal<TemplatePage> template = new ThreadLocal<>();

    public ImportData getContext() {
        return context.get();
    }

    public ImportHandler setContext(ImportData context) {
        this.context.set(context);
        return this;
    }

    public Map<String, Object> getArgs() {
        return args.get();
    }

    public <T> T getArgs(String key) {
        //noinspection unchecked
        return (T) (args.get()).get(key);
    }

    public ImportHandler setArgs(Map<String, Object> args) {
        this.args.set(args);
        return this;
    }

    public void putArgs(String key, Object value) {
        Map<String, Object> map = args.get();
        map.put(key, value);
        this.args.set(map);
    }

    public TemplatePage getTemplate() {
        return template.get();
    }

    public void setTemplate(TemplatePage templatePage) {
        template.set(templatePage);
    }

    public void addErrorMsg(String msg) {
        context.get().addErrorMsg(msg);
    }

    public void addBackInfo(String backInfo) {
        context.get().addBackInfo(backInfo);
    }

    public void clear() {
        context.remove();
        args.remove();
        template.remove();
    }
}
