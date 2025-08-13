package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_code_rule_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_code_rule_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_code_rule_tl", remarks: "编码规则多语言") {
            column(name: "rule_id", type: "bigint",  remarks: "编码规则Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "rule_name", type: "varchar(" + 60 * weight + ")",  remarks: "编码规则名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"rule_id,lang",tableName:"hpfm_code_rule_tl",constraintName: "hpfm_code_rule_tl_u1")
    }
}
