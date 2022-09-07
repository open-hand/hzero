package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_meta_data.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_meta_data") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_meta_data_s', startValue:"1")
        }
        createTable(tableName: "hivc_meta_data", remarks: "发票主数据") {
            column(name: "meta_data_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "invoice_no", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "发票号码")  {constraints(nullable:"false")}  
            column(name: "invoice_code", type: "varchar(" + 20 * weight + ")",   defaultValue:"",   remarks: "发票代码")  {constraints(nullable:"false")}  
            column(name: "invoice_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "发票类型(01:专票/02:货运专票/03:机动车发票/04:普票/10:电子票/11:卷票/14:通行发票/15:二手车发票)")  {constraints(nullable:"false")}  
            column(name: "invoice_date", type: "date", remarks: "开票日期")  {constraints(nullable:"false")}
            column(name: "buyer_no", type: "varchar(" + 240 * weight + ")",   defaultValue:"",   remarks: "购方纳税人识别号")  {constraints(nullable:"false")}  
            column(name: "saler_no", type: "varchar(" + 240 * weight + ")",   defaultValue:"",   remarks: "销方纳税人识别号")  {constraints(nullable:"false")}  
            column(name: "saler_name", type: "varchar(" + 240 * weight + ")",   defaultValue:"",   remarks: "销方名称")  {constraints(nullable:"false")}  
            column(name: "invoice_amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "发票金额")  {constraints(nullable:"false")}
            column(name: "tax_amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "发票税额")  {constraints(nullable:"false")}
            column(name: "valid_tax_amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "有效税额")  {constraints(nullable:"false")}
            column(name: "invoice_status", type: "int",   defaultValue:"-1",   remarks: "发票状态(0:正常/1:已失控/2:已作废/3:已红冲/4:异常/5:认证异常)")  {constraints(nullable:"false")}  
            column(name: "tick_status", type: "tinyint",   defaultValue:"0",   remarks: "勾选状态(0:未勾选 1:已勾选)")  {constraints(nullable:"false")}  
            column(name: "tick_date", type: "date", remarks: "勾选时间")  {constraints(nullable:"false")}
            column(name: "auth_status", type: "tinyint",   defaultValue:"0",   remarks: "认证状态(0:未认证 1:已认证)")  {constraints(nullable:"false")}  
            column(name: "auth_type", type: "int",   defaultValue:"-1",   remarks: "认证类型(1:抵扣 2:不抵扣)")  {constraints(nullable:"false")}  
            column(name: "auth_period", type: "varchar(" + 10 * weight + ")",   defaultValue:"190001",   remarks: "认证所属期")  {constraints(nullable:"false")}  
            column(name: "info_source", type: "int",   defaultValue:"-1",   remarks: "信息来源(0:扫描认证 1:系统推送 2:不予退税)")  {constraints(nullable:"false")}  
            column(name: "management_status", type: "tinyint",   defaultValue:"0",   remarks: "管理状态(0:正常 1:非正常)")  {constraints(nullable:"false")}  
            column(name: "stock_in_date", type: "date", remarks: "入电子抵账库日期")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hivc_meta_data", indexName: "hivc_meta_data_n1") {
            column(name: "invoice_date")
        }

        addUniqueConstraint(columnNames:"invoice_no,invoice_code",tableName:"hivc_meta_data",constraintName: "hivc_meta_data_u1")
    }
}