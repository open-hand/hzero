package org.hzero.excel.supporter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.hzero.core.base.BaseConstants;
import org.hzero.excel.entity.Column;

import com.alibaba.fastjson.JSONObject;

import io.choerodon.core.exception.CommonException;

/**
 * excel读取工具
 *
 * @author shuangfei.zhu@hand-china.com 2020/02/03 14:17
 */
public class ExcelReader {

    private static final DecimalFormat DEFAULT_DECIMAL_FORMAT = new DecimalFormat("#.####################");
    private static final FastDateFormat DEFAULT_DATE_FORMAT = FastDateFormat.getInstance("yyyy-MM-dd HH:mm:ss");

    public JSONObject readDataRow(Row dataRow, List<Column> columns) {
        JSONObject jsonObject = new JSONObject();
        for (Column column : columns) {
            String value = readDataCell(dataRow.getCell(column.getIndex()), column);
            if (value != null) {
                jsonObject.put(column.getName(), value);
            }
        }
        return jsonObject;
    }

    public static String readDataCell(Cell cell, Column column) {
        if (cell != null) {
            switch (cell.getCellType()) {
                case NUMERIC:
                    if (Column.DATE.equals(column.getColumnType())) {
                        return ExcelReader.readValue(cell.getNumericCellValue(), column);
                    } else {
                        return ExcelReader.readValue(cell.getStringCellValue(), column);
                    }
                case STRING:
                    return readValue(cell.getStringCellValue(), column);
                case FORMULA:
                    // TODO : 暂不支持公式
                    throw new CommonException("Sequence not support!");
                case BOOLEAN:
                    return readValue(cell.getBooleanCellValue(), column);
                case ERROR:
                    throw new CommonException("Not support cell type!");
                case _NONE:
                case BLANK:
                default:
                    break;
            }
        }
        return null;
    }

    public static String readValue(double value, Column column) {
        switch (column.getColumnType()) {
            case Column.STRING:
                return DEFAULT_DECIMAL_FORMAT.format(value);
            case Column.DATE:
                Date date = DateUtil.getJavaDate(value);
                if (StringUtils.isNotBlank(column.getFormat())) {
                    return FastDateFormat.getInstance(column.getFormat()).format(date);
                } else {
                    return DEFAULT_DATE_FORMAT.format(date);
                }
            case Column.LONG:
                return String.valueOf((long) value);
            case Column.DECIMAL:
                if (StringUtils.isNotBlank(column.getFormat())) {
                    return new DecimalFormat(column.getFormat()).format(value);
                } else {
                    return String.valueOf(value);
                }
            default:
                return null;
        }
    }

    public static String readValue(String value, Column column) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        switch (column.getColumnType()) {
            case Column.STRING:
            case Column.SEQUENCE:
            case Column.LONG:
                return value;
            case Column.DATE:
                FastDateFormat dateFormat;
                if (StringUtils.isNotBlank(column.getFormat())) {
                    dateFormat = FastDateFormat.getInstance(column.getFormat());
                } else {
                    dateFormat = DEFAULT_DATE_FORMAT;
                }
                try {
                    dateFormat.parse(value);
                    return value;
                } catch (ParseException e) {
                    throw new CommonException(BaseConstants.ErrorCode.DATA_INVALID);
                }
            case Column.DECIMAL:
                try {
                    if (StringUtils.isNotBlank(column.getFormat())) {
                        return new DecimalFormat(column.getFormat()).format(DecimalFormat.getNumberInstance().parse(value));
                    } else {
                        return String.valueOf(DecimalFormat.getNumberInstance().parse(value));
                    }
                } catch (ParseException e) {
                    throw new CommonException("Number '" + value + "' parsing failed!");
                }
            default:
                return null;
        }
    }

    public static String readValue(boolean value, Column column) {
        switch (column.getColumnType()) {
            case Column.STRING:
            case Column.SEQUENCE:
            case Column.LONG:
                return String.valueOf(value);
            case Column.DECIMAL:
                if (StringUtils.isNotBlank(column.getFormat())) {
                    return new DecimalFormat(column.getFormat()).format(value);
                } else {
                    return String.valueOf(value);
                }
            case Column.DATE:
                throw new CommonException("Date format error!");
            default:
                return null;
        }
    }
}
