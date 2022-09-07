package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_auth_result_detail.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_auth_result_detail") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_auth_result_detail_s', startValue:"1")
        }
        createTable(tableName: "hivc_auth_result_detail", remarks: "发票认证统计结果") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "auth_id", type: "bigint",   defaultValue:"-1",   remarks: "hivc_auth_result表主键")  {constraints(nullable:"false")}  
            column(name: "invoice_type", type: "varchar(" + 10 * weight + ")",   defaultValue:"",   remarks: "发票类型")  {constraints(nullable:"false")}  
            column(name: "deduction_invoice_num", type: "int",   defaultValue:"0",   remarks: "抵扣发票份数")  {constraints(nullable:"false")}  
            column(name: "deduction_total_amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "抵扣总金额")  {constraints(nullable:"false")}
            column(name: "deduction_total_valid_tax", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "抵扣总有效税额")  {constraints(nullable:"false")}
            column(name: "non_deduction_invoice_num", type: "int",   defaultValue:"0",   remarks: "不抵扣发票份数")  {constraints(nullable:"false")}  
            column(name: "non_deduction_total_amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "不抵扣总金额")  {constraints(nullable:"false")}
            column(name: "non_deduction_total_valid_tax", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "不抵扣总有效税额")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}