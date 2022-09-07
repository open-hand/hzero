package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_uom_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_uom_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_uom_tl", remarks: "计量单位定义多语言") {
            column(name: "uom_id", type: "bigint",  remarks: "计量单位表id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "uom_name", type: "varchar(" + 60 * weight + ")",  remarks: "计量单位名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"uom_id,lang",tableName:"hpfm_uom_tl",constraintName: "hpfm_uom_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_uom_tl") {
        addColumn(tableName: 'hpfm_uom_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}