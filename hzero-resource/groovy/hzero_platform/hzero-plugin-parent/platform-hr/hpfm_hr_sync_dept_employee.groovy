package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_hr_sync_dept_employee.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-27-hpfm_hr_sync_dept_employee") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_hr_sync_dept_employee_s', startValue:"1")
        }
        createTable(tableName: "hpfm_hr_sync_dept_employee", remarks: "HR数据同步员工分配岗位") {
            column(name: "sync_assign_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sync_dept_id", type: "bigint",  remarks: "同步数据部门ID")  {constraints(nullable:"false")}  
            column(name: "sync_employee_id", type: "bigint",  remarks: "同步数据员工ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"sync_employee_id,sync_dept_id",tableName:"hpfm_hr_sync_dept_employee",constraintName: "hpfm_hr_sync_dept_employee_u1")
    }
}