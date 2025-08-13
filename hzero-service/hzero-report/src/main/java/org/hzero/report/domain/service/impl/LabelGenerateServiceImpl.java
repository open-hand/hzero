package org.hzero.report.domain.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.report.domain.entity.Dataset;
import org.hzero.report.domain.entity.LabelParameter;
import org.hzero.report.domain.entity.LabelPrint;
import org.hzero.report.domain.entity.LabelTemplate;
import org.hzero.report.domain.repository.DatasetRepository;
import org.hzero.report.domain.repository.LabelParameterRepository;
import org.hzero.report.domain.repository.LabelPrintRepository;
import org.hzero.report.domain.repository.LabelTemplateRepository;
import org.hzero.report.domain.service.ILabelGenerateService;
import org.hzero.report.domain.service.IReportMetaService;
import org.hzero.report.infra.constant.HrptConstants;
import org.hzero.report.infra.constant.HrptMessageConstants;
import org.hzero.report.infra.engine.data.ReportDataSource;
import org.hzero.report.infra.engine.data.ReportSqlTemplate;
import org.hzero.report.infra.engine.query.QueryerFactory;
import org.hzero.report.infra.enums.LabelParamTypeEnum;
import org.hzero.report.infra.util.CodeUtils;
import org.hzero.report.infra.util.VelocityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * 标签生成领域服务实现类
 *
 * @author fanghan.liu 2019/12/04 14:10
 */
@Service
public class LabelGenerateServiceImpl implements ILabelGenerateService {

    private final LabelTemplateRepository labelTemplateRepository;
    private final LabelParameterRepository labelParameterRepository;
    private final LabelPrintRepository labelPrintRepository;
    private final DatasetRepository datasetRepository;
    private final IReportMetaService reportMetaService;

    public LabelGenerateServiceImpl(LabelTemplateRepository labelTemplateRepository,
                                    LabelParameterRepository labelParameterRepository,
                                    LabelPrintRepository labelPrintRepository,
                                    DatasetRepository datasetRepository,
                                    IReportMetaService reportMetaService) {
        this.labelTemplateRepository = labelTemplateRepository;
        this.labelParameterRepository = labelParameterRepository;
        this.labelPrintRepository = labelPrintRepository;
        this.datasetRepository = datasetRepository;
        this.reportMetaService = reportMetaService;
    }

    @Override
    public String generateLabel(String labelTemplateCode, HttpServletRequest request) {
        LabelTemplate labelTemplate = labelTemplateRepository.selectLabelTemplate(labelTemplateCode,
                DetailsHelper.getUserDetails().getRoleId(),
                DetailsHelper.getUserDetails().getTenantId(),
                LocalDateTime.now().toLocalDate());
        if (labelTemplate == null || StringUtils.isEmpty(labelTemplate.getTemplateContent())) {
            throw new CommonException(HrptMessageConstants.NO_CONTENT_OR_PERMISSION);
        }
        // 替换参数
        List<String> contentList = replaceAllContent(labelTemplate, request.getParameterMap());

        List<LabelParameter> labelParameters = labelParameterRepository.select(new LabelParameter()
                .setLabelTemplateId(labelTemplate.getLabelTemplateId()));
        List<Element> elementList = new ArrayList<>(BaseConstants.Digital.SIXTEEN);
        // 解析参数
        contentList.forEach(content -> {
            // 获取画布内的内容
            Document document = getCanvasContent(content);
            // 解析模板参数
            parseTemplateParam(labelParameters, document);
            elementList.add(document.getElementsByAttributeValue(HrptConstants.LabelAttribute.DATA_TYPE, HrptConstants.LabelAttribute.CANVAS).get(0));
        });
        Element div = generateLabelContentHtml(elementList, labelTemplate);
        addSlash(div);
        return div.toString();
    }


