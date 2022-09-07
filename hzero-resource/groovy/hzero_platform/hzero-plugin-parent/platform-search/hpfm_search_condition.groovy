package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_search_condition.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hpfm_search_condition") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_search_condition_s', startValue:"1")
        }
        createTable(tableName: "hpfm_search_condition", remarks: "高级检索条件配置") {
            column(name: "search_condition_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "search_id", type: "bigint",  remarks: "高级检索配置ID，hpfm_search.search_id")  {constraints(nullable:"false")}  
            column(name: "field_name", type: "varchar(" + 60 * weight + ")",  remarks: "排序字段名称")  {constraints(nullable:"false")}  
            column(name: "comparator", type: "varchar(" + 30 * weight + ")",  remarks: "比较符")  {constraints(nullable:"false")}  
            column(name: "value", type: "varchar(" + 480 * weight + ")",  remarks: "语言")
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"search_id,field_name,comparator",tableName:"hpfm_search_condition",constraintName: "hpfm_search_condition_u1")
    }
}