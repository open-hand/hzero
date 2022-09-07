package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_quota_invoice.groovy') {
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_quota_invoice") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hocr_quota_invoice_s', startValue:"1")
        }
        createTable(tableName: "hocr_quota_invoice", remarks: "定额发票识别记录表") {
            column(name: "quota_invoice_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "code", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "发票代码")  {constraints(nullable:"false")}  
            column(name: "number", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "发票号码")  {constraints(nullable:"false")}  
            column(name: "amount", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "发票金额")  {constraints(nullable:"false")}  
            column(name: "location", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "地址")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
    }
	changeSet(author: "liang.li05@hand-china.com", id: "2020-04-22-hocr_quota_invoice") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }

        dropNotNullConstraint(tableName: "hocr_quota_invoice", columnName: "code", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_quota_invoice", columnName: "number", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_quota_invoice", columnName: "amount", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_quota_invoice", columnName: "location", columnDataType: "varchar(" + 30 * weight + ")")

        renameColumn(tableName: "hocr_quota_invoice", oldColumnName: "number", newColumnName: "invoice_number", columnDataType: "varchar(" + 30 * weight + ")")
		renameColumn(tableName: "hocr_quota_invoice", oldColumnName: "location", newColumnName: "address", columnDataType: "varchar(" + 30 * weight + ")")
    }
}