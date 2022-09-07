package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_tax_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_tax_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_tax_tl", remarks: "税率定义多语言") {
            column(name: "tax_id", type: "bigint",  remarks: "税率ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   

        }

        addUniqueConstraint(columnNames:"tax_id,lang",tableName:"hpfm_tax_tl",constraintName: "hpfm_tax_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_tax_tl") {
        addColumn(tableName: 'hpfm_tax_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}