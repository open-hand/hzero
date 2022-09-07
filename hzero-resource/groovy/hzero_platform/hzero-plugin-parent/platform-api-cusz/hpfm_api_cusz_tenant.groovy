package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_api_cusz_tenant.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-31-hpfm_api_cusz_tenant") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_api_cusz_tenant_s', startValue:"1")
        }
        createTable(tableName: "hpfm_api_cusz_tenant", remarks: "API个性化租户关系") {
            column(name: "customize_tenant_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "customize_id", type: "bigint",  remarks: "个性化ID，表hpfm_api_cusz.customize_id")  {constraints(nullable:"false")}  
            column(name: "apply_tenant_id", type: "bigint",  remarks: "应用的租户ID，表hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",  remarks: "所属租户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
        }

        
            addUniqueConstraint(columnNames:"customize_id,apply_tenant_id",tableName:"hpfm_api_cusz_tenant",constraintName: "hpfm_api_cusz_tenant_u1")
            }
}