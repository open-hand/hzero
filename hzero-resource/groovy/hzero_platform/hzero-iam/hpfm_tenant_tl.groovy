package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_tenant_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-08-02-hpfm_tenant_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_tenant_tl", remarks: "") {
            column(name: "tenant_id", type: "bigint",  remarks: "租户id，hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}
            column(name: "tenant_name", type: "varchar(" + 120 * weight + ")",  remarks: "租户名称，hpfm_tenant.tenant_name")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"tenant_id,lang",tableName:"hpfm_tenant_tl",constraintName: "hpfm_tenant_tl_u1")
    }
}