    @Override
    public String generateLabel(Long tenantId, String labelTemplateCode, HttpServletRequest request) {
        LabelTemplate labelTemplate = labelTemplateRepository.selectLabelTemplate(labelTemplateCode,
                DetailsHelper.getUserDetails().getRoleId(),
                tenantId == null ? DetailsHelper.getUserDetails().getTenantId() : tenantId,
                LocalDateTime.now().toLocalDate());
        if (labelTemplate == null || StringUtils.isEmpty(labelTemplate.getTemplateContent())) {
            throw new CommonException(HrptMessageConstants.NO_CONTENT_OR_PERMISSION);
        }
        // 替换参数
        List<String> contentList = replaceAllContent(labelTemplate, request.getParameterMap());

        List<LabelParameter> labelParameters = labelParameterRepository.select(new LabelParameter()
                .setLabelTemplateId(labelTemplate.getLabelTemplateId()));
        List<Element> elementList = new ArrayList<>(BaseConstants.Digital.SIXTEEN);
        // 解析参数
        contentList.forEach(content -> {
            // 获取画布内的内容
            Document document = getCanvasContent(content);
            // 解析模板参数
            parseTemplateParam(labelParameters, document);
            elementList.add(document.getElementsByAttributeValue(HrptConstants.LabelAttribute.DATA_TYPE, HrptConstants.LabelAttribute.CANVAS).get(0));
        });
        Element div = generateLabelContentHtml(elementList, labelTemplate);
        addSlash(div);
        return div.toString();
    }

    private void parseTemplateParam(List<LabelParameter> labelParameters, Document document) {
        labelParameters.forEach(param -> {
            LabelParamTypeEnum typeEnum = LabelParamTypeEnum.valueOf2(param.getParamTypeCode());
            switch (typeEnum) {
                case TEXT:
                    parseText(document, param);
                    break;
                case IMG:
                    parseImg(document, param);
                    break;
                case BAR_CODE:
                    parseBarCode(document, param);
                    break;
                case QR_CODE:
                    parseQrCode(document, param);
                    break;
                default:
                    break;
            }
        });
    }

    /**
     * 获取画布内容
     *
     * @param labelContent 标签模板参数替换后的内容
     * @return HTML节点
     */
    private Document getCanvasContent(String labelContent) {
        Document document = Jsoup.parse(labelContent);
        Element element = document.getElementsByAttributeValue(HrptConstants.LabelAttribute.DATA_TYPE,
                HrptConstants.LabelAttribute.CANVAS).get(0);
        element.attr(HrptConstants.LabelAttribute.STYLE, element.attr(HrptConstants.LabelAttribute.DATA_STYLE));
        return Jsoup.parse(element.toString());
    }


    @Override
    public void parseText(Document document, LabelParameter labelParameter) {
        // 获取标签
        Elements elements = document.getElementsByAttributeValue(HrptConstants.LabelAttribute.DATA_CODE,
                labelParameter.getParameterCode());
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        // 文字长度
        Integer textLength = labelParameter.getTextLength();
        // 最大行数
        final Integer maxRows = labelParameter.getMaxRows();
        elements.forEach(element -> {
            element.attr(HrptConstants.LabelAttribute.STYLE, element.attr(HrptConstants.LabelAttribute.DATA_STYLE));
            if (textLength == null) {
                return;
            }
            element.html(validateTextLength(textLength, element.attr(HrptConstants.LabelAttribute.DATA_VALUE)));
            element.html(validateTextLines(maxRows, element.html()));
        });
    }

    @Override
    public void parseImg(Document document, LabelParameter labelParameter) {
        // 获取标签
        Elements elements = document.getElementsByAttributeValue(HrptConstants.LabelAttribute.DATA_CODE,
                labelParameter.getParameterCode());
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        elements.forEach(element -> {
            element.tagName(HrptConstants.HtmlData.IMG);
            element.html(StringUtils.EMPTY);
            // 设置图片src 样式
            element.attr(HrptConstants.LabelAttribute.SRC, element.attr(HrptConstants.LabelAttribute.DATA_SRC));
            element.attr(HrptConstants.LabelAttribute.STYLE, element.attr(HrptConstants.LabelAttribute.DATA_STYLE));
        });

    }

