package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_tenant_access.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-06-hiam_tenant_access") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_tenant_access_s', startValue:"1")
        }
        createTable(tableName: "hiam_tenant_access", remarks: "租户访问审计") {
            column(name: "access_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "user_id", type: "bigint",  remarks: "用户ID,iam_user.id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "access_datetime", type: "datetime",  remarks: "访问时间")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"user_id,tenant_id",tableName:"hiam_tenant_access",constraintName: "hiam_tenant_access_u1")
    }

    changeSet(author: "hzero", id: "2019-09-10-hiam_tenant_access") {
        addColumn(tableName: "hiam_tenant_access") {
            column(name: "access_count", type: "int", remarks: "访问次数", defaultValue: 0) {
                constraints(nullable: "false")
            }
        }
    }
}