package org.hzero.boot.message.entity;

import java.util.Arrays;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/03 20:32
 */
public class Attachment {

    public Attachment() {
    }

    public Attachment(Attachment attachment) {
        this.file = attachment.getFile();
        this.fileName = attachment.getFileName();
    }

    private byte[] file;
    private String fileName;

    public byte[] getFile() {
        return file;
    }

    public Attachment setFile(byte[] file) {
        this.file = file;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public Attachment setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    @Override
    public String toString() {
        return "Attachment{" +
                "file=" + Arrays.toString(file) +
                ", fileName='" + fileName + '\'' +
                '}';
    }
}
