package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_sec_grp_acl.groovy') {
    changeSet(author: "hzero", id: "2020-03-13-hiam_sec_grp_acl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_sec_grp_acl_s', startValue:"1")
        }
        createTable(tableName: "hiam_sec_grp_acl", remarks: "安全组访问权限") {
            column(name: "sec_grp_acl_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "sec_grp_id", type: "bigint",  remarks: "安全组ID")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户")  {constraints(nullable:"false")}  
            column(name: "permission_id", type: "bigint",  remarks: "访问权限ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"sec_grp_id,permission_id",tableName:"hiam_sec_grp_acl",constraintName: "hiam_sec_grp_acl_u1")
    }
}