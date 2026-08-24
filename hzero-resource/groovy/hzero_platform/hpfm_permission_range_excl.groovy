package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_permission_range_excl.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2020-06-10-hpfm_permission_range_excl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_permission_range_excl_s', startValue:"1")
        }
        createTable(tableName: "hpfm_permission_range_excl", remarks: "屏蔽范围黑名单") {
            column(name: "range_exclude_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "range_id", type: "bigint",  remarks: "屏蔽范围ID,hpfm_permission_range_excl.range_id")  {constraints(nullable:"false")}  
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名称")   
            column(name: "tenant_id", type: "bigint",  remarks: "租户id，hpfm_tenant.tenant_id")   
            column(name: "sql_id", type: "varchar(" + 120 * weight + ")",  remarks: "屏蔽sqlId")   
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"range_id,service_name,tenant_id,sql_id",tableName:"hpfm_permission_range_excl",constraintName: "hpfm_permission_range_excl_u1")
    }
}