    @Override
    public void parseBarCode(Document document, LabelParameter labelParameter) {
        // 获取标签
        Elements elements = document.getElementsByAttributeValue(HrptConstants.LabelAttribute.DATA_CODE,
                labelParameter.getParameterCode());
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        elements.forEach(element -> {
            try {
                byte[] bytes = CodeUtils.generateBarCode(element.attr(HrptConstants.LabelAttribute.DATA_VALUE),
                        Integer.parseInt(element.attr(HrptConstants.LabelAttribute.DATA_WIDTH)) * 5,
                        Integer.parseInt(element.attr(HrptConstants.LabelAttribute.DATA_HEIGHT)) * 5,
                        labelParameter.getCharacterEncoding(),
                        labelParameter.getBarCodeType());
                element.tagName(HrptConstants.HtmlData.IMG);
                element.html(StringUtils.EMPTY);
                element.attr(HrptConstants.LabelAttribute.SRC,
                        HrptConstants.LabelAttribute.CODE_SRC_SUFFIX + Base64.getEncoder().encodeToString(bytes));
                element.attr(HrptConstants.LabelAttribute.STYLE, element.attr(HrptConstants.LabelAttribute.DATA_STYLE));
            } catch (Exception e) {
                throw new CommonException(HrptMessageConstants.ERROR_GENERATE_BARCODE, e);
            }
        });
    }

    @Override
    public void parseQrCode(Document document, LabelParameter labelParameter) {
        // 获取标签
        Elements elements = document.getElementsByAttributeValue(HrptConstants.LabelAttribute.DATA_CODE,
                labelParameter.getParameterCode());
        if (CollectionUtils.isEmpty(elements)) {
            return;
        }
        elements.forEach(element -> {
            try {
                byte[] bytes = CodeUtils.generateQrCode(element.attr(HrptConstants.LabelAttribute.DATA_VALUE),
                        Integer.parseInt(element.attr(HrptConstants.LabelAttribute.DATA_WIDTH)) * 5,
                        Integer.parseInt(element.attr(HrptConstants.LabelAttribute.DATA_HEIGHT)) * 5,
                        labelParameter.getCharacterEncoding());
                element.tagName(HrptConstants.HtmlData.IMG);
                element.html(StringUtils.EMPTY);
                element.attr(HrptConstants.LabelAttribute.SRC,
                        HrptConstants.LabelAttribute.CODE_SRC_SUFFIX + Base64.getEncoder().encodeToString(bytes));
                element.attr(HrptConstants.LabelAttribute.STYLE, element.attr(HrptConstants.LabelAttribute.DATA_STYLE));
            } catch (Exception e) {
                throw new CommonException(HrptMessageConstants.ERROR_GENERATE_QRCODE, e);
            }
        });
    }

    /**
     * 校验文本长度，大于length添加换行符<br>
     *
     * @param length 文本设定长度
     * @param text   文本内容
     * @return 校验后的文本
     */
    private String validateTextLength(Integer length, String text) {
        if (text.contains(HrptConstants.HtmlData.NEW_LINE)) {
            // 文本已经包含<br />，拆成两部分去解析
            int endIndex = text.lastIndexOf(HrptConstants.HtmlData.NEW_LINE) + HrptConstants.HtmlData.NEW_LINE.length();
            String before = text.substring(BaseConstants.Digital.ZERO, endIndex);
            String after = text.substring(endIndex);
            if (length.compareTo(after.length()) < BaseConstants.Digital.ZERO) {
                String s = new StringBuilder(after).insert(length, HrptConstants.HtmlData.NEW_LINE).toString();
                return validateTextLength(length, before.concat(s));
            }
        } else if (length.compareTo(text.length()) < BaseConstants.Digital.ZERO) {
            text = new StringBuilder(text).insert(length, HrptConstants.HtmlData.NEW_LINE).toString();
            return validateTextLength(length, text);
        }
        return text;
    }

