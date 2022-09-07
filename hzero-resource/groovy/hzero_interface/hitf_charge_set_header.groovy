package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_charge_set_header.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-14-hitf_charge_set_header") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_charge_set_header_s', startValue:"1")
        }
        createTable(tableName: "hitf_charge_set_header", remarks: "接口计费设置头信息") {
            column(name: "set_header_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
			column(name: "set_code", type: "varchar(" + 60 * weight + ")",  remarks: "计费代码")   {constraints(nullable:"false")}
			column(name: "set_name", type: "varchar(" + 240 * weight + ")",  remarks: "计费名称")   {constraints(nullable:"false")}
			column(name: "type_code", type: "varchar(" + 30 * weight + ")",  remarks: "计费类型，值集:HCHG.CHARGE_TYPE")   {constraints(nullable:"false")} 
			column(name: "status_code", type: "varchar(" + 30 * weight + ")",  remarks: "设置状态，值集:HITF.CHARGE_SET_STATUS")   {constraints(nullable:"false")} 
			column(name: "interface_server_id", type: "bigint",  remarks: "表hitf_interface_server主键ID")  {constraints(nullable:"false")} 
			column(name: "interface_id", type: "bigint",  remarks: "表hitf_interface主键ID")	
			column(name: "start_date", type: "datetime",   remarks: "起始日期")  			
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
			column(name: "remark", type: "varchar(" + 360 * weight + ")",  remarks: "备注说明")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }

        addUniqueConstraint(columnNames:"set_code",tableName:"hitf_charge_set_header",constraintName: "hitf_charge_set_header_u1")
		addUniqueConstraint(columnNames:"interface_server_id,interface_id",tableName:"hitf_charge_set_header",constraintName: "hitf_charge_set_header_u2")
		createIndex(tableName: "hitf_charge_set_header", indexName: "hitf_charge_set_header_n1") {
            column(name: "set_name")
            column(name: "set_code")
        }
    }
}