package org.hzero.export.filler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import io.choerodon.core.convertor.ApplicationContextHelper;
import io.choerodon.core.exception.CommonException;
import io.choerodon.core.oauth.CustomUserDetails;
import io.choerodon.core.oauth.DetailsHelper;

import org.hzero.core.base.BaseConstants;
import org.hzero.core.util.Pair;
import org.hzero.core.util.SecurityUtils;
import org.hzero.export.annotation.CommentProperty;
import org.hzero.export.annotation.EmptyColor;
import org.hzero.export.annotation.EmptyComment;
import org.hzero.export.annotation.EmptyFont;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.constant.FileType;
import org.hzero.export.lov.ExportLovValue;
import org.hzero.export.lov.IExportLovAdapter;
import org.hzero.export.render.ValueRenderer;
import org.hzero.export.vo.ExcelColumnProperty;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * Excel2007 数据填充器
 * (为了兼容性考虑，这个不能换包路径)
 *
 * @author xcxcxcxcx
 */
@SuppressWarnings("DuplicatedCode")
public abstract class ExcelFiller extends FileFiller implements IExcelFiller {

    /**
     * SXSS的缓存窗口值
     */
    protected static final int ROW_ACCESS_WINDOW_SIZE = 100;

    private final Map<Pair<ExcelColumnProperty, Workbook>, CellStyle> styleMap = new HashMap<>();
    private final Map<Pair<ExcelColumnProperty, Workbook>, Font> fontMap = new HashMap<>();
    private volatile DataFormat dataFormat;

    private final ZoneId zoneId = ZoneId.systemDefault();

    protected static final Logger LOGGER = LoggerFactory.getLogger(ExcelFiller.class);

    private final List<Workbook> workbooks = new ArrayList<>();

    private ExportColumn root;
    /**
     * 文件类型
     */
    private FileType fileType;
    /**
     * 单个excel的最大sheet数
     */
    private Integer singleMaxPage;
    /**
     * 单个sheet页的最大记录数
     */
    private Integer singleMaxRow;
    /**
     * 单个excel的最大记录数
     */
    private Integer countMaxRow;
    /**
     * excel剩余容纳数据量
     */
    private int remainCapacityOfExcel;
    /**
     * sheet页可容纳数据量
     */
    private int remainCapacityOfSheet;
    /**
     * 当前工作簿下标
     */
    private int workbookIndex;
    /**
     * 当前sheet页下标
     */
    private int sheetIndex;

    /**
     * sheet名称缓存，名称excel内唯一
     */
    private final Map<Pair<Integer, String>, Integer> sheetNames = new HashMap<>(16);
    /**
     * 记录sheet和所属批次
     */
    protected Map<Sheet, String> sheetBatch = new LinkedHashMap<>(16);

    /**
     * 无参构造用于实例化到容器
     */
    public ExcelFiller() {
    }

    /**
     * 单参数构造保留为了兼容老版本
     */
    @Deprecated
    public ExcelFiller(ExportColumn root) {
        this.root = root;
        this.exportProperty = null;
    }

    public ExcelFiller(ExportColumn root, ExportProperty exportProperty) {
        this.root = root;
        this.exportProperty = exportProperty;
    }

    @Override
    public void configure(int singleMaxPage, int singleMaxRow, FileType fileType) {
        this.singleMaxPage = singleMaxPage;
        this.singleMaxRow = singleMaxRow;
        this.countMaxRow = singleMaxPage * singleMaxRow;
        this.fileType = fileType;
    }

    /**
     * 创建excel2007的sheet页及标题行
     * <p>
     * 兼容性考虑，原本仅支持2007且写死了SXSSFWorkbook，后续不考虑兼容性的话可以将该方法移除
     *
     * @param workbook 工作簿
     */
    public void createSheetAndTitle(SXSSFWorkbook workbook) {
        createSheet(workbook);
    }

    /**
     * 创建excel2003、excel2007的sheet页及标题行
     *
     * @param workbook 工作簿
     */
    public void createSheet(Workbook workbook) {
        throw new CommonException("Unsupported file export type.");
    }

