package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_lov_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_lov_tl", remarks: "LOV多语言表") {
            column(name: "lov_id", type: "bigint",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "lov_name", type: "varchar(" + 240 * weight + ")",  remarks: "值集名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 480 * weight + ")",  remarks: "描述")   

        }

        addUniqueConstraint(columnNames:"lov_id,lang",tableName:"hpfm_lov_tl",constraintName: "hpfm_lov_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_lov_tl") {
        addColumn(tableName: 'hpfm_lov_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}