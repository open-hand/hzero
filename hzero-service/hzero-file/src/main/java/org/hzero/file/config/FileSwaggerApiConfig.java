package org.hzero.file.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.service.Tag;
import springfox.documentation.spring.web.plugins.Docket;

/**
 * Swagger 描述配置
 *
 * @author xianzhi.chen@hand-china.com 2018年7月31日上午11:20:49
 */
@Configuration
public class FileSwaggerApiConfig {
    public static final String FILE_CAPACITY_CONFIG = "File Capacity Config";
    public static final String FILE_CAPACITY_CONFIG_SITE = "File Capacity Config(Site Level)";

    public static final String FILE_SERVICE = "File Service";
    public static final String FILE_SERVICE_SITE = "File Service(Site Level)";

    public static final String FILE_SERVICE_V2 = "File Service V2";
    public static final String FILE_SERVICE_SITE_V2 = "File Service V2(Site Level)";

    public static final String FILE_SUMMARY_LIST = "File Summary List";
    public static final String FILE_SUMMARY_LIST_SITE = "File Summary List(Site Level)";

    public static final String FILE_STORAGE_CONFIG = "File Storage Config";
    public static final String FILE_STORAGE_CONFIG_SITE = "File Storage Config(Site Level)";

    public static final String FILE_STORAGE_CONFIG_V2 = "File Storage Config V2";
    public static final String FILE_STORAGE_CONFIG_SITE_V2 = "File Storage Config V2(Site Level)";

    public static final String FILE_UPLOAD_CONFIG = "File Upload Config";
    public static final String FILE_UPLOAD_CONFIG_SITE = "File Upload Config(Site Level)";

    public static final String FILE_PREVIEW = "File Preview";
    public static final String FILE_PREVIEW_SITE = "File Preview(Site Level)";

    public static final String ONLY_OFFICE = "Only Office";
    public static final String FILE_EDIT_LOG = "File Edit Log";
    public static final String FILE_EDIT_LOG_SITE = "File Edit Log(Site Level)";

    public static final String SERVER_CONFIG = "Server Upload Config";
    public static final String SERVER_CONFIG_SITE = "Server Upload Config(Site Level)";

    public static final String SERVER_FILE = "Server File";
    public static final String SERVER_FILE_SITE = "Server File(Site Level)";

    public static final String FRAGMENT_UPLOAD = "Fragment Upload";
    public static final String FRAGMENT_UPLOAD_SITE = "Fragment Upload(Site Level)";

    public static final String WATERMARK_CONFIG = "Watermark Config";
    public static final String WATERMARK_CONFIG_SITE = "Watermark Config(Site Level)";

    public static final String WATERMARK = "Watermark";
    public static final String WATERMARK_SITE = "Watermark(Site Level)";

    @Autowired
    public FileSwaggerApiConfig(Docket docket) {
        docket.tags(
                new Tag(FILE_CAPACITY_CONFIG, "文件容量配置"),
                new Tag(FILE_CAPACITY_CONFIG_SITE, "文件容量配置(平台级)"),

                new Tag(FILE_SERVICE, "文件服务"),
                new Tag(FILE_SERVICE_SITE, "文件服务(平台级)"),

                new Tag(FILE_SERVICE_V2, "文件服务 V2"),
                new Tag(FILE_SERVICE_SITE_V2, "文件服务 V2(平台级)"),

                new Tag(FILE_SUMMARY_LIST, "文件汇总查询"),
                new Tag(FILE_SUMMARY_LIST_SITE, "文件汇总查询(平台级)"),

                new Tag(FILE_STORAGE_CONFIG, "文件存储配置"),
                new Tag(FILE_STORAGE_CONFIG_SITE, "文件存储配置(平台级)"),

                new Tag(FILE_STORAGE_CONFIG_V2, "文件存储配置V2"),
                new Tag(FILE_STORAGE_CONFIG_SITE_V2, "文件存储配置V2(平台级)"),

                new Tag(FILE_UPLOAD_CONFIG, "文件上传配置"),
                new Tag(FILE_UPLOAD_CONFIG_SITE, "文件上传配置(平台级)"),

                new Tag(FILE_PREVIEW, "文件预览"),
                new Tag(FILE_PREVIEW_SITE, "文件预览(平台级)"),

                new Tag(ONLY_OFFICE, "Only Office在线编辑"),
                new Tag(FILE_EDIT_LOG, "文件编辑日志"),
                new Tag(FILE_EDIT_LOG_SITE, "文件编辑日志(平台级)"),

                new Tag(SERVER_CONFIG, "服务器上传配置"),
                new Tag(SERVER_CONFIG_SITE, "服务器上传配置(平台级)"),

                new Tag(SERVER_FILE, "服务器文件"),
                new Tag(SERVER_FILE_SITE, "服务器文件(平台级)"),

                new Tag(FRAGMENT_UPLOAD, "分片上传"),
                new Tag(FRAGMENT_UPLOAD_SITE, "分片上传(平台级)"),

                new Tag(WATERMARK_CONFIG, "水印配置"),
                new Tag(WATERMARK_CONFIG_SITE, "水印配置(平台级)"),

                new Tag(WATERMARK, "水印"),
                new Tag(WATERMARK_SITE, "水印(平台级)")
        );
    }

}