    /**
     * 校验文本最大行数
     *
     * @param maxRows 最大行数
     * @param text    文本内容
     * @return 校验后的文本
     */
    private String validateTextLines(Integer maxRows, String text) {
        if (maxRows == null) {
            return text;
        }
        int times = StringUtils.countMatches(text, HrptConstants.HtmlData.NEW_LINE);
        if (maxRows.compareTo(times) <= BaseConstants.Digital.ZERO) {
            int index = StringUtils.ordinalIndexOf(text, HrptConstants.HtmlData.NEW_LINE, maxRows);
            return new StringBuilder(text).substring(BaseConstants.Digital.ZERO, index);
        }
        return text;
    }

    /**
     * 替换要预览的所有标签参数
     *
     * @param labelTemplate 标签模板
     * @param parameterMap  查询参数
     * @return 模板正文
     */
    @SuppressWarnings("unchecked")
    private List<String> replaceAllContent(LabelTemplate labelTemplate, Map<?, ?> parameterMap) {
        Map<String, Object> formParameters = reportMetaService.getFormParameters(labelTemplate.getDatasetId(), parameterMap);
        Dataset dataset = datasetRepository.selectDataset(null, labelTemplate.getDatasetId());
        ReportDataSource dataSource = reportMetaService.getReportDataSource(labelTemplate.getTenantId(), dataset.getDatasourceCode());
        String sqlText = new ReportSqlTemplate(dataset.getSqlText(), formParameters).execute();
        String sqlType = Dataset.checkSqlType(sqlText);
        Map<String, Object> map = QueryerFactory.create(dataSource).getMetaDataMap(sqlType, sqlText);
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get(HrptConstants.DATA);
        String content = labelTemplate.getTemplateContent();
        List<String> contentList = new ArrayList<>(BaseConstants.Digital.SIXTEEN);
        if (CollectionUtils.isEmpty(list)) {
            return Collections.emptyList();
        } else {
            Object row = list.get(0).get(HrptConstants.DataXmlAttr.DEFAULT_ROW);
            if (row instanceof List) {
                for (Map<String, Object> param : (List<Map<String, Object>>) row) {
                    contentList.add(VelocityUtils.parse(content, param));
                }
                return contentList;
            }
            list.forEach(labelParam -> {
                contentList.add(VelocityUtils.parse(content, labelParam));
            });
            return contentList;
        }
    }

    /**
     * 添加闭合符号
     *
     * @param div html
     */
    private void addSlash(Element div) {
        Elements elements = div.getAllElements();
        elements.forEach(element -> {
            if (HrptConstants.HtmlData.IMG.equals(element.tagName()) || HrptConstants.HtmlData.BR.equals(element.tagName())) {
                element.attr(HrptConstants.HtmlData.SLASH, null);
            }
        });
    }

