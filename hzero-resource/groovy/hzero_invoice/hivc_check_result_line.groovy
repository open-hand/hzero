package script.db

databaseChangeLog(logicalFilePath: 'script/db/hivc_check_result_line.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-26-hivc_check_result_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hivc_check_result_line_s', startValue:"1")
        }
        createTable(tableName: "hivc_check_result_line", remarks: "查验结果行") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "result_id", type: "bigint",  remarks: "查验结果ID")   
            column(name: "detail_no", type: "varchar(" + 120 * weight + ")",  remarks: "明细编号")   
            column(name: "goods_name", type: "varchar(" + 240 * weight + ")",  remarks: "商品/服务名称")   
            column(name: "specification_model", type: "varchar(" + 240 * weight + ")",  remarks: "规格型号")   
            column(name: "unit", type: "varchar(" + 30 * weight + ")",  remarks: "单位")   
            column(name: "quantity", type: "decimal(20,6)",  remarks: "数量")
            column(name: "unit_price", type: "decimal(20,10)",  remarks: "单价")
            column(name: "amount", type: "decimal(20,2)",  remarks: "金额")
            column(name: "tax_rate", type: "varchar(" + 20 * weight + ")",  remarks: "税率")   
            column(name: "tax_amount", type: "decimal(20,2)",  remarks: "税额")
            column(name: "car_type", type: "varchar(" + 30 * weight + ")",  remarks: "类型(14)")   
            column(name: "plate_no", type: "varchar(" + 30 * weight + ")",  remarks: "车牌号(14)")   
            column(name: "traffic_date_start", type: "datetime",  remarks: "通行日期起(14)")   
            column(name: "traffic_date_end", type: "datetime",  remarks: "通行日期止(14)")   
            column(name: "expense_item", type: "varchar(" + 240 * weight + ")",  remarks: "费用项目(02)")   
            column(name: "special_mark", type: "varchar(" + 30 * weight + ")",  remarks: "特殊政策标记")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hivc_check_result_line", indexName: "hivc_check_result_line_n1") {
            column(name: "result_id")
        }

    }
}