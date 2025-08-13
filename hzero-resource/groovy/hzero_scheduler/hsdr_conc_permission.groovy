package script.db

databaseChangeLog(logicalFilePath: 'script/db/hsdr_conc_permission.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-18-hsdr_conc_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hsdr_conc_permission_s', startValue:"1")
        }
        createTable(tableName: "hsdr_conc_permission", remarks: "并发请求权限") {
            column(name: "permission_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "concurrent_id", type: "bigint",  remarks: "并发程序ID,hsdr_conc_pragram.concurrent_id")  {constraints(nullable:"false")}  
            column(name: "limit_quantity", type: "int",  remarks: "限制次数")   
            column(name: "role_id", type: "bigint",  remarks: "角色ID，iam_role.role_id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "start_date", type: "date",  remarks: "有效期从")   
            column(name: "end_date", type: "date",  remarks: "有效期至")   
            column(name: "enabled_flag", type: "tinyint",   defaultValue:"1",   remarks: "启用标识")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hsdr_conc_permission", indexName: "hsdr_conc_permission_n2") {
            column(name: "concurrent_id")
        }

        addUniqueConstraint(columnNames:"concurrent_id,role_id,tenant_id",tableName:"hsdr_conc_permission",constraintName: "hsdr_conc_permission_u1")
    }
}