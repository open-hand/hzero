package org.hzero.boot.imported.app.service;

import java.util.List;
import java.util.Map;

import org.hzero.boot.imported.domain.entity.ImportData;
import org.hzero.boot.imported.domain.entity.TemplatePage;

/**
 * 批量导入抽象类
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/13 14:39
 */
public abstract class BatchImportHandler implements IBatchImportService {

    /**
     * 数据对象
     */
    private final ThreadLocal<List<ImportData>> contextList = new ThreadLocal<>();
    /**
     * 自定义参数
     */
    private final ThreadLocal<Map<String, Object>> args = new ThreadLocal<>();
    /**
     * sheet页模板定义
     */
    private final ThreadLocal<TemplatePage> template = new ThreadLocal<>();

    public List<ImportData> getContextList() {
        return contextList.get();
    }

    public BatchImportHandler setContextList(List<ImportData> contextList) {
        this.contextList.set(contextList);
        return this;
    }

    public Map<String, Object> getArgs() {
        return args.get();
    }

    public BatchImportHandler setArgs(Map<String, Object> args) {
        this.args.set(args);
        return this;
    }

    public <T> T getArgs(String key) {
        //noinspection unchecked
        return (T) (args.get()).get(key);
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

    public void clear() {
        contextList.remove();
        args.remove();
        template.remove();
    }
}
