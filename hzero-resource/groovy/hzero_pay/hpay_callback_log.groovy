package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpay_callback_log.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-07-23-hpay_callback_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpay_callback_log_s', startValue:"1")
        }
        createTable(tableName: "hpay_callback_log", remarks: "回调日志") {
            column(name: "callback_log_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "order_type", type: "varchar(" + 30 * weight + ")",  remarks: "订单类型，PAY:支付 REFUND:退款")  {constraints(nullable:"false")}  
            column(name: "order_id", type: "bigint",  remarks: "支付或退款订单号")  {constraints(nullable:"false")}  
            column(name: "callback_url", type: "varchar(" + 480 * weight + ")",  remarks: "回调地址")  {constraints(nullable:"false")}  
            column(name: "status_flag", type: "tinyint",   defaultValue:"1",   remarks: "回调标识，1:成功 0:失败")  {constraints(nullable:"false")}  
            column(name: "callback_msg", type: "longtext",  remarks: "回调消息")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpay_callback_log", indexName: "hpay_callback_log_n1") {
            column(name: "order_type")
            column(name: "order_id")
            column(name: "status_flag")
            column(name: "tenant_id")
        }
   createIndex(tableName: "hpay_callback_log", indexName: "hpay_callback_log_n2") {
            column(name: "status_flag")
        }

    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-07-31-hpay_callback_log") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpay_callback_log') {
            column(name: "call_number", type: "int", defaultValue:"0", remarks: "回调次数") {constraints(nullable:"false")}
        }
        addColumn(tableName: 'hpay_callback_log') {
            column(name: "ext_attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpay_callback_log') {
            column(name: "ext_attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpay_callback_log') {
            column(name: "ext_attribute3", type: "varchar(" + 150 * weight + ")")
        }
    }
}