package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_label_permission.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-12-18-hrpt_label_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hrpt_label_permission_s', startValue:"1")
        }
        createTable(tableName: "hrpt_label_permission", remarks: "标签权限") {
            column(name: "permission_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "label_template_id", type: "bigint",  remarks: "标签模板ID，hrpt_label_template.label_template_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "role_id", type: "bigint",   defaultValue:"-1",   remarks: "角色ID，iam_role.role_id")   
            column(name: "start_date", type: "date",  remarks: "有效期从")   
            column(name: "end_date", type: "date",  remarks: "有效期至")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"label_template_id,tenant_id,role_id",tableName:"hrpt_label_permission",constraintName: "hrpt_label_permission_u1")
    }
}