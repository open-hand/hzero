package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_hr_sync_employee.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-27-hpfm_hr_sync_employee") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_hr_sync_employee_s', startValue:"1")
        }
        createTable(tableName: "hpfm_hr_sync_employee", remarks: "HR员工数据同步") {
            column(name: "sync_employee_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sync_type_code", type: "varchar(" + 30 * weight + ")",   defaultValue:"DD",   remarks: "同步类型，值集HPFM.HR_SYNC_TYPE DD:钉钉 WX:微信")  {constraints(nullable:"false")}  
            column(name: "userid", type: "varchar(" + 120 * weight + ")",  remarks: "用户ID")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 120 * weight + ")",  remarks: "名称")  {constraints(nullable:"false")}  
            column(name: "mobile", type: "varchar(" + 30 * weight + ")",  remarks: "电话")   
            column(name: "position", type: "varchar(" + 240 * weight + ")",  remarks: "职位")   
            column(name: "gender", type: "tinyint",  remarks: "性别")   
            column(name: "email", type: "varchar(" + 120 * weight + ")",  remarks: "邮箱")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"userid,sync_type_code",tableName:"hpfm_hr_sync_employee",constraintName: "hpfm_hr_sync_employee_u1")
    }

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2020-03-19-hpfm_hr_sync_employee") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_hr_sync_employee') {
            if(helper.isOracle()){
                column(name: "employee_num", type: "varchar(" + 30 * weight + ")", defaultValue: "",  remarks: "员工编码,hpfm_employee.employee_num")  {constraints(nullable:"false")}
            } else {
                column(name: "employee_num", type: "varchar(" + 30 * weight + ")", remarks: "员工编码,hpfm_employee.employee_num")  {constraints(nullable:"false")}
            }
        }
    }

    changeSet(author: "fanghan.liu@hand-china.com", id: "2020-06-05-hpfm_hr_sync_employee") {
        dropUniqueConstraint(tableName: 'hpfm_hr_sync_employee', constraintName: "hpfm_hr_sync_employee_u1")
        addUniqueConstraint(columnNames:"userid,sync_type_code,tenant_id",tableName:"hpfm_hr_sync_employee",constraintName: "hpfm_hr_sync_employee_u1")
    }
}