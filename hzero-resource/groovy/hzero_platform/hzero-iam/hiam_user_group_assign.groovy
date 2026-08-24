package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_user_group_assign.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hiam_user_group_assign") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_user_group_assign_s', startValue:"1")
        }
        createTable(tableName: "hiam_user_group_assign", remarks: "用户组分配") {
            column(name: "assign_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "bigint",  remarks: "用户ID，iam_user.user_id")   
            column(name: "user_group_id", type: "bigint",  remarks: "hiam_user_group.user_group_id")   
            column(name: "default_flag", type: "tinyint",   defaultValue:"0",   remarks: "是否默认 1:默认 0:不默认")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"tenant_id,user_id,user_group_id",tableName:"hiam_user_group_assign",constraintName: "hiam_user_group_assign_u1")
    }
}