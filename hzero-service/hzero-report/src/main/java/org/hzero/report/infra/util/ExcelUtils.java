package org.hzero.report.infra.util;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.VerticalAlignment;
import jxl.write.*;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.base.BaseConstants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;

/**
 * excel 工具类
 *
 * @author fanghan.liu 2020/05/22 11:20
 */
public class ExcelUtils {

    protected static final Logger logger = LoggerFactory.getLogger(ExcelUtils.class);

    private ExcelUtils() {
    }

    public static byte[] htmlToExcel(String html, String sheetName) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        generateExcel(html, sheetName, os);
        return os.toByteArray();
    }

    public static OutputStream htmlToExcel(String html, String sheetName, OutputStream os) {
        generateExcel(html, sheetName, os);
        return os;
    }

    private static void generateExcel(String html, String sheetName, OutputStream os) {
        try {
            WritableWorkbook workbook = Workbook.createWorkbook(os);
            WritableSheet sheet = workbook.createSheet(sheetName, 0);
            Document document = Jsoup.parse(html);
            Elements tables = document.getElementsByTag("table");
            if (tables.isEmpty()) {
                return;
            }
            // 创建表头
            Element head = tables.get(0).getElementsByTag("thead").get(0);
            Elements ths = head.children().get(0).children();
            for (int i = 0; i < ths.size(); i++) {
                sheet.addCell(generateCellTitle(ths, i, sheet));
            }
            // 创建表体
            Element table = tables.get(0);
            if (tables.size() > 1) {
                table = tables.get(1);
            }
            Elements tbody = table.getElementsByTag("tbody");
            Elements trs = tbody.get(0).getElementsByTag("tr");
            List<int[]> mergeCells = new ArrayList<>();
            for (int i = 0; i < trs.size(); i++) {
                Element tr = trs.get(i);
                int size = tr.children().size();
                // 合并左侧单元格会导致每行数量不一致，因此从右往左创建单元格
                for (int j = ths.size() - 1; j >= 0; j--) {
                    if (size <= 0) {
                        continue;
                    }
                    // 处理合并单元格
                    Element td = tr.children().get(size - 1);
                    String rowspan = td.attr("rowspan");
                    if (!StringUtils.isEmpty(rowspan)) {
                        int i1 = Integer.parseInt(rowspan);
                        int[] mergeCell = {j, i + 1, j, i + i1};
                        mergeCells.add(mergeCell);
                    }
                    sheet.addCell(generateCellBody(j, i, td));
                    size--;
                }
            }
            // 合并单元格
            if (!CollectionUtils.isEmpty(mergeCells)) {
                for (int[] mergeCell : mergeCells) {
                    sheet.mergeCells(mergeCell[0], mergeCell[1], mergeCell[2], mergeCell[3]);
                }
            }
            workbook.write();
            workbook.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            logger.error("Html To Excel Failed", e);
        }
    }

    private static WritableCell generateCellTitle(Elements ths, int index, WritableSheet sheet) throws WriteException {
        WritableFont font = new WritableFont(WritableFont.ARIAL, WritableFont.DEFAULT_POINT_SIZE, WritableFont.BOLD);
        WritableCellFormat format = new WritableCellFormat(font);
        format.setAlignment(Alignment.CENTRE);
        format.setVerticalAlignment(VerticalAlignment.CENTRE);
        Element th = ths.get(index);
        String width = th.attr("width");
        if (!StringUtils.isEmpty(width)) {
            width = width.substring(0, width.indexOf("px"));
            int widthValue = Integer.parseInt(width);
            if (widthValue > BaseConstants.Digital.ZERO) {
                widthValue = widthValue * 10 / 72;
                sheet.setColumnView(index, widthValue);
            }
        }
        Label cell = new Label(index, 0, th.text());
        cell.setCellFormat(format);
        return cell;
    }

    private static WritableCell generateCellBody(int j, int i, Element td) throws WriteException {
        WritableFont font = new WritableFont(WritableFont.ARIAL, WritableFont.DEFAULT_POINT_SIZE, WritableFont.NO_BOLD);
        WritableCellFormat format = new WritableCellFormat(font);
        format.setVerticalAlignment(VerticalAlignment.CENTRE);
        Label cell = new Label(j, i + 1, td.text());
        cell.setCellFormat(format);
        return cell;
    }

}
