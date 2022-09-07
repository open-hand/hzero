package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_charge_group_line.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-14-hitf_charge_group_line") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_charge_group_line_s', startValue: "1")
        }
        createTable(tableName: "hitf_charge_group_line", remarks: "组合计费设置行表") {
            column(name: "group_line_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
			column(name: "group_header_id", type: "bigint",  remarks: "头id")  {constraints(nullable:"false")}			
			column(name: "type_code", type: "varchar(" + 30 * weight + ")",  remarks: "组合计费服务类型，值集：HITF.GROUP_SERVER_TYPE")   {constraints(nullable:"false")} 
			column(name: "interface_server_id", type: "bigint",  remarks: "表hitf_interface_server主键ID")  {constraints(nullable:"false")} 
			column(name: "interface_id", type: "bigint",  remarks: "表hitf_interface主键ID")
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
			column(name: "remark", type: "varchar(" + 240 * weight + ")",  remarks: "备注说明")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }

        addUniqueConstraint(columnNames: "group_header_id,interface_server_id,interface_id", tableName: "hitf_charge_group_line", constraintName: "hitf_charge_group_line_u1")
        createIndex(tableName: "hitf_charge_group_line", indexName: "hitf_charge_group_line_n1") {
            column(name: "group_header_id")
        }
    }
}