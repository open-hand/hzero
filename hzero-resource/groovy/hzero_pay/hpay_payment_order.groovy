package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpay_payment_order.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-23-hpay_payment_order") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpay_payment_order_s', startValue:"1")
        }
        createTable(tableName: "hpay_payment_order", remarks: "支付订单") {
            column(name: "payment_order_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "payment_order_num", type: "varchar(" + 60 * weight + ")",  remarks: "支付单号，每次进行支付的单号，此处唯一")  {constraints(nullable:"false")}
            column(name: "channel_merchant_num", type: "varchar(" + 60 * weight + ")",  remarks: "渠道商户编码")
            column(name: "channel_code", type: "varchar(" + 30 * weight + ")",  remarks: "渠道，HPAY.PAYMENT_CHANNEL,ALIPAY 支付宝、WECHAT 微信、UNIONPAY 银联")  {constraints(nullable:"false")}
            column(name: "channel_trx_type", type: "varchar(" + 60 * weight + ")",  remarks: "渠道事务类型")  {constraints(nullable:"false")}
            column(name: "channel_open_id", type: "varchar(" + 60 * weight + ")",  remarks: "公共号ID")
            column(name: "merchant_order_num", type: "varchar(" + 60 * weight + ")",  remarks: "商户支付订单号")  {constraints(nullable:"false")}
            column(name: "payment_subject", type: "varchar(" + 60 * weight + ")",  remarks: "商品名称/交易标题/订单标题/订单关键字等")  {constraints(nullable:"false")}
            column(name: "payment_description", type: "varchar(" + 240 * weight + ")",  remarks: "商品名称/交易标题/订单标题/订单描述等")
            column(name: "payment_customer", type: "varchar(" + 60 * weight + ")",  remarks: "支付用户")
            column(name: "currency_code", type: "varchar(" + 30 * weight + ")",  remarks: "币种")  {constraints(nullable:"false")}
            column(name: "payment_amount", type: "decimal(20,2)",  remarks: "金额（单位分）")  {constraints(nullable:"false")}
            column(name: "status", type: "varchar(" + 30 * weight + ")",  remarks: "状态，HPAY.PAYMENT_STATUS 待支付UNPAID/已支付PAID/已取消CANCELLED")  {constraints(nullable:"false")}
            column(name: "client_ip", type: "varchar(" + 50 * weight + ")",  remarks: "客户端IP(客户端IP,在网关页面获取)")
            column(name: "client_device", type: "varchar(" + 120 * weight + ")",  remarks: "客户端设备(客户端设备,在网关页面获取)")
            column(name: "order_referer_url", type: "varchar(" + 240 * weight + ")",  remarks: "从哪个页面链接过来的(可用于防诈骗)")
            column(name: "return_url", type: "varchar(" + 480 * weight + ")",  remarks: "页面回调通知url")
            column(name: "notify_url", type: "varchar(" + 480 * weight + ")",  remarks: "后台异步通知url")
            column(name: "cancel_reason", type: "varchar(" + 480 * weight + ")",  remarks: "订单撤销原因")
            column(name: "order_period", type: "int",  remarks: "订单有效期(单位分钟)")
            column(name: "expire_time", type: "datetime",  remarks: "到期时间")
            column(name: "channel_trade_no", type: "varchar(" + 60 * weight + ")",  remarks: "支付流水号")
            column(name: "payment_datetime", type: "datetime",  remarks: "订单支付成功时间")
            column(name: "channel_resp_code", type: "varchar(" + 60 * weight + ")",  remarks: "渠道支付错误码")
            column(name: "channel_resp_msg", type: "varchar(" + 480 * weight + ")",  remarks: "渠道支付错误信息")
            column(name: "buyer_pay_amount", type: "decimal(20,2)",  remarks: "买家支付金额")
            column(name: "receipt_amount", type: "decimal(20,2)",  remarks: "商户实收金额")
            column(name: "ext_param1", type: "varchar(" + 120 * weight + ")",  remarks: "拓展参数1")
            column(name: "ext_param2", type: "varchar(" + 120 * weight + ")",  remarks: "拓展参数2")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }
   createIndex(tableName: "hpay_payment_order", indexName: "hpay_payment_order_n1") {
            column(name: "channel_code")
            column(name: "channel_trx_type")
            column(name: "tenant_id")
        }
   createIndex(tableName: "hpay_payment_order", indexName: "hpay_payment_order_n2") {
            column(name: "payment_subject")
        }
   createIndex(tableName: "hpay_payment_order", indexName: "hpay_payment_order_u2") {
            column(name: "channel_merchant_num")
            column(name: "channel_code")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"payment_order_num",tableName:"hpay_payment_order",constraintName: "hpay_payment_order_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-31-hpay_payment_order") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpay_payment_order') {
            column(name: "config_code", type: "varchar(" + 30 * weight + ")", remarks: "支付配置编码")
        }
    }
}