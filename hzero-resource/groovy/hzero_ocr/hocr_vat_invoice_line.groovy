package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_vat_invoice_line.groovy') {
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_vat_invoice_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hocr_vat_invoice_line_s', startValue:"1")
        }
        createTable(tableName: "hocr_vat_invoice_line", remarks: "增值税发票识别信息行表") {
            column(name: "vat_invoice_line_id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)}
            column(name: "vat_invoice_header_id", type: "bigint",  remarks: "hocr_vat_invoice_header表主键")  {constraints(nullable:"false")}
            column(name: "name", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "商品名称")  {constraints(nullable:"false")}  
            column(name: "specification", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "规格型号")  {constraints(nullable:"false")}  
            column(name: "unit", type: "varchar(" + 255 * weight + ")",   defaultValue:"",   remarks: "单位")  {constraints(nullable:"false")}  
            column(name: "quantity", type: "decimal(20,6)",   defaultValue:"1.000000",   remarks: "数量")  {constraints(nullable:"false")}  
            column(name: "unit_price", type: "decimal(20,10)",   defaultValue:"0.0000000000",   remarks: "单价")  {constraints(nullable:"false")}  
            column(name: "amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "金额")  {constraints(nullable:"false")}  
            column(name: "tax_rate", type: "decimal(10,2)",   defaultValue:"0.00",   remarks: "税率（%）")  {constraints(nullable:"false")}  
            column(name: "tax", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "税额")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hocr_vat_invoice_line", indexName: "hocr_vat_invoice_line_n1") {
            column(name: "vat_invoice_header_id")
        }

    }
	
    changeSet(author: "liang.li05@hand-china.com", id: "2020-04-23-hocr_vat_invoice_line") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "name", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "specification", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "unit", columnDataType: "varchar(" + 255 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "quantity", columnDataType: "decimal(20,6)")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "unit_price", columnDataType: "decimal(20,10)")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "amount", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "tax_rate", columnDataType: "decimal(10,2)")
        dropNotNullConstraint(tableName: "hocr_vat_invoice_line", columnName: "tax", columnDataType: "decimal(20,2)")

        renameColumn(tableName: "hocr_vat_invoice_line", oldColumnName: "name", newColumnName: "line_name", columnDataType: "varchar(" + 255 * weight + ")")
        renameColumn(tableName: "hocr_vat_invoice_line", oldColumnName: "specification", newColumnName: "line_specification", columnDataType: "varchar(" + 255 * weight + ")")
    }
}