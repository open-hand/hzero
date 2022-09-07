package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_region_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-14-hpfm_exchange_rate_type_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_exchange_rate_type_tl", remarks: "汇率类型定义多语言") {
            column(name: "rate_type_id", type: "bigint",  remarks: "hpfm_exchange_rate_type.rate_type_id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "type_name", type: "varchar(" + 30 * weight + ")",  remarks: "汇率类型名称")  {constraints(nullable:"false")}

        }
        addUniqueConstraint(columnNames:"rate_type_id,lang",tableName:"hpfm_exchange_rate_type_tl",constraintName: "hpfm_exchange_rate_type_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_exchange_rate_type_tl") {
        addColumn(tableName: 'hpfm_exchange_rate_type_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}