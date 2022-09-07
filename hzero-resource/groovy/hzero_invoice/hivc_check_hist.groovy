package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_check_hist.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_check_hist") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_check_hist_s', startValue:"1")
        }
        createTable(tableName: "hivc_check_hist", remarks: "查验历史") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "invoice_type", type: "varchar(" + 30 * weight + ")",  remarks: "发票类型")   
            column(name: "invoice_code", type: "varchar(" + 60 * weight + ")",  remarks: "发票代码")   
            column(name: "invoice_no", type: "varchar(" + 60 * weight + ")",  remarks: "发票编码")   
            column(name: "invoice_date", type: "varchar(" + 30 * weight + ")",  remarks: "开票时间")   
            column(name: "invoice_amount", type: "varchar(" + 240 * weight + ")",  remarks: "发票金额")   
            column(name: "check_code", type: "varchar(" + 30 * weight + ")",  remarks: "发票校验码")   
            column(name: "result_id", type: "bigint",  remarks: "查验结果id，查验失败时为空")   
            column(name: "image_url", type: "varchar(" + 480 * weight + ")",  remarks: "ocr查验图片地址")   
            column(name: "status", type: "varchar(" + 30 * weight + ")",  remarks: "状态")   
            column(name: "message", type: "varchar(" + 240 * weight + ")",  remarks: "错误信息")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "response_code", type: "varchar(" + 30 * weight + ")",  remarks: "")   

        }
   createIndex(tableName: "hivc_check_hist", indexName: "hivc_check_hist_n1") {
            column(name: "invoice_code")
            column(name: "invoice_no")
            column(name: "tenant_id")
            column(name: "creation_date")
        }

    }
}