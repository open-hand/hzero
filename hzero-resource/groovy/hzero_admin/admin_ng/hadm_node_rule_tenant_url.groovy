package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_node_rule_tenant_url.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hadm_node_rule_tenant_url") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hadm_node_rule_tenant_url_s', startValue:"1")
        }
        createTable(tableName: "hadm_node_rule_tenant_url", remarks: "节点组规则URL配置") {
            column(name: "tenant_url_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "rule_tenant_id", type: "bigint",  remarks: "hmgr_node_rule_tenant主键")  {constraints(nullable:"false")}  
            column(name: "url_id", type: "bigint",  remarks: "URL ID")  {constraints(nullable:"false")}  
            column(name: "url", type: "varchar(" + 240 * weight + ")",  remarks: "需替换URL")  {constraints(nullable:"false")}  
            column(name: "method", type: "varchar(" + 30 * weight + ")",  remarks: "URL匹配")  {constraints(nullable:"false")}  
            column(name: "service_name", type: "varchar(" + 60 * weight + ")",  remarks: "服务名")  {constraints(nullable:"false")}  
            column(name: "url_prefix", type: "varchar(" + 20 * weight + ")",  remarks: "URL前缀，租户CODE")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

   addUniqueConstraint(columnNames:"rule_tenant_id,url_id",tableName:"hadm_node_rule_tenant_url",constraintName: "hadm_node_rule_tenant_url_u1")   
    }
}