package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_report_permission.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-02-19-hrpt_report_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_report_permission_s', startValue:"1")
        }
        createTable(tableName: "hrpt_report_permission", remarks: "报表权限") {
            column(name: "permission_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "report_id", type: "bigint",  remarks: "报表类别，hrpt_report.report_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "role_id", type: "bigint",   defaultValue:"0",   remarks: "角色ID，iam_role.role_id")   
            column(name: "start_date", type: "date",  remarks: "有效期从")   
            column(name: "end_date", type: "date",  remarks: "有效期至")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"report_id,tenant_id,role_id",tableName:"hrpt_report_permission",constraintName: "hrpt_report_permission_u1")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2019-04-03-hrpt_report_permission') {
        dropDefaultValue(tableName: "hrpt_report_permission", columnName: 'role_id')
        addDefaultValue(tableName: "hrpt_report_permission", columnName: "role_id", defaultValue: "-1")
    }
}