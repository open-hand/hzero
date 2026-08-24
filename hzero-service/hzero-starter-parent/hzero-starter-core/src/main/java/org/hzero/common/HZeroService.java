package org.hzero.common;

import io.choerodon.core.convertor.ApplicationContextHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


/**
 * HZero服务常量，服务名称常量采用变量方式，
 *
 * @author bojiangzhou 2018/08/10
 */
public final class HZeroService {

    private static Environment environment;

    static {
        ApplicationContextHelper.asyncStaticSetter(Environment.class, HZeroService.class, "environment");
    }

    public static String getRealName(String serviceName) {
        return environment.resolvePlaceholders(serviceName);
    }

    //
    // 基础服务
    // ------------------------------------------------------------------------------

    /**
     * HZero 注册中心
     */
    @Component
    public static class Register {
        public static final String NAME = "${hzero.service.register.name:hzero-register}";
        public static final String CODE = "hreg";
        public static final String PATH = "";
        public static Integer PORT = 8000;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hreg";

        @Value("${hzero.service.register.port:8000}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.register.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hreg}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 网关服务
     */
    @Component
    public static class Gateway {
        public static final String NAME = "${hzero.service.gateway.name:hzero-gateway}";
        public static final String CODE = "hgwy";
        public static final String PATH = "";
        public static Integer PORT = 8080;
        public static Integer REDIS_DB = 4;
        public static String BUCKET_NAME = "hgwy";

        @Value("${hzero.service.gateway.port:8080}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.gateway.redis-db:4}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hgwy}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 配置中心
     */
    @Component
    public static class Config {
        public static final String NAME = "${hzero.service.config.name:hzero-config}";
        public static final String CODE = "hcnf";
        public static final String PATH = "/hcnf/**";
        public static Integer PORT = 8010;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hcnf";

        @Value("${hzero.service.config.port:8010}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.config.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hcnf}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 认证服务
     */
    @Component
    public static class Oauth {
        public static final String NAME = "${hzero.service.oauth.name:hzero-oauth}";
        public static final String CODE = "hoth";
        public static final String PATH = "/oauth/**";
        public static Integer PORT = 8020;
        public static Integer REDIS_DB = 3;
        public static String BUCKET_NAME = "hoth";

        @Value("${hzero.service.oauth.port:8020}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.oauth.redis-db:3}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hoth}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 用户身份服务
     */
    @Component
    public static class Iam {
        public static final String NAME = "${hzero.service.iam.name:hzero-iam}";
        public static final String CODE = "hiam";
        public static final String PATH = "/iam/**";
        public static Integer PORT = 8030;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hiam";

        @Value("${hzero.service.iam.port:8030}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.iam.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hiam}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 分布式服务
     */
    @Component
    public static class Asgard {
        public static final String NAME = "${hzero.service.asgard.name:hzero-asgard}";
        public static final String CODE = "hagd";
        public static final String PATH = "/hagd/**";
        public static Integer PORT = 8040;
        public static Integer REDIS_DB = 4;
        public static String BUCKET_NAME = "hagd";

        @Value("${hzero.service.asgard.port:8040}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.asgard.redis-db:4}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hagd}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero Swagger API测试服务
     */
    @Component
    public static class Swagger {
        public static final String NAME = "${hzero.service.swagger.name:hzero-swagger}";
        public static final String CODE = "hwgr";
        public static final String PATH = "/swagger/**";
        public static Integer PORT = 8050;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hwgr";

        @Value("${hzero.service.swagger.port:8050}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.swagger.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hwgr}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero Admin
     */
    @Component
    public static class Admin {
        public static final String NAME = "${hzero.service.admin.name:hzero-admin}";
        public static final String CODE = "hadm";
        public static final String PATH = "/hadm/**";
        public static Integer PORT = 8060;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hadm";

        @Value("${hzero.service.admin.port:8060}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.admin.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hadm}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }


    //
    // 平台服务
    // ------------------------------------------------------------------------------

    /**
     * HZero 平台服务
     */
    @Component
    public static class Platform {
        public static final String NAME = "${hzero.service.platform.name:hzero-platform}";
        public static final String CODE = "hpfm";
        public static final String PATH = "/hpfm/**";
        public static Integer PORT = 8100;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hpfm";

        @Value("${hzero.service.platform.port:8100}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.platform.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hpfm}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 文件服务
     */
    @Component
    public static class File {
        public static final String NAME = "${hzero.service.file.name:hzero-file}";
        public static final String CODE = "hfle";
        public static final String PATH = "/hfle/**";
        public static Integer PORT = 8110;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hfle";

        @Value("${hzero.service.file.port:8110}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.file.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hfle}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 消息服务
     */
    @Component
    public static class Message {
        public static final String NAME = "${hzero.service.message.name:hzero-message}";
        public static final String CODE = "hmsg";
        public static final String PATH = "/hmsg/**";
        public static Integer PORT = 8120;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hmsg";

        @Value("${hzero.service.message.port:8120}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.message.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hmsg}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 调度服务
     */
    @Component
    public static class Scheduler {
        public static final String NAME = "${hzero.service.scheduler.name:hzero-scheduler}";
        public static final String CODE = "hsdr";
        public static final String PATH = "/hsdr/**";
        public static Integer PORT = 8130;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hsdr";

        @Value("${hzero.service.scheduler.port:8130}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.scheduler.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hsdr}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 导入服务
     */
    @Component
    public static class Import {
        public static final String NAME = "${hzero.service.import.name:hzero-import}";
        public static final String CODE = "himp";
        public static final String PATH = "/himp/**";
        public static Integer PORT = 8140;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "himp";

        @Value("${hzero.service.import.port:8140}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.import.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:himp}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 接口服务
     */
    @Component
    public static class Interface {
        public static final String NAME = "${hzero.service.interface.name:hzero-interface}";
        public static final String CODE = "hitf";
        public static final String PATH = "/hitf/**";
        public static Integer PORT = 8150;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hitf";

        @Value("${hzero.service.interface.port:8150}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.interface.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hitf}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 数据传输服务
     */
    @Component
    public static class Transfer {
        public static final String NAME = "${hzero.service.transfer.name:hzero-transfer}";
        public static final String CODE = "hdtt";
        public static final String PATH = "/hdtt/**";
        public static Integer PORT = 8180;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hdtt";

        @Value("${hzero.service.transfer.port:8180}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.transfer.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hdtt}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 数据仓库服务
     */
    @Component
    @Deprecated
    public static class Warehouse {
        public static final String NAME = "${hzero.service.dw.name:hzero-dw}";
        public static final String CODE = "hdtw";
        public static final String PATH = "/hdtw/**";
        public static Integer PORT = 8190;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hdtw";

        @Value("${hzero.service.dw.port:8190}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.dw.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hdtw}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 门户服务
     */
    @Component
    @Deprecated
    public static class Portal {
        public static final String NAME = "${hzero.service.portal.name:hzero-portal}";
        public static final String CODE = "hptl";
        public static final String PATH = "/hptl/**";
        public static Integer PORT = 8200;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hptl";

        @Value("${hzero.service.portal.port:8200}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.portal.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hptl}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 报表服务
     */
    @Component
    public static class Report {
        public static final String NAME = "${hzero.service.report.name:hzero-report}";
        public static final String CODE = "hrpt";
        public static final String PATH = "/hrpt/**";
        public static Integer PORT = 8210;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hrpt";

        @Value("${hzero.service.report.port:8210}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.report.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hrpt}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * 工作流（Plus）
     */
    @Component
    public static class WorkflowPlus {
        public static final String NAME = "${hzero.service.workflow-plus.name:hzero-workflow-plus}";
        public static final String CODE = "hwfp";
        public static final String PATH = "/hwfp/**";
        public static Integer PORT = 8220;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hwfp";

        @Value("${hzero.service.workflow-plus.port:8220}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.workflow-plus.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hwfp}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 自然语言处理
     */
    @Component
    public static class Nlp {
        public static final String NAME = "${hzero.service.nlp.name:hzero-nlp}";
        public static final String CODE = "hnlp";
        public static final String PATH = "/hnlp/**";
        public static Integer PORT = 8230;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hnlp";

        @Value("${hzero.service.nlp.port:8230}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.nlp.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hnlp}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 监控服务
     */
    @Component
    public static class Monitor {
        public static final String NAME = "${hzero.service.monitor.name:hzero-monitor}";
        public static final String CODE = "hmnt";
        public static final String PATH = "/hmnt/**";
        public static Integer PORT = 8260;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hmnt";

        @Value("${hzero.service.monitor.port:8260}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.monitor.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hmnt}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 支付服务
     */
    @Component
    public static class Pay {
        public static final String NAME = "${hzero.service.pay.name:hzero-pay}";
        public static final String CODE = "hpay";
        public static final String PATH = "/hpay/**";
        public static Integer PORT = 8250;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hpay";

        @Value("${hzero.service.pay.port:8250}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.pay.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hpay}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 低代码编程
     */
    @Component
    public static class LowLowCode {
        public static final String NAME = "${hzero.service.low-code.name:hzero-low-code}";
        public static final String CODE = "hlcd";
        public static final String PATH = "/hlcd/**";
        public static Integer PORT = 8280;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hlcd";

        @Value("${hzero.service.low-code.port:8280}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.low-code.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hlcd}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 图像文字识别
     */
    @Component
    public static class Ocr {
        public static final String NAME = "${hzero.service.ocr.name:hzero-ocr}";
        public static final String CODE = "hocr";
        public static final String PATH = "/hocr/**";
        public static Integer PORT = 8290;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hocr";

        @Value("${hzero.service.ocr.port:8290}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.ocr.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hocr}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 发票
     */
    @Component
    public static class Invoice {
        public static final String NAME = "${hzero.service.invoice.name:hzero-invoice}";
        public static final String CODE = "hivc";
        public static final String PATH = "/hivc/**";
        public static Integer PORT = 8015;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hivc";

        @Value("${hzero.service.invoice.port:8015}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.invoice.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hivc}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero 即时通讯
     */
    @Component
    public static class Im {
        public static final String NAME = "${hzero.service.im.name:hzero-im}";
        public static final String CODE = "hims";
        public static final String PATH = "/hims/**";
        public static Integer PORT = 8025;
        public static Integer REDIS_DB = 8;
        public static String BUCKET_NAME = "hims";

        @Value("${hzero.service.invoice.port:8025}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.invoice.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hims}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero WebExcel
     */
    @Component
    public static class WebExcel {
        public static final String NAME = "${hzero.service.webexcel.name:hzero-webexcel}";
        public static final String CODE = "hexl";
        public static final String PATH = "/hexl/**";
        public static Integer PORT = 8045;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hexl";

        @Value("${hzero.service.webexcel.port:8045}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.webexcel.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hexl}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero Iot
     */
    @Component
    public static class Iot {
        public static final String NAME = "${hzero.service.iot.name:hzero-iot}";
        public static final String CODE = "hiot";
        public static final String PATH = "/hiot/**";
        public static Integer PORT = 8070;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hiot";

        @Value("${hzero.service.iot.port:8070}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.iot.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hiot}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero search
     */
    @Component
    public static class Search {
        public static final String NAME = "${hzero.service.search.name:hzero-search}";
        public static final String CODE = "hsrh";
        public static final String PATH = "/hsrh/**";
        public static Integer PORT = 8090;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hsrh";

        @Value("${hzero.service.search.port:8090}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.search.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hsrh}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero dpm
     */
    @Component
    public static class Dpm {
        public static final String NAME = "${hzero.service.dpm.name:hzero-dpm}";
        public static final String CODE = "hdpm";
        public static final String PATH = "/hdpm/**";
        public static Integer PORT = 8055;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hdpm";

        @Value("${hzero.service.dpm.port:8055}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.dpm.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hdpm}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero Rule Engine
     */
    @Component
    public static class RuleEngine {
        public static final String NAME = "${hzero.service.rule-engine.name:hzero-rule-engine}";
        public static final String CODE = "hres";
        public static final String PATH = "/hres/**";
        public static Integer PORT = 8065;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hres";

        @Value("${hzero.service.rule-engine.port:8065}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.rule-engine.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hres}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero Ebank
     */
    @Component
    public static class Ebank {
        public static final String NAME = "${hzero.service.ebank.name:hzero-ebank}";
        public static final String CODE = "hebk";
        public static final String PATH = "/hebk/**";
        public static Integer PORT = 8075;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hebk";

        @Value("${hzero.service.ebank.port:8075}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.ebank.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hebk}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero modeler
     */
    @Component
    public static class Modeler {
        public static final String NAME = "${hzero.service.modeler.name:hzero-modeler}";
        public static final String CODE = "hmde";
        public static final String PATH = "/hmde/**";
        public static Integer PORT = 8085;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hmde";

        @Value("${hzero.service.modeler.port:8085}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.modeler.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hmde}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * HZero LowCode
     */
    @Component
    public static class Lowcode {
        public static final String NAME = "${hzero.service.hlod.name:hzero-lowcode}";
        public static final String CODE = "hlod";
        public static final String PATH = "/hlod/**";
        public static Integer PORT = 8095;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hlod";

        @Value("${hzero.service.lowcode.port:8095}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.lowcode.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hlod}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * 计费服务
     * HZero Charge
     */
    @Component
    public static class Charge {
        public static final String NAME = "${hzero.service.hchg.name:hzero-charge}";
        public static final String CODE = "hchg";
        public static final String PATH = "/hchg/**";
        public static Integer PORT = 8035;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hchg";

        @Value("${hzero.service.charge.port:8035}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.charge.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hchg}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * 预警服务
     * HZero ALERT
     */
    @Component
    public static class Alert {
        public static final String NAME = "${hzero.service.halt.name:hzero-alert}";
        public static final String CODE = "halt";
        public static final String PATH = "/halt/**";
        public static Integer PORT = 8125;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "halt";

        @Value("${hzero.service.halt.port:8125}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.halt.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:halt}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    /**
     * 新版分发服务
     * hzero-datasync
     */
    @Component
    public static class DataSync {
        public static final String NAME = "${hzero.service.hdsc.name:hzero-datasync}";
        public static final String CODE = "hdsc";
        public static final String PATH = "/hdsc/**";
        public static Integer PORT = 8125;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hdsc";

        @Value("${hzero.service.hdsc.port:8125}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.hdsc.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hdsc}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    @Component
    public static class Frontal {
        public static final String NAME = "${hzero.service.frontal.name:hzero-frontal}";
        public static final String CODE = "hfnt";
        public static final String PATH = "/hfnt/**";
        public static Integer PORT = 8155;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hfnt";

        public Frontal() {
        }

        @Value("${hzero.service.frontal.port:8155}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.frontal.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hfnt}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    @Component
    public static class Orchestration {
        public static final String NAME = "${hzero.service.orchestration.name:hzero-orchestration}";
        public static final String CODE = "horc";
        public static final String PATH = "/horc/**";
        public static Integer PORT = 8185;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "horc";

        public Orchestration() {
        }

        @Value("${hzero.service.orchestration.port:8185}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.orchestration.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:horc}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    @Component
    public static class OrchestrationManager {
        public static final String NAME = "${hzero.service.orchestration-manager.name:hzero-orchestration-manager}";
        public static final String CODE = "hrcm";
        public static final String PATH = "/hrcm/**";
        public static Integer PORT = 8195;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hrcm";

        public OrchestrationManager() {
        }

        @Value("${hzero.service.orchestration-manager.port:8195}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.orchestration-manager.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hrcm}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }


    @Component
    public static class OrchestrationWorkloader {
        public static final String NAME = "${hzero.service.orchestration-workloader.name:hzero-orchestration-workloader}";
        public static final String CODE = "hrcw";
        public static final String PATH = "/hrcw/**";
        public static Integer PORT = 8205;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hrcw";

        public OrchestrationWorkloader() {
        }

        @Value("${hzero.service.orchestration-workloader.port:8205}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.orchestration-workloader.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hrcw}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }

    @Component
    public static class PayGateway {
        public static final String NAME = "${hzero.service.pay-gateway.name:hzero-pay-gateway}";
        public static final String CODE = "hpgw";
        public static final String PATH = "/hpgw/**";
        public static Integer PORT = 8245;
        public static Integer REDIS_DB = 1;
        public static String BUCKET_NAME = "hpgw";

        public PayGateway() {
        }

        @Value("${hzero.service.pay-gateway.port:8245}")
        public void setPort(Integer port) {
            PORT = port;
        }

        @Value("${hzero.service.pay-gateway.redis-db:1}")
        public void setRedisDb(Integer redisDb) {
            REDIS_DB = redisDb;
        }

        @Value("${hzero.service.bucket-name:hpgw}")
        public void setBucketName(String bucketName) {
            BUCKET_NAME = bucketName;
        }
    }
}
