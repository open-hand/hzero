package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_tenant_permission.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-11-26-hiam_tenant_permission") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hiam_tenant_permission_s', startValue:"1")
        }
        createTable(tableName: "hiam_tenant_permission", remarks: "租户权限") {
			column(name: "tenant_permission_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint", remarks: "租户ID，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
			column(name: "permission_id", type: "bigint", remarks: "权限ID，iam_permission.id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }
		
        addUniqueConstraint(columnNames:"tenant_id,permission_id",tableName:"hiam_tenant_permission",constraintName: "hiam_tenant_permission_u1")
    }
}