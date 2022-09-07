package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_taxi_receipt.groovy') {
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_taxi_receipt") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hocr_taxi_receipt_s', startValue:"1")
        }
        createTable(tableName: "hocr_taxi_receipt", remarks: "出租车识别记录表") {
            column(name: "taxi_receipt_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "invoice_code", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "发票代码")  {constraints(nullable:"false")}  
            column(name: "number", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "发票号码")  {constraints(nullable:"false")}  
            column(name: "taxi_number", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "车牌号")  {constraints(nullable:"false")}  
            column(name: "date", type: "date",  remarks: "日期")   
            column(name: "time", type: "varchar(" + 30 * weight + ")",   defaultValue:"",   remarks: "上下车时间")  {constraints(nullable:"false")}  
            column(name: "fare", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "金额")  {constraints(nullable:"false")}  
            column(name: "fuel_oil_surcharge", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "附加燃油费")  {constraints(nullable:"false")}  
            column(name: "call_service_surcharge", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "叫车服务费")  {constraints(nullable:"false")}  
            column(name: "total_fare", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "实收金额")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
	
    changeSet(author: "liang.li05@hand-china.com", id: "2020-04-23-hocr_taxi_receipt") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "invoice_code", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "number", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "taxi_number", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "time", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "fare", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "fuel_oil_surcharge", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "call_service_surcharge", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_taxi_receipt", columnName: "total_fare", columnDataType: "decimal(20,2)")

        renameColumn(tableName: "hocr_taxi_receipt", oldColumnName: "number", newColumnName: "invoice_num", columnDataType: "varchar(" + 30 * weight + ")")
        renameColumn(tableName: "hocr_taxi_receipt", oldColumnName: "date", newColumnName: "invoice_date", columnDataType: "date")
        renameColumn(tableName: "hocr_taxi_receipt", oldColumnName: "time", newColumnName: "up_down_time", columnDataType: "varchar(" + 30 * weight + ")")
    }
}