    /**
     * 生成标签完整内容
     *
     * @param elementList   单个标签html
     * @param labelTemplate 标签模板
     * @return 生成结果
     */
    private Element generateLabelContentHtml(List<Element> elementList, LabelTemplate labelTemplate) {
        // 获取打印配置
        LabelPrint labelPrint = labelPrintRepository.selectLabelPrintAttribute(labelTemplate.getTenantId(), labelTemplate.getTemplateCode());
        if (labelPrint == null) {
            // 默认样式
            Long paperWidth = HrptConstants.LabelAttribute.DEFAULT_WIDTH;
            Element div = new Element(HrptConstants.HtmlData.DIV);
            div.attr(HrptConstants.LabelAttribute.STYLE, HrptConstants.LabelAttribute.DEFAULT_DIV_STYLE + paperWidth + HrptConstants.HtmlData.MM);
            for (Element element : elementList) {
                // 设置高间距
                element.attr(HrptConstants.LabelAttribute.STYLE,
                        element.attr(HrptConstants.LabelAttribute.STYLE) + HrptConstants.LabelAttribute.DEFAULT_MARGIN_TOP);
                div.appendChild(element);
            }
            String result = HrptConstants.HtmlData.BODY_PREFIX + div + HrptConstants.HtmlData.BODY_SUFFIX;
            return Jsoup.parse(result);
        } else {
            // 纸张宽度
            long paperWidth = labelPrint.getPaperWidth();
            // 宽个数 必输
            Long wideQty = labelPrint.getWideQty();
            // 高个数
            Long highQty = labelPrint.getHighQty();
            // 高间距
            Long highSpace = labelPrint.getHighSpace() == null ? 0L : labelPrint.getHighSpace();
            // 纸张高度
            Long paperHigh = labelPrint.getPaperHigh();
            // 单页纸内容高度
            Long contentHigh = generatorPaperContentHight(labelTemplate.getTemplateHigh(), highQty, labelPrint.getMarginTop(), labelPrint.getMarginBottom(), highSpace);
            // 横向打印，交换纸张宽高
            if (labelPrint.getPrintDirection() == 0 && paperHigh != null) {
                paperWidth = paperHigh + paperWidth;
                paperHigh = paperWidth - paperHigh;
                paperWidth = paperWidth - paperHigh;
            }

            Element div = new Element(HrptConstants.HtmlData.DIV);
            // 纸张高度无限制
            if (paperHigh == null) {
                div = div.appendChild(generatorPaper(elementList, paperHigh, paperWidth, null, highSpace, wideQty, labelPrint, false));
            } else {
                // 纸张
                if (highQty == null || highQty < 1) {
                    // 计算一张纸最多能放的标签高个数
                    highQty = paperHigh / (labelTemplate.getTemplateHigh() + highSpace);
                    if (paperHigh % (labelTemplate.getTemplateWidth() + highSpace) > labelTemplate.getTemplateWidth()) {
                        highQty++;
                    }
                }
                div = div.appendChild(generatorMultiPaper(elementList, contentHigh, paperWidth, highQty, highSpace, wideQty, labelPrint));
            }
            String result = HrptConstants.HtmlData.BODY_PREFIX + div + HrptConstants.HtmlData.BODY_SUFFIX;
            return Jsoup.parse(result);
        }
    }

    private Element generatorMultiPaper(List<Element> elementList, Long paperHigh, Long paperWidth, Long highQty, Long highSpace, Long wideQty, LabelPrint labelPrint) {
        Element papers = new Element(HrptConstants.HtmlData.DIV);
        // 计算一张纸能放的个数
        long paperTotal = highQty * wideQty;
        // 计算纸张数量
        long paperQuantity;
        if (elementList.size() < paperTotal) {
            papers = papers.appendChild(generatorPaper(elementList, paperHigh, paperWidth, highQty, highSpace, wideQty, labelPrint, false));
        }
        paperQuantity = elementList.size() / paperTotal;
        paperQuantity = elementList.size() % paperTotal > 0 ? paperQuantity + 1 : paperQuantity;
        for (int i = 0; i < paperQuantity; i++) {
            if (i == paperQuantity - 1) {
                papers = papers.appendChild(generatorPaper(elementList, paperHigh, paperWidth, highQty, highSpace, wideQty, labelPrint, false));
                continue;
            }
            papers = papers.appendChild(generatorPaper(elementList, paperHigh, paperWidth, highQty, highSpace, wideQty, labelPrint, true));
        }
        return papers;
    }

