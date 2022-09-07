package org.hzero.export.filler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Comment;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.hzero.boot.platform.lov.adapter.LovAdapter;
import org.hzero.boot.platform.lov.dto.LovValueDTO;
import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;
import org.hzero.core.util.SecurityUtils;
import org.hzero.export.IExcelFiller;
import org.hzero.export.annotation.*;
import org.hzero.export.render.ValueRenderer;
import org.hzero.export.vo.ExportColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

/**
 * Excel 数据填充器V2
 *
 * @author xcxcxcxcx
 */
public abstract class ExcelFiller implements IExcelFiller {

    /**
     * SXSS的缓存窗口值
     */
    protected static final int ROW_ACCESS_WINDOW_SIZE = 100;

    private final Map<Class<? extends ValueRenderer>, ValueRenderer> renderers = new ConcurrentHashMap<>();
    private final Map<Pair<ExcelColumn, SXSSFWorkbook>, CellStyle> styleMap = new ConcurrentHashMap<>();
    private final Map<Pair<ExcelColumn, SXSSFWorkbook>, Font> fontMap = new ConcurrentHashMap<>();
    private final Map<Pair<ExcelColumn, SXSSFWorkbook>, Comment> commentMap = new ConcurrentHashMap<>();
    private volatile DataFormat dataFormat;

    private final ZoneId zoneId = ZoneId.systemDefault();

    protected Logger logger = LoggerFactory.getLogger(ExcelFiller.class);

    private final List<SXSSFWorkbook> workbooks = new ArrayList<>();

    private ExportColumn root;
    private Pattern sheetNamePattern;

    /**
     * 标题样式
     */
    protected CellStyle titleCellStyle;

    /**
     * 单个excel的最大sheet数
     */
    private Integer singleExcelMaxSheetNum = 5;
    /**
     * 单个sheet页的最大记录数
     */
    protected Integer singleSheetMaxRow = 1000000;
    /**
     * 单个excel的最大记录数
     */
    private Integer singleExcelMaxRow = singleExcelMaxSheetNum * singleSheetMaxRow;

    private int remainCapacityOfExcel;
    private int remainCapacityOfSheet;

    private int workbookIndex;
    private int sheetIndex;

    public ExcelFiller() {
    }

    public ExcelFiller(ExportColumn root) {
        this.root = root;
        String title = StringUtils.defaultIfBlank(getRootExportColumn().getTitle(), getRootExportColumn().getName());
        this.sheetNamePattern = Pattern.compile(title + "-[0-9]+");
    }

    @Override
    public void configure(int singleExcelMaxSheetNum, int singleSheetMaxRow) {
        this.singleExcelMaxSheetNum = singleExcelMaxSheetNum;
        this.singleSheetMaxRow = singleSheetMaxRow;
        this.singleExcelMaxRow = singleExcelMaxSheetNum * singleSheetMaxRow;
    }

    /**
     * @return 填充器类型名称
     */
    @Override
    public abstract String getFillerType();

    /**
     * 创建标题
     */
    public abstract void createSheetAndTitle0(SXSSFWorkbook workbook);

