package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_node_rule_tenant_user.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hadm_node_rule_tenant_user") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_node_rule_tenant_user_s', startValue:"1")
        }
        createTable(tableName: "hadm_node_rule_tenant_user", remarks: "节点组规则用户配置") {
            column(name: "tenant_user_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "rule_tenant_id", type: "bigint",  remarks: "hmgr_node_rule_tenant主键")  {constraints(nullable:"false")}  
            column(name: "user_id", type: "bigint",  remarks: "用户ID")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

   addUniqueConstraint(columnNames:"rule_tenant_id,user_id",tableName:"hadm_node_rule_tenant_user",constraintName: "hadm_node_rule_tenant_user_u1")   
    }
}