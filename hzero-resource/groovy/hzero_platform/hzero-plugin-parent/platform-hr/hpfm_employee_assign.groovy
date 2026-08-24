package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_employee_assign.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_employee_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_employee_assign_s', startValue:"1")
        }
        createTable(tableName: "hpfm_employee_assign", remarks: "员工岗位分配表") {
            column(name: "employee_assign_id", type: "bigint", autoIncrement: true ,   remarks: "员工岗位分配ID　")  {constraints(primaryKey: true)} 
            column(name: "employee_id", type: "bigint",  remarks: "员工ID")  {constraints(nullable:"false")}  
            column(name: "unit_company_id", type: "bigint",  remarks: "公司ID")  {constraints(nullable:"false")}  
            column(name: "unit_id", type: "bigint",  remarks: "组织ID")  {constraints(nullable:"false")}  
            column(name: "position_id", type: "bigint",  remarks: "岗位ID")  {constraints(nullable:"false")}  
            column(name: "primary_position_flag", type: "tinyint",   defaultValue:"1",   remarks: "主岗位标示")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用状态")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "记录版本号")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "记录创建人")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "记录创建日期")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "记录更新人")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "记录更新时间")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"employee_id,tenant_id,position_id",tableName:"hpfm_employee_assign",constraintName: "hpfm_employee_assign_u1")
    }
}