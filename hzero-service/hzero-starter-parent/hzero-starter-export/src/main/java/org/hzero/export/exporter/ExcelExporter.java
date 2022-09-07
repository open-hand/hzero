package org.hzero.export.exporter;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.hzero.core.message.MessageAccessor;
import org.hzero.export.ExcelFillerHolder;
import org.hzero.export.IExcelExporter;
import org.hzero.export.IExcelFiller;
import org.hzero.export.vo.ExportColumn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class ExcelExporter implements IExcelExporter {

    public static final int MAX_ROW = 1000000;

    private static final String TXT_SUFFIX = ".txt";
    private static final String ZIP_SUFFIX = ".zip";
    private static final String EXCEL_SUFFIX = ".xlsx";

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelExporter.class);

    // 数据填充器
    private IExcelFiller excelFiller;

    private File temp;

    private InputStream inputStream;

    private String errorMessage;

    /**
     * @param root                   导出列
     * @param fillerType             数据填充方式
     * @param singleExcelMaxSheetNum 单excel最大sheet数
     * @param singleSheetMaxRow      单sheet页最大行数
     */
    public ExcelExporter(ExportColumn root, String fillerType,
                         int singleExcelMaxSheetNum, int singleSheetMaxRow) {
        this.excelFiller = ExcelFillerHolder.getExcelFiller(fillerType, root);
        this.excelFiller.configure(singleExcelMaxSheetNum, singleSheetMaxRow);
    }

    /**
     * 导出模板，仅设置标题
     */
    @Override
    public void fillTitle() {
        excelFiller.fillTitle();
    }

    /**
     * 填充表数据
     *
     * @param exportData 要填充的数据
     */
    @Override
    public void fillSheet(List<?> exportData) {

        if (CollectionUtils.isEmpty(exportData) || exportData.get(0) == null) {
            return;
        }

        try {
            this.excelFiller.fillData(exportData);
        } catch (Exception e) {
            LOGGER.error("fill data occurred error.", e);
            errorMessage = e.getMessage();
        }
    }

    @Override
    public void setError(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    @Override
    public String getError() {
        return MessageAccessor.getMessage(errorMessage).desc();
    }

    @Override
    public void export(OutputStream outputStream) throws IOException {
        writeBytes(outputStream);
    }

    @Override
    public InputStream export() throws IOException {
        return readInputStream();
    }

    @Override
    public byte[] exportBytes() throws IOException {
        return readFileBytes();
    }

    private byte[] readFileBytes() throws IOException {
        if (temp == null) {
            temp = buildTempFile();
        }
        return readDataFromTempFile(temp);
    }

    private byte[] readDataFromTempFile(File tmp) throws IOException {
        FileInputStream fis = null;
        byte[] buffer = null;
        try {
            fis = new FileInputStream(tmp);
            int max = fis.available();
            buffer = new byte[max];
            fis.read(buffer, 0, max);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                fis.close();
            }
        }
        return buffer;
    }

    @Override
    public void close() throws Exception {
        if (inputStream != null) {
            inputStream.close();
        }
        if (temp != null) {
            boolean del = temp.delete();
            Assert.isTrue(del, "delete tmp file error!");
        }
    }

    @Override
    public String getTitle() {
        return this.excelFiller.getRootExportColumn().getTitle();
    }

    @Override
    public String getOutputFileSuffix() {
        if (!StringUtils.isEmpty(errorMessage)) {
            return TXT_SUFFIX;
        }
        return isZip() ? ZIP_SUFFIX : EXCEL_SUFFIX;
    }

    private boolean isZip() {
        return this.excelFiller.getWorkbooks().size() > 1;
    }

    private void writeBytes(OutputStream outputStream) throws IOException {
        try {
            if (!StringUtils.isEmpty(errorMessage)) {
                outputStream.write(errorMessage.getBytes());
                return;
            }
            List<SXSSFWorkbook> workbooks = excelFiller.getWorkbooks();
            if (workbooks.size() == 1) {
                workbooks.get(0).write(outputStream);
            } else {
                writeZipBytes(outputStream);
            }
        } catch (IOException e) {
            LOGGER.error("IO exception when write response", e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }

    }

    private void writeZipBytes(OutputStream outputStream) throws IOException {
        ZipOutputStream zipOutputStream = null;
        try {
            zipOutputStream = new ZipOutputStream(outputStream);
            ZipEntry zipEntry = null;
            List<SXSSFWorkbook> workbooks = excelFiller.getWorkbooks();
            for (int i = 0; i < workbooks.size(); i++) {
                SXSSFWorkbook workbook = workbooks.get(i);
                zipEntry = new ZipEntry(this.excelFiller.getRootExportColumn().getTitle() + "-" + (i + 1) + EXCEL_SUFFIX);
                zipOutputStream.putNextEntry(zipEntry);
                workbook.write(zipOutputStream);
            }
            zipOutputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (zipOutputStream != null) {
                zipOutputStream.closeEntry();
                zipOutputStream.close();
            }
        }

    }

    private File buildTempFile() throws IOException {
        File tmp = null;
        FileOutputStream fos = null;
        try {
            List<SXSSFWorkbook> workbooks = this.excelFiller.getWorkbooks();
            if (workbooks.size() == 1) {
                tmp = new File(UUID.randomUUID() + EXCEL_SUFFIX);
                boolean create = tmp.createNewFile();
                Assert.isTrue(create, "create tmp file error!");
                fos = new FileOutputStream(tmp);
                workbooks.get(0).write(fos);
                fos.flush();
            } else {
                tmp = new File(UUID.randomUUID() + ZIP_SUFFIX);
                boolean create = tmp.createNewFile();
                Assert.isTrue(create, "create tmp file error!");
                fos = new FileOutputStream(tmp);
                writeZipBytes(fos);
                fos.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                fos.close();
            }
        }
        return tmp;
    }

    private InputStream readInputStreamFromTempFile(File temp) throws IOException {
        if (inputStream == null) {
            inputStream = new FileInputStream(temp);
        }
        return inputStream;
    }

    /**
     * 获取文件输入流, 便于上传文件优化
     *
     * @return
     * @throws IOException
     */
    public InputStream readInputStream() throws IOException {
        if (temp == null) {
            temp = buildTempFile();
        }
        return readInputStreamFromTempFile(temp);
    }

}
