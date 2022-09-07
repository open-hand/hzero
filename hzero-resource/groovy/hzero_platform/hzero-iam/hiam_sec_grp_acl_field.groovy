package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_acl_field.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_acl_field") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_acl_field_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_acl_field", remarks: "安全组字段权限") {
            column(name: "sec_grp_acl_field_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sec_grp_id", type: "bigint",  remarks: "安全组ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户")  {constraints(nullable:"false")}  
            column(name: "field_id", type: "bigint",  remarks: "字段ID，hiam_field.field_id")  {constraints(nullable:"false")}  
            column(name: "permission_id", type: "bigint",  remarks: "API权限ID，iam_permission.id")  {constraints(nullable:"false")}  
            column(name: "permission_type", type: "varchar(" + 30 * weight + ")",  remarks: "权限类型，值集HIAM.FIELD.PERMISSION_TYPE[HIDDEN]")  {constraints(nullable:"false")}  
            column(name: "permission_rule", type: "varchar(" + 60 * weight + ")",  remarks: "权限规则（脱敏预留）")   
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "auto_assign_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否自动分配。1是，0否")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"sec_grp_id,tenant_id,field_id",tableName:"hiam_sec_grp_acl_field",constraintName: "hiam_sec_grp_acl_field_u1")
    }

    changeSet(author: "hzero", id: "2020-06-01-hiam_sec_grp_acl_field"){
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }

        addColumn(tableName: 'hiam_sec_grp_acl_field') {
            column(name: "assign_type_code", type: "varchar(" + 20 * weight + ")", remarks: "权限分配类型，Code：HAIM.SEC_GRP.ASSIGN_TYPE_CODE ([SELF/自己创建]、[PARENT/父类分配]、[SELF_PARENT/自己创建之后，父类也创建])")
        }
    }
}