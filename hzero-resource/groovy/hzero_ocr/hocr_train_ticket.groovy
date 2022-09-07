package script.db

databaseChangeLog(logicalFilePath: 'script/db/hocr_train_ticket.groovy') {
	def weight = 1
	if(helper.isSqlServer()){
	    weight = 2
	} else if(helper.isOracle()){
	    weight = 3
	}
    changeSet(author: "jialin.xing@hand-china.com", id: "2019-10-14-hocr_train_ticket") {
		if(helper.dbType().isSupportSequence()){
			createSequence(sequenceName: 'hocr_train_ticket_s', startValue:"1")
		}
        createTable(tableName: "hocr_train_ticket", remarks: "火车票识别记录表") {
            column(name: "train_ticket_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "ticket_number", type: "varchar(" + 30 * weight + ")",  remarks: "车票号")  {constraints(nullable:"false")}  
            column(name: "train_number", type: "varchar(" + 30 * weight + ")",  remarks: "车次")  {constraints(nullable:"false")}  
            column(name: "check_port", type: "varchar(" + 10 * weight + ")",  remarks: "检票口")  {constraints(nullable:"false")}  
            column(name: "starting_station", type: "varchar(" + 30 * weight + ")",  remarks: "始发站")  {constraints(nullable:"false")}  
            column(name: "destination_station", type: "varchar(" + 30 * weight + ")",  remarks: "终点站")  {constraints(nullable:"false")}  
            column(name: "starting_time", type: "varchar(" + 30 * weight + ")",   defaultValue:"1",   remarks: "开车时间")  {constraints(nullable:"false")}  
            column(name: "ticket_price", type: "decimal(20,2)",   defaultValue:"0.00",   remarks: "票价")  {constraints(nullable:"false")}  
            column(name: "seat_category", type: "varchar(" + 30 * weight + ")",  remarks: "席别")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 30 * weight + ")",  remarks: "乘客姓名")  {constraints(nullable:"false")}  
            column(name: "seat_name", type: "varchar(" + 30 * weight + ")",  remarks: "座位号")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }

    changeSet(author: "liang.li05@hand-china.com", id: "2020-04-123-hocr_train_ticket") {
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "ticket_number", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "train_number", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "check_port", columnDataType: "varchar(" + 10 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "starting_station", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "destination_station", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "starting_time", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "ticket_price", columnDataType: "decimal(20,2)")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "seat_category", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "name", columnDataType: "varchar(" + 30 * weight + ")")
        dropNotNullConstraint(tableName: "hocr_train_ticket", columnName: "seat_name", columnDataType: "varchar(" + 30 * weight + ")")

        renameColumn(tableName: "hocr_train_ticket", oldColumnName: "name", newColumnName: "passenger_name", columnDataType: "varchar(" + 30 * weight + ")")
		
		addColumn(tableName: "hocr_train_ticket") {
            column(name: "serial_number", type: "varchar(" + 30 * weight + ")", remarks: "序列号") {constraints(nullable:"true")}  
        }

    }
}