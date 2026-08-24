package org.hzero.file.domain.entity;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Length;
import org.hzero.boot.platform.lov.annotation.LovValue;
import org.hzero.core.util.Regexs;
import org.hzero.file.domain.service.factory.StoreService;
import org.hzero.file.infra.constant.HfleConstant;
import org.hzero.file.infra.constant.HfleMessageConstant;
import org.hzero.mybatis.common.query.Where;
import org.hzero.starter.keyencrypt.core.Encrypt;
import org.springframework.util.Assert;

import io.choerodon.core.exception.CommonException;
import io.choerodon.mybatis.annotation.ModifyAudit;
import io.choerodon.mybatis.annotation.VersionAudit;
import io.choerodon.mybatis.domain.AuditDomain;
import io.swagger.annotations.ApiModelProperty;

/**
 * 上传文件
 *
 * @author xianzhi.chen@hand-china.com 2018-07-11 10:34:37
 */
@VersionAudit
@ModifyAudit
@Table(name = "hfle_file")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class File extends AuditDomain {

    public static final String FIELD_FILE_ID = "fileId";
    public static final String FIELD_ATTACHMENT_UUID = "attachmentUuid";
    public static final String FIELD_DIRECTORY = "directory";
    public static final String FIELD_FILE_URL = "fileUrl";
    public static final String FIELD_FILE_TYPE = "fileType";
    public static final String FIELD_FILE_NAME = "fileName";
    public static final String FIELD_FILE_SIZE = "fileSize";
    public static final String FIELD_BUCKET_NAME = "bucketName";
    public static final String FIELD_FILE_KEY = "fileKey";
    public static final String FIELD_TENANT_ID = "tenantId";
    public static final String FIELD_MD5 = "md5";
    public static final String FIELD_STORAGE_CODE = "storageCode";
    public static final String FIELD_SERVER_CODE = "serverCode";

    //
    // 业务方法(按public protected private顺序排列)
    // ------------------------------------------------------------------------------

    /**
     * 初始化文件Key
     *
     * @param suffixFileName uuid+文件名
     */
    public void initFileKey(String suffixFileName) {
        this.fileKey = this.directory + this.tenantId + HfleConstant.DIRECTORY_SEPARATOR + suffixFileName;
    }

    public void validateSize(StoreService service) {
        List<String> urls = new ArrayList<>();
        urls.add(fileUrl);
        if (fileUrl.length() > HfleConstant.Digital.FOUR_HUNDRED_AND_EIGHTY) {
            // 文件url超长，删除已上传的文件
            service.deleteFile(urls, tenantId, bucketName, attachmentUuid);
            throw new CommonException(HfleMessageConstant.ERROR_FILE_NAME_TOO_LONG);
        }
    }

    //
    // 数据库字段
    // ------------------------------------------------------------------------------


    @Id
    @GeneratedValue
    @ApiModelProperty("表ID，主键，供其他表做外键")
    @Encrypt
    private Long fileId;
    @Length(max = 50)
    @ApiModelProperty("附件集UUID")
    @Where
    private String attachmentUuid;
    @Length(max = 60)
    @ApiModelProperty("上传目录")
    private String directory;
    @Length(max = 480)
    @ApiModelProperty("文件地址")
    private String fileUrl;
    @Length(max = 120)
    @ApiModelProperty("文件类型")
    private String fileType;
    @ApiModelProperty("文件名称")
    @Length(max = 240)
    private String fileName;
    @ApiModelProperty("文件大小")
    private Long fileSize;
    @NotBlank(message = "文件Bucket不能为空")
    @Length(max = 60)
    @ApiModelProperty("文件目录")
    private String bucketName;
    @ApiModelProperty("对象KEY")
    @Length(max = 480)
    @Where
    private String fileKey;
    @NotNull(message = "租户不能为空")
    @ApiModelProperty("租户Id")
    @Where
    private Long tenantId;
    @Length(max = 60)
    @ApiModelProperty("文件MD5")
    private String md5;
    @ApiModelProperty("存储编码")
    private String storageCode;
    @ApiModelProperty("来源类型")
    @LovValue("HFLE.SERVER_PROVIDER")
    @Where
    private String sourceType;
    @ApiModelProperty("服务器编码，hpfm_server.server_code")
    @Pattern(regexp = Regexs.CODE_UPPER)
    private String serverCode;

    //
    // 非数据库字段
    // ------------------------------------------------------------------------------

    @Transient
    private String tenantName;
    @Transient
    private String realName;
    @Transient
    private String sourceTypeMeaning;
    @Transient
    private String tableName;
    //
    // getter/setter
    // ------------------------------------------------------------------------------


    public Long getFileId() {
        return fileId;
    }

    public File setFileId(Long fileId) {
        this.fileId = fileId;
        return this;
    }

    public String getAttachmentUuid() {
        return attachmentUuid;
    }

    public File setAttachmentUuid(String attachmentUuid) {
        this.attachmentUuid = attachmentUuid;
        return this;
    }

    public String getDirectory() {
        return directory;
    }

    /**
     * 初始化 目录字段(支持多级目录)    xxx/
     */
    public File setDirectory(String directory) {
        if (StringUtils.isBlank(directory)) {
            this.directory = StringUtils.EMPTY;
        } else {
            for (String item : HfleConstant.getForbiddenSymbols()) {
                Assert.isTrue(!directory.contains(item), HfleMessageConstant.ERROR_DIRECTORY_FORBIDDEN);
            }
            String[] dirs = directory.split(HfleConstant.DIRECTORY_SEPARATOR);
            StringBuilder builder = new StringBuilder();
            for (String item : dirs) {
                if (StringUtils.isNotBlank(item)) {
                    builder.append(item).append(HfleConstant.DIRECTORY_SEPARATOR);
                }
            }
            this.directory = String.valueOf(builder);
        }
        return this;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public File setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
        return this;
    }

    public String getFileType() {
        return fileType;
    }

    public File setFileType(String fileType) {
        this.fileType = fileType;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public File setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public File setFileSize(Long fileSize) {
        this.fileSize = fileSize;
        return this;
    }

    public String getBucketName() {
        return bucketName;
    }

    public File setBucketName(String bucketName) {
        this.bucketName = bucketName;
        return this;
    }

    public String getFileKey() {
        return fileKey;
    }

    public File setFileKey(String fileKey) {
        this.fileKey = fileKey;
        return this;
    }

    public Long getTenantId() {
        return tenantId;
    }

    public File setTenantId(Long tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    public String getMd5() {
        return md5;
    }

    public File setMd5(String md5) {
        this.md5 = md5;
        return this;
    }

    public String getTenantName() {
        return tenantName;
    }

    public File setTenantName(String tenantName) {
        this.tenantName = tenantName;
        return this;
    }

    public String getRealName() {
        return realName;
    }

    public File setRealName(String realName) {
        this.realName = realName;
        return this;
    }

    public String getStorageCode() {
        return storageCode;
    }

    public File setStorageCode(String storageCode) {
        this.storageCode = storageCode;
        return this;
    }

    public String getSourceType() {
        return sourceType;
    }

    public File setSourceType(String sourceType) {
        this.sourceType = sourceType;
        return this;
    }

    public String getServerCode() {
        return serverCode;
    }

    public File setServerCode(String serverCode) {
        this.serverCode = serverCode;
        return this;
    }

    public String getSourceTypeMeaning() {
        return sourceTypeMeaning;
    }

    public File setSourceTypeMeaning(String sourceTypeMeaning) {
        this.sourceTypeMeaning = sourceTypeMeaning;
        return this;
    }

    public String getTableName() {
        return tableName;
    }

    public File setTableName(String tableName) {
        this.tableName = tableName;
        return this;
    }
}
