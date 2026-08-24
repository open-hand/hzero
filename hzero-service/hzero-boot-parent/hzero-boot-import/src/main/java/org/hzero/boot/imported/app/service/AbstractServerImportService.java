package org.hzero.boot.imported.app.service;

import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.boot.imported.domain.entity.TemplatePage;

/**
 * 批量导入处理抽象类
 *
 * @author xianzhi.chen@hand-china.com 2019年1月18日上午10:34:29
 */
public abstract class AbstractServerImportService implements IBatchImportService {

    protected Template template;

    public Template getTemplate() {
        return template;
    }

    public AbstractServerImportService setTemplate(Template template) {
        this.template = template;
        return this;
    }

    protected TemplatePage currentTemplatePage;

    public TemplatePage getCurrentTemplatePage() {
        return currentTemplatePage;
    }

    public AbstractServerImportService setCurrentTemplatePage(TemplatePage currentTemplatePage) {
        this.currentTemplatePage = currentTemplatePage;
        return this;
    }
}
