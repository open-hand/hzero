package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpay_config.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-23-hpay_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpay_config_s', startValue: "1")
        }
        createTable(tableName: "hpay_config", remarks: "支付配置") {
            column(name: "config_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "config_code", type: "varchar(" + 30 * weight + ")", remarks: "支付配置编码") { constraints(nullable: "false") }
            column(name: "config_name", type: "varchar(" + 240 * weight + ")", remarks: "支付配置名称") { constraints(nullable: "false") }
            column(name: "channel_code", type: "varchar(" + 30 * weight + ")", remarks: "渠道，HPAY.PAYMENT_CHANNEL,ALIPAY 支付宝、WECHAT 微信、UNIONPAY 银联") { constraints(nullable: "false") }
            column(name: "app_id", type: "varchar(" + 60 * weight + ")", remarks: "应用ID（支付宝、微信）")
            column(name: "mch_id", type: "varchar(" + 60 * weight + ")", remarks: "商户ID（支付宝、微信、银联）")
            column(name: "seller", type: "varchar(" + 60 * weight + ")", remarks: "收款账号（支付宝）")
            column(name: "public_key", type: "longtext", remarks: "公钥（支付宝、微信）")
            column(name: "private_key", type: "longtext", remarks: "私钥支付宝、微信）")
            column(name: "cert_pwd", type: "varchar(" + 480 * weight + ")", remarks: "证书密码（银联、微信）")
            column(name: "private_cert", type: "blob", remarks: "私有证书（银联、微信）")
            column(name: "root_cert", type: "blob", remarks: "根证书（银联）")
            column(name: "middle_cert", type: "blob", remarks: "中间层证书（银联）")
            column(name: "pc_name", type: "varchar(" + 60 * weight + ")", defaultValue: "", remarks: "私有证书名称")
            column(name: "mc_name", type: "varchar(" + 60 * weight + ")", defaultValue: "", remarks: "中级证书名称")
            column(name: "rc_name", type: "varchar(" + 60 * weight + ")", remarks: "根证书名称")
            column(name: "sign_type", type: "varchar(" + 60 * weight + ")", remarks: "签名方式")
            column(name: "return_url", type: "varchar(" + 480 * weight + ")", remarks: "页面回调URL")
            column(name: "pay_notify_url", type: "varchar(" + 480 * weight + ")", remarks: "支付异步回调URL")
            column(name: "refund_notify_url", type: "varchar(" + 240 * weight + ")", remarks: "退款异步回调URL")
            column(name: "default_flag", type: "tinyint", defaultValue: "1", remarks: "默认标识") { constraints(nullable: "false") }
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "启用标识") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID，hpfm_tenant.tenant_id") { constraints(nullable: "false") }
            column(name: "remark", type: "varchar(" + 240 * weight + ")", remarks: "备注说明")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }
        createIndex(tableName: "hpay_config", indexName: "hpay_config_n1") {
            column(name: "config_code")
            column(name: "channel_code")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames: "config_code,tenant_id", tableName: "hpay_config", constraintName: "hpay_config_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-11-25-hpay_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hpay_config') {
            column(name: "ext_param", type: "varchar(" + 480 * weight + ")", remarks: "扩展参数")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-09-29-hpay_config") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpay_config", columnName: 'ext_param', newDataType: "varchar(" + 1200 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-09-30-hpay_config") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hpay_config') {
            column(name: "cert_param", type: "varchar(" + 240 * weight + ")", remarks: "证书参数")
        }
    }
}