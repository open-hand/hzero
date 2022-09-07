package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_search.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-03-10-hpfm_search") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_search_s', startValue:"1")
        }
        createTable(tableName: "hpfm_search", remarks: "高级检索配置") {
            column(name: "search_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "search_code", type: "varchar(" + 30 * weight + ")",  remarks: "高级检索配置编码")  {constraints(nullable:"false")}  
            column(name: "search_name", type: "varchar(" + 30 * weight + ")",  remarks: "高级检索配置名称")  {constraints(nullable:"false")}  
            column(name: "default_flag", type: "tinyint",   defaultValue:"1",   remarks: "默认高级检索配置标记")  {constraints(nullable:"false")}  
            column(name: "remark", type: "longtext",  remarks: "备注说明")   
            column(name: "user_id", type: "bigint",  remarks: "用户ID,iam_user.id")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"search_name,search_code,user_id,tenant_id",tableName:"hpfm_search",constraintName: "hpfm_search_u1")
    }
}