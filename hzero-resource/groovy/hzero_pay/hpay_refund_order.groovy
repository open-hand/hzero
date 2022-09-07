package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpay_refund_order.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-23-hpay_refund_order") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpay_refund_order_s', startValue:"1")
        }
        createTable(tableName: "hpay_refund_order", remarks: "退款订单") {
            column(name: "refund_order_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "refund_order_num", type: "varchar(" + 60 * weight + ")",  remarks: "退款单号，每次进行退款的单号，此处唯一")  {constraints(nullable:"false")}  
            column(name: "payment_order_num", type: "varchar(" + 60 * weight + ")",  remarks: "支付订单号,hpay_payment_order.payment_order_num")  {constraints(nullable:"false")}  
            column(name: "merchant_order_num", type: "varchar(" + 60 * weight + ")",  remarks: "支付订单号,hpay_payment_order.merchant_order_num")  {constraints(nullable:"false")}  
            column(name: "refund_amount", type: "decimal(20,2)",  remarks: "金额（单位分）")  {constraints(nullable:"false")}  
            column(name: "total_amount", type: "decimal(20,2)",  remarks: "订单总金额")   
            column(name: "refund_reason", type: "varchar(" + 480 * weight + ")",  remarks: "退款原因")   
            column(name: "status", type: "varchar(" + 30 * weight + ")",  remarks: "状态，HPAY.REFUND_STATUS 待退款UNREF/退款中REFUNDING/已退款REFUNDED/退款失败FAILED/退款拒接REFUSED")  {constraints(nullable:"false")}  
            column(name: "refund_way", type: "varchar(" + 30 * weight + ")",  remarks: "退款方式，HPAY.REFUND_WAY NONE/不确认结果,PENDING/等待手动处理")  {constraints(nullable:"false")}  
            column(name: "client_ip", type: "varchar(" + 50 * weight + ")",  remarks: "客户端IP(客户端IP,在网关页面获取)")   
            column(name: "client_device", type: "varchar(" + 120 * weight + ")",  remarks: "客户端设备(客户端设备,在网关页面获取)")   
            column(name: "channel_resp_code", type: "varchar(" + 60 * weight + ")",  remarks: "渠道退款错误码")   
            column(name: "channel_resp_msg", type: "varchar(" + 480 * weight + ")",  remarks: "渠道退款错误信息")   
            column(name: "channel_trade_no", type: "varchar(" + 60 * weight + ")",  remarks: "支付流水号")   
            column(name: "refund_datetime", type: "datetime",  remarks: "订单退款成功时间")   
            column(name: "notify_url", type: "varchar(" + 480 * weight + ")",  remarks: "后台异步通知url")   
            column(name: "ext_param1", type: "varchar(" + 120 * weight + ")",  remarks: "拓展参数1")   
            column(name: "ext_param2", type: "varchar(" + 120 * weight + ")",  remarks: "拓展参数2")   
            column(name: "remark", type: "varchar(" + 480 * weight + ")",  remarks: "备注说明")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpay_refund_order", indexName: "hpay_refund_order_n1") {
            column(name: "merchant_order_num")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames:"refund_order_num",tableName:"hpay_refund_order",constraintName: "hpay_refund_order_u1")
    }
}