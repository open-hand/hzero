package org.hzero.export.exporter;

import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import org.hzero.core.message.MessageAccessor;
import org.hzero.export.FillerHolder;
import org.hzero.export.constant.ExportConstants;
import org.hzero.export.constant.FileType;
import org.hzero.export.entity.Csv;
import org.hzero.export.filler.ICsvFiller;
import org.hzero.export.vo.ExportColumn;
import org.hzero.export.vo.ExportProperty;

/**
 * csv导出
 *
 * @author shuangfei.zhu@hand-china.com 2021/05/08 10:08
 */
public class CsvExporter implements IFileExporter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvExporter.class);

    /**
     * 数据填充器
     */
    private final ICsvFiller csvFiller;
    private final FileType fileType;

    private File temp;

    private InputStream inputStream;

    private String errorMessage;

    /**
     * @param root           导出列
     * @param fillerType     数据填充方式
     * @param singleMaxPage  单excel最大sheet数
     * @param singleMaxRow   单sheet页最大行数
     * @param fileType       文件类型
     * @param exportProperty 导出属性
     */
    public CsvExporter(ExportColumn root, String fillerType, int singleMaxPage, int singleMaxRow, FileType fileType, ExportProperty exportProperty) {
        // 根据导出类型，加载处理器
        this.csvFiller = FillerHolder.getCsvFiller(fillerType, root, exportProperty);
        // 配置数据限制
        this.csvFiller.configure(singleMaxPage, singleMaxRow, fileType);
        // 文件类型
        this.fileType = fileType;
    }

    /**
     * 导出模板，仅设置标题
     */
    @Override
    public void fillTitle() {
        csvFiller.fillTitle();
    }

    /**
     * 填充表数据
     *
     * @param exportData 要填充的数据
     */
    @Override
    public void fillData(List<?> exportData) {

        if (CollectionUtils.isEmpty(exportData) || exportData.get(0) == null) {
            return;
        }

        try {
            this.csvFiller.fillData(exportData);
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
    public String export(ByteArrayOutputStream outputStream, boolean md5) throws IOException {
        try {
            if (StringUtils.isNotBlank(errorMessage)) {
                outputStream.write(errorMessage.getBytes());
                return null;
            }
            List<Csv> csvList = csvFiller.getCsv();
            if (csvList.size() == 1) {
                Csv csv = csvList.get(0);
                csv.closeWriter();
                // 写出数据
                IOUtils.write(csv.getOutputStream().toByteArray(), outputStream);
                csv.close();
            } else {
                writeZipBytes(outputStream);
            }
            // 计算文件的MD5
            if (md5) {
                return DigestUtils.md5Hex(outputStream.toByteArray());
            }
        } catch (IOException e) {
            LOGGER.error("IO exception when write response", e);
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
        return null;
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
        return FileUtils.readFileToByteArray(tmp);
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
        return this.csvFiller.getRootExportColumn().getTitle();
    }

    @Override
    public String getOutputFileSuffix() {
        if (!StringUtils.isEmpty(errorMessage)) {
            return ExportConstants.TXT_SUFFIX;
        }
        return isZip() ? ExportConstants.ZIP_SUFFIX : fileType.getSuffix();
    }

    private boolean isZip() {
        return csvFiller.getCsv().size() > 1;
    }

    private void writeZipBytes(OutputStream outputStream) {
        try (ZipOutputStream zos = new ZipOutputStream(outputStream)) {
            List<Csv> csvList = csvFiller.getCsv();
            for (Csv csv : csvList) {
                zos.putNextEntry(new ZipEntry(csv.getName() + fileType.getSuffix()));
                csv.closeWriter();
                zos.write(csv.getOutputStream().toByteArray());
                csv.closeWriter();
                zos.closeEntry();
            }
        } catch (IOException e) {
            LOGGER.error("error!", e);
        }
    }

    private File buildTempFile() throws IOException {
        File tmp = null;
        FileOutputStream fos = null;
        try {
            List<Csv> csvList = this.csvFiller.getCsv();
            if (csvList.size() == 1) {
                tmp = new File(UUID.randomUUID() + fileType.getSuffix());
                boolean create = tmp.createNewFile();
                Assert.isTrue(create, "create tmp file error!");
                fos = new FileOutputStream(tmp);
                Csv csv = csvList.get(0);
                csv.closeWriter();
                // 写出数据
                IOUtils.write(csv.getOutputStream().toByteArray(), fos);
                csv.close();
            } else {
                tmp = new File(UUID.randomUUID() + ExportConstants.ZIP_SUFFIX);
                boolean create = tmp.createNewFile();
                Assert.isTrue(create, "create tmp file error!");
                fos = new FileOutputStream(tmp);
                writeZipBytes(fos);
            }
            fos.flush();
        } catch (IOException e) {
            LOGGER.error("error!", e);
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
     */
    public InputStream readInputStream() throws IOException {
        if (temp == null) {
            temp = buildTempFile();
        }
        return readInputStreamFromTempFile(temp);
    }

}