    @Override
    public void fillTitle() {
        if (CollectionUtils.isEmpty(workbooks)) {
            workbooks.add(new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE));
        }
        createSheetAndTitle0(workbooks.get(0));
    }

    /**
     * 填充数据, 自动扩容，自动填充标题
     *
     * @param data 数据
     */
    @Override
    public void fillData(List<?> data) {
        int append = data.size();
        List<SXSSFWorkbook> workbooks = getAppendWriteExcels(append);
        List<List<?>> shardData = shardData(getRemainCapacityOfExcel(), singleExcelMaxRow, data);
        fillExcelData(workbooks, shardData);
    }

    private void resetRemainValue(int append) {
        int oldRemainCapacityOfExcel = remainCapacityOfExcel;
        int oldRemainCapacityOfSheet = remainCapacityOfSheet;
        if (append > oldRemainCapacityOfExcel) {
            append = append - oldRemainCapacityOfExcel;
            remainCapacityOfExcel = (singleExcelMaxRow - append % singleExcelMaxRow) % singleExcelMaxRow;
            remainCapacityOfSheet = (singleSheetMaxRow - append % singleSheetMaxRow) % singleSheetMaxRow;
        } else {
            remainCapacityOfExcel = oldRemainCapacityOfExcel - append;
            if (append > oldRemainCapacityOfSheet) {
                remainCapacityOfSheet = (singleSheetMaxRow - (append - oldRemainCapacityOfSheet) % singleSheetMaxRow) % singleSheetMaxRow;
            } else {
                remainCapacityOfSheet = oldRemainCapacityOfSheet - append;
            }
        }
        if (append > oldRemainCapacityOfSheet) {
            sheetIndex = sheetIndex + (int) Math.floor((double) (append - oldRemainCapacityOfSheet) / singleSheetMaxRow);
        } else if (append == oldRemainCapacityOfSheet && append > 0) {
            sheetIndex = sheetIndex + 1;
        }
        workbookIndex = workbookIndex + (int) Math.floor((double) sheetIndex / singleExcelMaxSheetNum);
        sheetIndex = sheetIndex % singleExcelMaxSheetNum;
    }

    private void fillExcelData(List<SXSSFWorkbook> workbooks, List<List<?>> shardData) {
        Assert.isTrue(workbooks.size() == shardData.size(), "workbooks.size() != shardData.size()");
        for (int i = 0; i < workbooks.size(); i++) {
            SXSSFWorkbook workbook = workbooks.get(i);
            List<?> data = shardData.get(i);
            fillExcelData(workbook, data);
        }
    }

    private void fillExcelData(SXSSFWorkbook workbook, List<?> data) {
        int append = data.size();
        List<SXSSFSheet> sheets = getAppendWriteSheets(workbook, append);
        List<List<?>> shardData = shardData(getRemainCapacityOfSheet(), singleSheetMaxRow, data);
        fillSheetData(sheets, shardData);
    }

    protected void fillSheetData(List<SXSSFSheet> sheets, List<List<?>> shardData) {
        Assert.isTrue(sheets.size() == shardData.size(), "sheets.size() != shardData.size()");
        for (int i = 0; i < sheets.size(); i++) {
            SXSSFSheet sheet = sheets.get(i);
            List<?> data = shardData.get(i);
            fillData0(sheet, data);
            resetRemainValue(data.size());
        }
    }

    private List<List<?>> shardData(int firstSize, int shardSize, List<?> data) {
        int size = data.size();
        if (firstSize >= size) {
            return Collections.singletonList(data);
        } else {
            List<List<?>> result = new ArrayList<>();
            int remain = size - firstSize;
            if (firstSize > 0) {
                result.add(data.subList(0, firstSize));
            }
            int shardNum = (int) Math.ceil((double) remain / shardSize);
            for (int i = 0; i < shardNum; i++) {
                result.add(data.subList(firstSize + shardSize * i, Math.min(firstSize + shardSize * (i + 1), data.size())));
            }
            return result;
        }
    }

    private List<SXSSFWorkbook> getAppendWriteExcels(int append) {
        int remainCapacity = getRemainCapacityOfExcel();
        if (remainCapacity < append) {
            //需要扩容
            int remainAppend = append - remainCapacity;
            int appendExcel = (int) Math.ceil((double) remainAppend / singleExcelMaxRow);
            excelExpansion(appendExcel);
        }
        return workbooks.subList(workbookIndex, workbooks.size());
    }

    private List<SXSSFSheet> getAppendWriteSheets(SXSSFWorkbook workbook, int append) {
        int remainCapacity = getRemainCapacityOfSheet();
        if (remainCapacity < append) {
            //需要扩容
            int remainAppend = append - remainCapacity;
            int appendExcel = (int) Math.ceil((double) remainAppend / singleSheetMaxRow);
            sheetExpansion(workbook, appendExcel);
        }
        return getSheets(workbook);
    }

    private List<SXSSFSheet> getSheets(SXSSFWorkbook workbook) {
        List<SXSSFSheet> sheets = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            // 过滤
            if (sheetNamePattern.matcher(workbook.getSheetAt(i).getSheetName()).matches()) {
                sheets.add(workbook.getSheetAt(i));
            }
        }
        return sheets.subList(sheetIndex, sheets.size());
    }

    private int getRemainCapacityOfExcel() {
        return remainCapacityOfExcel;
    }

    private int getRemainCapacityOfSheet() {
        return remainCapacityOfSheet;
    }

    private void excelExpansion(int append) {
        for (int i = 0; i < append; i++) {
            workbooks.add(new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE));
        }
    }

    private void sheetExpansion(SXSSFWorkbook workbook, int append) {
        int current = workbook.getNumberOfSheets();
        for (int i = 0; i < append; i++) {
            String title = root.getTitle();
            root.setTitle(title + "-" + (current + i + 1));
            createSheetAndTitle0(workbook);
            root.setTitle(title);
        }
    }

    /**
     * 填充数据
     *
     * @param sheet 导出sheet
     * @param data  数据
     */
    protected abstract void fillData0(SXSSFSheet sheet, List<?> data);

    @Override
    public ExportColumn getRootExportColumn() {
        return root;
    }

    /**
     * 填充单元格
     *
     * @param cell        SXSSFCell
     * @param value       数据
     * @param rowData     行数据
     * @param excelColumn 行数据配置属性
     */
    protected void fillCellValue(SXSSFWorkbook workbook, SXSSFCell cell, Object value, Object rowData, ExcelColumn excelColumn, boolean isTitle) {

        List<Class<? extends ValueRenderer>> renderers = isTitle ? null : (excelColumn == null ? null : Arrays.asList(excelColumn.renderers()));
        Class<? extends org.hzero.export.annotation.Comment> comment = excelColumn == null ? null : excelColumn.comment();

        CellStyle s = getCellStyle(workbook, excelColumn, isTitle);
        //标题无需添加注释
        if (!isTitle && comment != null) {
            setComment(workbook, cell, comment, excelColumn);
        }
        cell.setCellStyle(s);

        value = this.doRender(value, rowData, renderers);
        if (value == null) {
            cell.setCellType(CellType.BLANK);
            return;
        }
        Class<?> type = value.getClass();
        if (value instanceof Number) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
            cell.setCellType(CellType.BOOLEAN);
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDate) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Date.from(((LocalDate) value).atStartOfDay(zoneId).toInstant()));
        } else if (value instanceof LocalDateTime) {
            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Date.from(((LocalDateTime) value).atZone(zoneId).toInstant()));
        } else if (value instanceof String) {
            cell.setCellType(CellType.STRING);
            // 防止 注入攻击
            if (excelColumn == null || excelColumn.safeCheck()) {
                value = SecurityUtils.preventCsvInjection((String) value);
            }
            cell.setCellValue(String.valueOf(value));
        } else {
            this.logger.warn("can not process type [{}] in excel export", type);
            cell.setCellType(CellType.STRING);
            if (excelColumn == null || excelColumn.safeCheck()) {
                value = SecurityUtils.preventCsvInjection(String.valueOf(value));
            }
            cell.setCellValue(String.valueOf(value));
        }
    }

    private CellStyle getCellStyle(SXSSFWorkbook workbook, ExcelColumn excelColumn, boolean isTitle) {
        //标题无需缓存
        if (isTitle) {
            return createCellStyle(workbook, excelColumn, true);
        }
        return styleMap.computeIfAbsent(Pair.of(excelColumn, workbook), column -> {
            CellStyle columnCellStyle = createCellStyle(workbook, excelColumn, false);
            String pattern = getPattern(excelColumn);
            if (StringUtils.isNotBlank(pattern)) {
                //lazy init
                if (dataFormat == null) {
                    dataFormat = workbook.createDataFormat();
                }
                columnCellStyle.setDataFormat(dataFormat.getFormat(pattern));
            }
            return columnCellStyle;
        });
    }

    private CellStyle createCellStyle(SXSSFWorkbook workbook, ExcelColumn excelColumn, boolean isTitle) {
        CellStyle columnCellStyle = workbook.createCellStyle();
        Class<? extends org.hzero.export.annotation.Font> font =
                excelColumn == null ? null : (isTitle ? excelColumn.titleFont() : excelColumn.font());
        Class<? extends org.hzero.export.annotation.Color> foregroundColor =
                excelColumn == null ? null : (isTitle ? excelColumn.titleForegroundColor() : excelColumn.foregroundColor());
        Class<? extends org.hzero.export.annotation.Color> backgroundColor =
                excelColumn == null ? null : (isTitle ? excelColumn.titleBackgroundColor() : excelColumn.backgroundColor());
        if (font != null) {
            setFont(workbook, columnCellStyle, font, excelColumn);
        }
        if (foregroundColor != null) {
            setForegroundColor(columnCellStyle, foregroundColor);
        }
        if (backgroundColor != null) {
            setBackgroundColor(columnCellStyle, backgroundColor);
        }
        return columnCellStyle;
    }

    private void setFont(SXSSFWorkbook workbook, CellStyle style, Class<? extends org.hzero.export.annotation.Font> fontClass, ExcelColumn excelColumn) {
        org.hzero.export.annotation.Font font = getFontInstance(fontClass);
        if (font instanceof EmptyFont) {
            return;
        }
        style.setFont(font.getFont(fontMap.computeIfAbsent(Pair.of(excelColumn, workbook), column -> workbook.createFont())));
    }

    private org.hzero.export.annotation.Font getFontInstance(Class<? extends org.hzero.export.annotation.Font> fontClass) {
        try {
            return fontClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new CommonException("org.hzero.export.annotation.Font class newInstance() failed", e);
        }
    }

    private void setForegroundColor(CellStyle style, Class<? extends org.hzero.export.annotation.Color> foregroundColorClass) {
        org.hzero.export.annotation.Color foregroundColor = getForegroundColorInstance(foregroundColorClass);
        if (foregroundColor instanceof EmptyColor) {
            return;
        }
        IndexedColors colors = foregroundColor.getColor();
        FillPatternType fillPattern = foregroundColor.getFillPattern();
        style.setFillPattern(fillPattern);
        style.setFillForegroundColor(colors.getIndex());
    }

    private org.hzero.export.annotation.Color getForegroundColorInstance(Class<? extends org.hzero.export.annotation.Color> foregroundColorClass) {
        try {
            return foregroundColorClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new CommonException("org.hzero.export.annotation.ForegroundColor class newInstance() failed", e);
        }
    }

    private void setBackgroundColor(CellStyle style, Class<? extends org.hzero.export.annotation.Color> backgroundColorClass) {
        org.hzero.export.annotation.Color foregroundColor = getBackgroundColorInstance(backgroundColorClass);
        if (foregroundColor instanceof EmptyColor) {
            return;
        }
        IndexedColors colors = foregroundColor.getColor();
        FillPatternType fillPattern = foregroundColor.getFillPattern();
        style.setFillPattern(fillPattern);
        style.setFillBackgroundColor(colors.getIndex());
    }

    private org.hzero.export.annotation.Color getBackgroundColorInstance(Class<? extends org.hzero.export.annotation.Color> backgroundColorClass) {
        try {
            return backgroundColorClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new CommonException("org.hzero.export.annotation.ForegroundColor class newInstance() failed", e);
        }
    }

    private void setComment(SXSSFWorkbook workbook, SXSSFCell cell, Class<? extends org.hzero.export.annotation.Comment> commentClass, ExcelColumn excelColumn) {
        org.hzero.export.annotation.Comment comment = getCommentInstance(commentClass);
        if (comment instanceof EmptyComment) {
            return;
        }
        CommentProperty commentProperty = comment.getComment();
        if (cell.getCellComment() == null) {
            SXSSFDrawing p = cell.getSheet().createDrawingPatriarch();
            Comment commentValue = commentMap.computeIfAbsent(Pair.of(excelColumn, workbook), column -> {
                Comment c = p.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex() + 2, cell.getRowIndex() + 2));
                c.setString(new XSSFRichTextString(commentProperty.getComment()));
                c.setAuthor(commentProperty.getAuthor());
                return c;
            });
            cell.setCellComment(commentValue);
        }
    }

    private org.hzero.export.annotation.Comment getCommentInstance(Class<? extends org.hzero.export.annotation.Comment> commentClass) {
        try {
            return commentClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new CommonException("org.hzero.export.annotation.Comment class newInstance() failed", e);
        }
    }

    /**
     * 设置单元格的宽度
     *
     * @param sheet       SXSSFSheet
     * @param type        数据类型
     * @param columnIndex 列索引
     */
    protected void setCellWidth(SXSSFSheet sheet, String type, int columnIndex, int customWidth) {
        if (StringUtils.isBlank(type)) {
            return;
        }
        int columnWidth = 0;
        switch (type.toUpperCase()) {
            case "STRING":
                columnWidth = 22 * 256;
                break;
            case "FLOAT":
            case "DOUBLE":
            case "BIGDECIMAL":
            case "INT":
            case "INTEGER":
            case "LONG":
            case "BOOLEAN":
                columnWidth = 14 * 256;
                break;
            case "DATE":
                columnWidth = 21 * 256;
                break;
            default:
                columnWidth = 20 * 256;
                break;
        }
        sheet.setColumnWidth(columnIndex, columnWidth);
        if (customWidth != 0) {
            sheet.setColumnWidth(columnIndex, (int) (255.86 * customWidth + 184.27));
        }
    }


    /**
     * 设置标题单元格样式
     */
    public void setTitleCellStyle(SXSSFWorkbook workbook) {
        Font font = workbook.createFont();
        font.setColor(Font.COLOR_NORMAL);
        font.setBold(true);

        titleCellStyle = workbook.createCellStyle();
        titleCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.index);
        titleCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        titleCellStyle.setFont(font);
        titleCellStyle.setAlignment(HorizontalAlignment.CENTER);
        titleCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
    }

    protected Object doRender(Object value, Object rowData, List<Class<? extends ValueRenderer>> rendererTypes) {
        if (CollectionUtils.isNotEmpty(rendererTypes)) {
            for (Class<? extends ValueRenderer> rendererType : rendererTypes) {
                ValueRenderer renderer = this.renderers.computeIfAbsent(rendererType, key ->
                        Optional.ofNullable(key).map(type -> {
                            try {
                                return type.getConstructor().newInstance();
                            } catch (Exception e) {
                                logger.error("can not create renderer!", e);
                                return null;
                            }
                        }).orElse(null)
                );

                if (renderer != null) {
                    value = renderer.render(value, rowData);
                }
            }
        }
        return value;
    }

    @Override
    public List<SXSSFWorkbook> getWorkbooks() {
        return workbooks;
    }

    protected SXSSFWorkbook getCurrentWorkbook() {
        return workbooks.get(workbookIndex);
    }


    /**
     * 设置下拉选项
     *
     * @param sheet    sheet
     * @param column   列信息
     * @param startRow 起始行下标
     * @param colIndex 列下表
     */
    protected void setOptions(SXSSFSheet sheet, ExportColumn column, int startRow, int colIndex) {
        String lovCode = column.getExcelColumn().lovCode();
        if (StringUtils.isBlank(lovCode)) {
            return;
        }
        LovAdapter lovAdapter;
        try {
            lovAdapter = ApplicationContextHelper.getContext().getBean(LovAdapter.class);
        } catch (Exception e) {
            return;
        }
        Long tenantId = BaseConstants.DEFAULT_TENANT_ID;
        CustomUserDetails userDetails = DetailsHelper.getUserDetails();
        if (userDetails != null) {
            tenantId = userDetails.getTenantId();
        }
        String[] options = getLovValue(tenantId, lovCode, lovAdapter);
        if (options.length == 0) {
            return;
        }
        // 加载下拉列表内容
        DataValidationHelper helper = sheet.getDataValidationHelper();
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(startRow, singleSheetMaxRow, colIndex, colIndex);
        DataValidationConstraint constraint = helper.createExplicitListConstraint(options);
        DataValidation dataValidation = helper.createValidation(constraint, cellRangeAddressList);
        // 处理Excel兼容性问题
        if (dataValidation instanceof XSSFDataValidation) {
            dataValidation.setSuppressDropDownArrow(true);
            dataValidation.setShowErrorBox(true);
        } else {
            dataValidation.setSuppressDropDownArrow(false);
        }
        sheet.addValidationData(dataValidation);
    }

    /**
     * 获取值集meaning集合
     *
     * @param tenantId   租户
     * @param lovCode    值集编码
     * @param lovAdapter lovAdapter
     * @return 值集meaning集合
     */
    private String[] getLovValue(Long tenantId, String lovCode, LovAdapter lovAdapter) {
        List<LovValueDTO> lovValueList = lovAdapter.queryLovValue(lovCode, tenantId);
        if (CollectionUtils.isEmpty(lovValueList)) {
            return new String[0];
        }
        String[] lovValues = new String[lovValueList.size()];
        lovValueList.stream().map(LovValueDTO::getMeaning).distinct().collect(Collectors.toList()).toArray(lovValues);
        return lovValues;
    }

    /**
     * 获取格式掩码
     */
    protected String getPattern(ExcelColumn excelColumn) {
        if (excelColumn == null) {
            return null;
        }
        Class<? extends org.hzero.export.annotation.ExpandProperty> propertyClass = excelColumn.expandProperty();
        org.hzero.export.annotation.ExpandProperty property;
        try {
            // 先尝试从容器中获取
            property = ApplicationContextHelper.getContext().getBean(propertyClass);
        } catch (Exception e) {
            try {
                property = propertyClass.newInstance();
            } catch (Exception exception) {
                logger.error("Interface Pattern Instantiation failed!", e);
                return excelColumn.pattern();
            }
        }
        String patternStr = property.getPattern();
        if (StringUtils.isBlank(patternStr)) {
            return excelColumn.pattern();
        }
        return patternStr;
    }
}