    /**
     * 填充excel2007的数据
     * 兼容性考虑，原本仅支持2007且写死了SXSSFSheet，后续不考虑兼容性的话可以将该方法移除
     *
     * @param sheet 导出sheet
     * @param data  数据
     */
    protected void fillData0(SXSSFSheet sheet, List<?> data) {
        fillData(sheet, data);
    }

    /**
     * 填充excel2003、excel2007的数据
     *
     * @param sheet 导出sheet
     * @param data  数据
     */
    protected void fillData(Sheet sheet, List<?> data) {
        throw new CommonException("Unsupported file export type.");
    }

    @Override
    public void fillTitle() {
        if (CollectionUtils.isEmpty(workbooks)) {
            Workbook workbook;
            if (FileType.EXCEL_2003.equals(fileType)) {
                workbook = new HSSFWorkbook();
            } else {
                workbook = new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE);
            }
            workbooks.add(workbook);
        }
        Workbook workbook = workbooks.get(0);
        if (workbook instanceof SXSSFWorkbook) {
            createSheetAndTitle((SXSSFWorkbook) workbook);
        } else {
            createSheet(workbook);
        }
    }

    /**
     * 创建工作簿并填充数据
     *
     * @param data 数据
     */
    @Override
    public void fillData(List<?> data) {
        int append = data.size();
        // 根据数据量生成工作簿，每个工作簿存储的数据量为 单sheet最大行数*最大sheet页数量
        List<Workbook> workbooks = getAppendWriteExcels(append);
        // 划分每个工作簿的数据
        List<List<?>> shardData = shardData(remainCapacityOfExcel, countMaxRow, data);
        // 填充工作簿数据
        Assert.isTrue(workbooks.size() == shardData.size(), "workbooks.size() != shardData.size()");
        for (int i = 0; i < workbooks.size(); i++) {
            Workbook workbook = workbooks.get(i);
            List<?> item = shardData.get(i);
            fillExcelData(workbook, item);
        }
    }

    private List<Workbook> getAppendWriteExcels(int append) {
        int remainCapacity = remainCapacityOfExcel;
        if (remainCapacity < append) {
            // 需要扩容
            int remainAppend = append - remainCapacity;
            int appendExcel = (int) Math.ceil((double) remainAppend / countMaxRow);
            for (int i = 0; i < appendExcel; i++) {
                Workbook workbook;
                if (FileType.EXCEL_2003.equals(fileType)) {
                    workbook = new HSSFWorkbook();
                } else {
                    workbook = new SXSSFWorkbook(ROW_ACCESS_WINDOW_SIZE);
                }
                workbooks.add(workbook);
            }
        }
        return workbooks.subList(workbookIndex, workbooks.size());
    }

    /**
     * 创建sheet页并填充数据
     */
    private void fillExcelData(Workbook workbook, List<?> data) {
        int append = data.size();
        // 根据数据量生成sheet页，每个sheet页存储的数据量为 单sheet最大行数
        List<Sheet> sheets = getAppendWriteSheets(workbook, append);
        // 划分每个sheet页的数据
        List<List<?>> shardData = shardData(remainCapacityOfSheet, singleMaxRow, data);
        // 插入数据
        Assert.isTrue(sheets.size() == shardData.size(), "sheets.size() != shardData.size()");
        for (int i = 0; i < sheets.size(); i++) {
            Sheet sheet = sheets.get(i);
            List<?> item = shardData.get(i);
            if (sheet instanceof SXSSFSheet) {
                fillData0((SXSSFSheet) sheet, item);
            } else {
                fillData(sheet, item);
            }
            resetRemainValue(item.size());
        }
    }

    /**
     * 数据按照每页数量限制，动态计算创建sheet页（头行分sheet页导出，每组数据会创建多个sheet）
     */
    private List<Sheet> getAppendWriteSheets(Workbook workbook, int append) {
        // 若小于数据总量，则需要扩容
        if (remainCapacityOfSheet < append) {
            // 需要扩容的大小
            int remainAppend = append - remainCapacityOfSheet;
            // 计算需要创建的sheet页数量
            int appendSheet = (int) Math.ceil((double) remainAppend / singleMaxRow);
            for (int i = 1; i <= appendSheet; i++) {
                if (workbook instanceof SXSSFWorkbook) {
                    createSheetAndTitle((SXSSFWorkbook) workbook);
                } else {
                    createSheet(workbook);
                }
            }
        }
        // sheet页创建完成，反向扫描获取sheet页列表
        List<Sheet> result = new ArrayList<>();
        // 相同批次的sheet页，只记录第一个sheet页
        List<String> stored = new ArrayList<>();
        for (Sheet sheet : getSheets(workbook)) {
            if (!sheetBatch.containsKey(sheet)) {
                continue;
            }
            String batch = sheetBatch.get(sheet);
            if (stored.contains(batch)) {
                // 该批次已记录，跳过
                continue;
            }
            result.add(sheet);
            stored.add(batch);
        }
        return result;
    }

    /**
     * 获取本次要操作的sheet页
     */
    private List<Sheet> getSheets(Workbook workbook) {
        List<Sheet> sheets = new ArrayList<>();
        for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
            sheets.add(workbook.getSheetAt(i));
        }
        return sheets.subList(sheetIndex, sheets.size());
    }

    /**
     * 记录可容纳数据量
     */
    private void resetRemainValue(int append) {
        int oldRemainCapacityOfExcel = remainCapacityOfExcel;
        int oldRemainCapacityOfSheet = remainCapacityOfSheet;
        if (append > oldRemainCapacityOfExcel) {
            append = append - oldRemainCapacityOfExcel;
            remainCapacityOfExcel = (countMaxRow - append % countMaxRow) % countMaxRow;
            remainCapacityOfSheet = (singleMaxRow - append % singleMaxRow) % singleMaxRow;
        } else {
            remainCapacityOfExcel = oldRemainCapacityOfExcel - append;
            if (append > oldRemainCapacityOfSheet) {
                remainCapacityOfSheet = (singleMaxRow - (append - oldRemainCapacityOfSheet) % singleMaxRow) % singleMaxRow;
            } else {
                remainCapacityOfSheet = oldRemainCapacityOfSheet - append;
            }
        }
        if (append > oldRemainCapacityOfSheet) {
            sheetIndex = sheetIndex + (int) Math.floor((double) (append - oldRemainCapacityOfSheet) / singleMaxRow);
        } else if (append == oldRemainCapacityOfSheet && append > 0) {
            sheetIndex = sheetIndex + 1;
        }
        workbookIndex = workbookIndex + (int) Math.floor((double) sheetIndex / singleMaxPage);
        sheetIndex = sheetIndex % singleMaxPage;
    }

    /**
     * 填充单元格
     *
     * @param cell                SXSSFCell
     * @param value               数据
     * @param rowData             行数据
     * @param excelColumnProperty 行数据配置属性
     */
    protected void fillCellValue(Workbook workbook, Cell cell, Object value, Object rowData, ExcelColumnProperty excelColumnProperty, boolean isTitle) {

        List<Class<? extends ValueRenderer>> renderers = isTitle ? null : excelColumnProperty.getRenderers();
        Class<? extends org.hzero.export.annotation.Comment> comment = excelColumnProperty.getComment();
        value = this.doRender(value, rowData, renderers);

        CellStyle style = getCellStyle(workbook, excelColumnProperty, isTitle);
        // 标题无需添加注释
        if (isTitle && comment != null) {
            setComment(cell, comment);
        }
        cell.setCellStyle(style);

        if (value == null) {
            cell.setCellType(CellType.BLANK);
            return;
        }
        Class<?> type = value.getClass();
        if (value instanceof Number) {
//            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(((Number) value).doubleValue());
        } else if (value instanceof Boolean) {
//            cell.setCellType(CellType.BOOLEAN);
            cell.setCellValue((Boolean) value);
        } else if (value instanceof Date) {
//            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue((Date) value);
        } else if (value instanceof LocalDate) {
//            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Date.from(((LocalDate) value).atStartOfDay(zoneId).toInstant()));
        } else if (value instanceof LocalDateTime) {
//            cell.setCellType(CellType.NUMERIC);
            cell.setCellValue(Date.from(((LocalDateTime) value).atZone(zoneId).toInstant()));
        } else if (value instanceof String) {
//            cell.setCellType(CellType.STRING);
            // 防止 注入攻击
            if (excelColumnProperty.isSafeCheck()) {
                value = SecurityUtils.preventCsvInjection((String) value);
            }
            cell.setCellValue(String.valueOf(value));
        } else {
            LOGGER.warn("can not process type [{}] in excel export", type);
//            cell.setCellType(CellType.STRING);
            if (excelColumnProperty.isSafeCheck()) {
                value = SecurityUtils.preventCsvInjection(String.valueOf(value));
            }
            cell.setCellValue(String.valueOf(value));
        }
    }

    private CellStyle getCellStyle(Workbook workbook, ExcelColumnProperty excelColumnProperty, boolean isTitle) {
        // 标题无需缓存
        if (isTitle) {
            return createCellStyle(workbook, excelColumnProperty, true);
        }
        if (excelColumnProperty == null) {
            return workbook.createCellStyle();
        }
        return styleMap.computeIfAbsent(Pair.of(excelColumnProperty, workbook), column -> {
            CellStyle columnCellStyle = createCellStyle(workbook, excelColumnProperty, false);
            String pattern = getPattern(excelColumnProperty);
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

    private CellStyle createCellStyle(Workbook workbook, ExcelColumnProperty excelColumnProperty, boolean isTitle) {
        CellStyle columnCellStyle = workbook.createCellStyle();
        Class<? extends org.hzero.export.annotation.Font> font =
                isTitle ? excelColumnProperty.getTitleFont() : excelColumnProperty.getFont();
        Class<? extends org.hzero.export.annotation.Color> foregroundColor =
                isTitle ? excelColumnProperty.getTitleForegroundColor() : excelColumnProperty.getForegroundColor();
        Class<? extends org.hzero.export.annotation.Color> backgroundColor =
                isTitle ? excelColumnProperty.getTitleBackgroundColor() : excelColumnProperty.getBackgroundColor();
        if (font != null) {
            setFont(workbook, columnCellStyle, font, excelColumnProperty);
        }
        if (foregroundColor != null) {
            setForegroundColor(columnCellStyle, foregroundColor);
        }
        if (backgroundColor != null) {
            setBackgroundColor(columnCellStyle, backgroundColor);
        }
        return columnCellStyle;
    }

    private void setFont(Workbook workbook, CellStyle style, Class<? extends org.hzero.export.annotation.Font> fontClass, ExcelColumnProperty excelColumnProperty) {
        org.hzero.export.annotation.Font font = getFontInstance(fontClass);
        if (font instanceof EmptyFont) {
            return;
        }
        style.setFont(font.getFont(fontMap.computeIfAbsent(Pair.of(excelColumnProperty, workbook), column -> workbook.createFont())));
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
        style.setFillForegroundColor(foregroundColor.getColor().getIndex());
        style.setFillPattern(foregroundColor.getFillPattern());
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
        style.setFillBackgroundColor(foregroundColor.getColor().getIndex());
        style.setFillPattern(foregroundColor.getFillPattern());
    }

    private org.hzero.export.annotation.Color getBackgroundColorInstance(Class<? extends org.hzero.export.annotation.Color> backgroundColorClass) {
        try {
            return backgroundColorClass.newInstance();
        } catch (IllegalAccessException | InstantiationException e) {
            throw new CommonException("org.hzero.export.annotation.ForegroundColor class newInstance() failed", e);
        }
    }

    private void setComment(Cell cell, Class<? extends org.hzero.export.annotation.Comment> commentClass) {
        org.hzero.export.annotation.Comment comment = getCommentInstance(commentClass);
        if (comment instanceof EmptyComment) {
            return;
        }
        CommentProperty commentProperty = comment.getComment();
        if (cell.getCellComment() == null) {
            Drawing<?> p = cell.getSheet().createDrawingPatriarch();
            Comment cellComment = p.createCellComment(new XSSFClientAnchor(0, 0, 0, 0, cell.getColumnIndex(), cell.getRowIndex(), cell.getColumnIndex() + 2, cell.getRowIndex() + 2));
            cellComment.setString(new XSSFRichTextString(commentProperty.getComment()));
            cellComment.setAuthor(commentProperty.getAuthor());
            cell.setCellComment(cellComment);
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
     * @param sheet       sheet
     * @param type        数据类型
     * @param columnIndex 列索引
     */
    protected void setCellWidth(Sheet sheet, String type, int columnIndex, int customWidth) {
        if (StringUtils.isBlank(type)) {
            return;
        }
        int columnWidth;
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

    @Override
    public List<Workbook> getWorkbooks() {
        return workbooks;
    }

    @Override
    public ExportColumn getRootExportColumn() {
        return root;
    }


    /**
     * 设置下拉选项
     *
     * @param sheet    sheet
     * @param column   列信息
     * @param startRow 起始行下标
     * @param colIndex 列下表
     */
    protected void setOptions(Sheet sheet, ExportColumn column, int startRow, int colIndex) {
        ExcelColumnProperty excelColumnProperty = column.getExcelColumnProperty();
        String lovCode = excelColumnProperty.getLovCode();
        if (StringUtils.isBlank(lovCode)) {
            return;
        }
        IExportLovAdapter lovAdapter;
        try {
            lovAdapter = ApplicationContextHelper.getContext().getBean(IExportLovAdapter.class);
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
        CellRangeAddressList cellRangeAddressList = new CellRangeAddressList(startRow, singleMaxRow, colIndex, colIndex);
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
    private String[] getLovValue(Long tenantId, String lovCode, IExportLovAdapter lovAdapter) {
        List<ExportLovValue> lovValueList = lovAdapter.getLovValue(tenantId, lovCode);
        if (CollectionUtils.isEmpty(lovValueList)) {
            return new String[0];
        }
        String[] lovValues = new String[lovValueList.size()];
        lovValueList.stream().map(ExportLovValue::getMeaning).distinct().collect(Collectors.toList()).toArray(lovValues);
        return lovValues;
    }

    /**
     * 处理sheet页名称，若sheet页名称重复，添加 (x)后缀
     */
    protected String interceptSheetName(String sheetName) {
        // 不带后缀的名称
        String nameWithoutSuffix = sheetName;
        if (sheetName.contains(ExportConstants.FILE_SUFFIX_SEPARATE)) {
            nameWithoutSuffix = sheetName.substring(0, sheetName.lastIndexOf(ExportConstants.FILE_SUFFIX_SEPARATE));
        }
        if (StringUtils.isBlank(nameWithoutSuffix)) {
            nameWithoutSuffix = "Sheet";
        }
        // 判断sheet名称是否已经存在
        Pair<Integer, String> key = Pair.of(workbookIndex, nameWithoutSuffix);
        int num = 1;
        if (sheetNames.containsKey(key)) {
            num = sheetNames.get(key);
            num++;
        }
        sheetNames.put(key, num);

        String suffix = ExportConstants.FILE_SUFFIX_SEPARATE + num;
        // 若名称超过31则截取，但是要保留后面的编号后缀
        if (nameWithoutSuffix.length() <= ExportConstants.SHEET_NAME_MAX_LENGTH - suffix.length()) {
            return nameWithoutSuffix + suffix;
        }
        return nameWithoutSuffix.substring(0, ExportConstants.SHEET_NAME_MAX_LENGTH - suffix.length()) + suffix;
    }

    /**
     * 将编码保存至隐藏sheet页
     */
    protected void saveCodeToHiddenSheet(Workbook workbook, int fromSheetIndex, String code, String title) {
        Sheet hiddenSheet = workbook.getSheet(ExportConstants.CODE_SHEET_NAME);
        if (hiddenSheet == null) {
            // sheet不能存，创建sheet页并设置sheet页隐藏
            hiddenSheet = workbook.createSheet(ExportConstants.CODE_SHEET_NAME);
            workbook.setSheetHidden(workbook.getSheetIndex(hiddenSheet), true);
        }
        // 在第一列写数据
        int lastRowIndex = hiddenSheet.getLastRowNum();
        Row row = hiddenSheet.createRow(lastRowIndex + 1);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("%s|%s|%s", fromSheetIndex, code, title));
    }
}
