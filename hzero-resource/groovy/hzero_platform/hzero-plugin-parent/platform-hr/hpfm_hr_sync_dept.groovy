package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_hr_sync_dept.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-27-hpfm_hr_sync_dept") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_hr_sync_dept_s', startValue:"1")
        }
        createTable(tableName: "hpfm_hr_sync_dept", remarks: "HR部门数据同步") {
            column(name: "sync_dept_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sync_type_code", type: "varchar(" + 30 * weight + ")",   defaultValue:"DD",   remarks: "同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信")  {constraints(nullable:"false")}  
            column(name: "unit_id", type: "bigint",  remarks: "部门ID,hpfm_unit.unit_id")  {constraints(nullable:"false")}  
            column(name: "department_id", type: "bigint",  remarks: "部门ID")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 60 * weight + ")",  remarks: "名称")  {constraints(nullable:"false")}  
            column(name: "order_seq", type: "bigint",   defaultValue:"0",   remarks: "序号")   
            column(name: "parentid", type: "bigint",   defaultValue:"1",   remarks: "父级部门ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"department_id,sync_type_code",tableName:"hpfm_hr_sync_dept",constraintName: "hpfm_hr_sync_dept_u1")
    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-06-05-hpfm_hr_sync_dept") {
        dropUniqueConstraint(tableName: 'hpfm_hr_sync_dept', constraintName: "hpfm_hr_sync_dept_u1")
        addUniqueConstraint(columnNames:"department_id,sync_type_code,tenant_id",tableName:"hpfm_hr_sync_dept",constraintName: "hpfm_hr_sync_dept_u1")
    }
}