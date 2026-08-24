package org.hzero.export.entity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import com.csvreader.CsvWriter;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.choerodon.core.exception.CommonException;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2021/08/10 11:26
 */
public class Csv {

    private static final Logger LOGGER = LoggerFactory.getLogger(Csv.class);

    /**
     * csv名称，不带格式后缀
     */
    private String name;
    private CsvWriter csvWriter;
    private ByteArrayOutputStream outputStream;
    private String batch;
    /**
     * 所属分组
     */
    private CsvGroup csvGroup;
    /**
     * csv的列数量
     */
    private int total;

    public Csv(CsvGroup csvGroup, String name, String batch) {
        this.csvGroup = csvGroup;
        this.name = name;
        this.batch = batch;
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            // 加上UTF-8文件的标识字符
            byteArrayOutputStream.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            this.csvWriter = new CsvWriter(byteArrayOutputStream, ',', StandardCharsets.UTF_8);
            this.outputStream = byteArrayOutputStream;
        } catch (IOException e) {
            throw new CommonException(e);
        }
    }

    public String getName() {
        return name;
    }

    public Csv setName(String name) {
        this.name = name;
        return this;
    }

    public CsvWriter getCsvWriter() {
        return csvWriter;
    }

    public Csv setCsvWriter(CsvWriter csvWriter) {
        this.csvWriter = csvWriter;
        return this;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public Csv setOutputStream(ByteArrayOutputStream outputStream) {
        this.outputStream = outputStream;
        return this;
    }

    public CsvGroup getCsvGroup() {
        return csvGroup;
    }

    public Csv setCsvGroup(CsvGroup csvGroup) {
        this.csvGroup = csvGroup;
        return this;
    }

    public int getTotal() {
        return total;
    }

    public Csv setTotal(int total) {
        this.total = total;
        return this;
    }

    public String getBatch() {
        return batch;
    }

    public Csv setBatch(String batch) {
        this.batch = batch;
        return this;
    }

    public void writeHead(List<String> list) {
        // 记录列总数
        this.total = list.size();
        writeRow(list);
    }

    public void writeRow(List<String> list) {
        try {
            if (CollectionUtils.isEmpty(list)) {
                csvWriter.writeRecord(new String[0]);
            } else {
                csvWriter.writeRecord(list.toArray(new String[0]));
            }
        } catch (IOException e) {
            LOGGER.error("error", e);
        }
    }

    public void closeWriter() {
        csvWriter.close();
    }

    public void close() {
        try {
            outputStream.close();
        } catch (IOException e) {
            LOGGER.error("error", e);
        }
    }
}
