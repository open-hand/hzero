package org.hzero.file.api.dto;


/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/26 17:56
 */
public class UpdateFileDTO {

    private SaveCallbackParamDTO saveCallbackParam;
    private String fileKey;

    public SaveCallbackParamDTO getSaveCallbackParam() {
        return saveCallbackParam;
    }

    public UpdateFileDTO setSaveCallbackParam(SaveCallbackParamDTO saveCallbackParam) {
        this.saveCallbackParam = saveCallbackParam;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public UpdateFileDTO setFileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    @Override
    public String toString() {
        return "UpdateFileDTO{" +
                "saveCallbackParam=" + saveCallbackParam +
                ", fileKey='" + fileKey + '\'' +
                '}';
    }
}
