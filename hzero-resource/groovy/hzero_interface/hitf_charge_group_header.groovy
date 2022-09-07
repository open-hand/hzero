package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_charge_group_header.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-14-hitf_charge_group_header") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_charge_group_header_s', startValue: "1")
        }
        createTable(tableName: "hitf_charge_group_header", remarks: "组合计费设置头表") {
            column(name: "group_header_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
			column(name: "group_code", type: "varchar(" + 60 * weight + ")",  remarks: "组合代码")   {constraints(nullable:"false")}
			column(name: "group_name", type: "varchar(" + 240 * weight + ")",  remarks: "组合名称")   {constraints(nullable:"false")}
			column(name: "status_code", type: "varchar(" + 30 * weight + ")",  remarks: "状态代码，值集：HITF.CHARGE_GROUP_STATUS")   {constraints(nullable:"false")} 
			column(name: "start_date", type: "datetime",   remarks: "起始日期")  			
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID")  {constraints(nullable:"false")}  
			column(name: "remark", type: "varchar(" + 360 * weight + ")",  remarks: "备注说明")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }

        addUniqueConstraint(columnNames: "group_code", tableName: "hitf_charge_group_header", constraintName: "hitf_charge_group_header_u1")
        addUniqueConstraint(columnNames: "group_name", tableName: "hitf_charge_group_header", constraintName: "hitf_charge_group_header_u2")
    }
}