    private Element generatorPaper(List<Element> elementList, Long paperHigh, Long paperWidth, Long highQty, Long highSpace, Long wideQty, LabelPrint labelPrint, boolean paperBreak) {
        Element paper = new Element(HrptConstants.HtmlData.DIV);
        paper.attr(HrptConstants.LabelAttribute.STYLE,
                HrptConstants.LabelAttribute.WIDTH + paperWidth + HrptConstants.HtmlData.MM +
                        (paperHigh != null ?
                                HrptConstants.LabelAttribute.HEIGHT + paperHigh + HrptConstants.HtmlData.MM +
                                        (paperBreak ? HrptConstants.LabelAttribute.PAGE_BREAK_AFTER : StringUtils.EMPTY) : StringUtils.EMPTY));
        Element realDiv = new Element(HrptConstants.HtmlData.DIV);
        realDiv.attr(HrptConstants.LabelAttribute.STYLE, generatorMarginStyle(labelPrint));
        paper.appendChild(realDiv);
        if (highQty == null) {
            // 行数量
            int total = (int) (elementList.size() / wideQty);
            total = elementList.size() % wideQty > 0 ? total + 1 : total;
            for (int i = 0; i < total; i++) {
                realDiv = realDiv.appendChild(generatorLabelLine(elementList, highSpace, wideQty));
            }
        } else {
            for (int i = 0; i < highQty; i++) {
                realDiv = realDiv.appendChild(generatorLabelLine(elementList, highSpace, wideQty));
            }
        }
        return paper;
    }

    /**
     * 生成一行标签
     */
    private Element generatorLabelLine(List<Element> elementList, Long highSpace, Long wideQty) {
        if (CollectionUtils.isEmpty(elementList)) {
            return new Element(HrptConstants.HtmlData.DIV);
        }
        Element lineDiv = new Element(HrptConstants.HtmlData.DIV);
        for (int i = 0; i < wideQty; i++) {
            Element element = elementList.get(0);
            Element body;
            try {
                body = Jsoup.parse(element.html()).body().child(0);
            } catch (Exception e) {
                String html = Jsoup.parse(element.html()).body().html();
                body = new Element(HrptConstants.HtmlData.DIV).append(html);
            }
            body.attr(HrptConstants.LabelAttribute.STYLE,
                    body.attr(HrptConstants.LabelAttribute.STYLE) +
                            HrptConstants.LabelAttribute.DISPLAY_INLINE_TABLE +
                            HrptConstants.LabelAttribute.MARGIN_TOP_PREFIX +
                            highSpace +
                            HrptConstants.LabelAttribute.MM);
            lineDiv.appendChild(body);
            elementList.remove(element);
            if (CollectionUtils.isEmpty(elementList)) {
                return lineDiv;
            }
        }
        return lineDiv;
    }

    private String generatorMarginStyle(LabelPrint labelPrint) {
        Long marginLeft = labelPrint.getMarginLeft();
        Long marginRight = labelPrint.getMarginRight();
        Long marginTop = labelPrint.getMarginTop();
        Long marginBottom = labelPrint.getMarginBottom();
        String style = HrptConstants.LabelAttribute.PADDING_LEFT_PREFIX + marginLeft + HrptConstants.LabelAttribute.MM +
                HrptConstants.LabelAttribute.PADDING_RIGHT_PREFIX + marginRight + HrptConstants.LabelAttribute.MM +
                HrptConstants.LabelAttribute.PADDING_TOP_PREFIX + marginTop + HrptConstants.LabelAttribute.MM;
        if (marginBottom != null) {
            style = style + HrptConstants.LabelAttribute.PADDING_BOTTOM_PREFIX + marginBottom + HrptConstants.LabelAttribute.MM;
        }
        return style;
    }

    private Long generatorPaperContentHight(Integer templateHigh, Long highQty, Long marginTop, Long marginBottom, Long highSpace) {
        if (highQty == null) {
            return null;
        } else {
            return templateHigh * highQty + marginBottom + marginTop + (highQty - 1) * highSpace;
        }
    }

    /**
     * 将毫米转成像素
     *
     * @param length 毫米长度
     * @return 像素长度
     */
    private Long covertLengthToSize(Long length) {
        // 以1/72英寸为单位 纸张宽250mm 则paperpaperWidth为 25cm/2.54*72
        BigDecimal bigDecimal = new BigDecimal(length);
        return bigDecimal.multiply(new BigDecimal(72)).divide(new BigDecimal("25.4"), 0).longValue();
    }
}
