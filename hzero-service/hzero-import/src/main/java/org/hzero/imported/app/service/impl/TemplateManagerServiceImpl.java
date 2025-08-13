package org.hzero.imported.app.service.impl;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import org.hzero.boot.imported.app.service.TemplateClientService;
import org.hzero.boot.imported.domain.entity.Template;
import org.hzero.boot.imported.domain.entity.TemplateColumn;
import org.hzero.boot.imported.domain.entity.TemplatePage;
import org.hzero.boot.imported.infra.constant.HimpBootConstants;
import org.hzero.boot.imported.infra.redis.TemplateRedis;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.convert.CommonConverter;
import org.hzero.core.redis.RedisHelper;
import org.hzero.imported.app.service.TemplateHeaderService;
import org.hzero.imported.app.service.TemplateLineService;
import org.hzero.imported.app.service.TemplateManagerService;
import org.hzero.imported.domain.entity.TemplateHeader;
import org.hzero.imported.domain.entity.TemplateLine;
import org.hzero.imported.domain.entity.TemplateTarget;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author shuangfei.zhu@hand-china.com
 */
@Service
public class TemplateManagerServiceImpl implements TemplateManagerService {

    private final RedisHelper redisHelper;
    private final TemplateHeaderService headerService;
    private final TemplateLineService linesService;
    private final TemplateClientService templateClientService;

    @Autowired
    public TemplateManagerServiceImpl(RedisHelper redisHelper,
                                      TemplateHeaderService headerService,
                                      TemplateLineService linesService,
                                      TemplateClientService templateClientService) {
        this.redisHelper = redisHelper;
        this.headerService = headerService;
        this.linesService = linesService;
        this.templateClientService = templateClientService;
    }

    @Override
    public void exportExcel(Long tenantId, String templateCode, HttpServletResponse response) {
        templateClientService.exportExcel(() -> getTemplate(tenantId, templateCode), response);
    }

    @Override
    public void exportCsv(Long tenantId, String templateCode, HttpServletResponse response) {
        templateClientService.exportCsv(() -> getTemplate(tenantId, templateCode), response);
    }

    private Template getTemplate(Long tenantId, String templateCode) {
        TemplateHeader templateHeader = getTemplateInfoNoMulti(tenantId, templateCode);
        Template template = CommonConverter.beanConvert(Template.class, templateHeader);
        if (!CollectionUtils.isEmpty(templateHeader.getTemplateTargetList())) {
            template.setTemplatePageList(CommonConverter.listConverter(TemplatePage.class, templateHeader.getTemplateTargetList()));
            Map<Integer, TemplatePage> indexTemplatePageMap = template.getTemplatePageList().stream()
                    .collect(Collectors.toMap(TemplatePage::getSheetIndex, Function.identity()));
            for (TemplateTarget templateTarget : templateHeader.getTemplateTargetList()) {
                if (!CollectionUtils.isEmpty(templateTarget.getTemplateLineList())) {
                    indexTemplatePageMap.get(templateTarget.getSheetIndex())
                            .setTemplateColumnList(CommonConverter.listConverter(TemplateColumn.class, templateTarget.getTemplateLineList()));
                }
            }
        }
        template.setColumnNameByLang();
        return template;
    }

    @Override
    public TemplateHeader getTemplateInfo(Long tenantId, String templateCode) {
        TemplateHeader header = headerService.getTemplateHeader(templateCode, tenantId);
        if (header == null) {
            header = headerService.getTemplateHeader(templateCode, BaseConstants.DEFAULT_TENANT_ID);
        }
        if (header == null) {
            return null;
        }
        if (!CollectionUtils.isEmpty(header.getTemplateTargetList())) {
            Map<Long, List<TemplateLine>> targetLineMap = linesService.listTemplateLine(header.getId()).stream()
                    .collect(Collectors.groupingBy(TemplateLine::getTargetId));
            header.getTemplateTargetList().forEach(templateTarget -> {
                List<TemplateLine> templateLines = targetLineMap.get(templateTarget.getId());
                mergeTls(templateLines);
                templateTarget.setTemplateLineList(templateLines).setTenantId(tenantId);
            });
        }
        return header;
    }

    @Override
    public TemplateHeader getTemplateInfoNoMulti(Long tenantId, String templateCode) {
        TemplateHeader header = headerService.getTemplateHeader(templateCode, tenantId);
        if (header == null) {
            header = headerService.getTemplateHeader(templateCode, BaseConstants.DEFAULT_TENANT_ID);
        }
        if (header == null) {
            return null;
        }
        if (!CollectionUtils.isEmpty(header.getTemplateTargetList())) {
            Map<Long, List<TemplateLine>> targetLineMap = linesService.listTemplateLine(header.getId()).stream()
                    .collect(Collectors.groupingBy(TemplateLine::getTargetId));
            header.getTemplateTargetList().forEach(templateTarget ->
                    templateTarget.setTemplateLineList(targetLineMap.get(templateTarget.getId())).setTenantId(tenantId));
        }
        // 刷新缓存
        TemplateRedis.refreshCache(redisHelper, header.getTenantId(), header.getTemplateCode(), redisHelper.toJson(header));
        return header;
    }

    /**
     * 合并多语言列
     *
     * @param templateLines 模板行
     */
    private void mergeTls(List<TemplateLine> templateLines) {
        if (CollectionUtils.isEmpty(templateLines)) {
            return;
        }
        Map<String, TemplateLine> multiLines = new HashMap<>(16);
        Iterator<TemplateLine> iterator = templateLines.iterator();
        while (iterator.hasNext()) {
            TemplateLine templateLine = iterator.next();
            if (HimpBootConstants.ColumnType.MULTI.equals(templateLine.getColumnType())) {
                String columnCode = templateLine.getColumnCode();
                String code = columnCode.substring(0, columnCode.lastIndexOf(BaseConstants.Symbol.COLON));
                String lang = columnCode.substring(columnCode.lastIndexOf(BaseConstants.Symbol.COLON) + 1);
                multiLines.computeIfAbsent(code, k -> {
                    TemplateLine multiLine = new TemplateLine();
                    BeanUtils.copyProperties(templateLine.setColumnCode(code), multiLine);
                    multiLine.set_tls(new HashMap<>(16));
                    return multiLine;
                });
                Map<String, Map<String, String>> tls = multiLines.get(code).get_tls();
                tls.computeIfAbsent(code, k -> new HashMap<>(10)).put(lang, templateLine.getColumnName());
                iterator.remove();
            }
        }
        multiLines.forEach((code, templateLine) -> templateLines.add(templateLine));
        templateLines.sort(Comparator.comparing(TemplateLine::getColumnIndex));
    }
}
