package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_tick_result.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_tick_result") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_tick_result_s', startValue:"1")
        }
        createTable(tableName: "hivc_tick_result", remarks: "发票勾选结果") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "buyer_no", type: "varchar(" + 30 * weight + ")",  remarks: "纳税人识别号")   
            column(name: "batch_no", type: "varchar(" + 100 * weight + ")",  remarks: "批次号")   
            column(name: "tick_count", type: "int",  remarks: "勾选数量")   
            column(name: "all_success", type: "tinyint",   defaultValue:"0",   remarks: "是否全部勾选成功")  {constraints(nullable:"false")}  
            column(name: "tick_date", type: "date",  remarks: "勾选请求日期")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hivc_tick_result", indexName: "hivc_tick_result_n1") {
            column(name: "buyer_no")
        }

        addUniqueConstraint(columnNames:"batch_no",tableName:"hivc_tick_result",constraintName: "hivc_tick_result_u1")
    }
}