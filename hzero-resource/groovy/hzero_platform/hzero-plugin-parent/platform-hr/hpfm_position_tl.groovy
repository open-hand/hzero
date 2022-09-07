package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_position_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_position_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_position_tl", remarks: "岗位多语言") {
            column(name: "position_id", type: "bigint",  remarks: "岗位ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "position_name", type: "varchar(" + 120 * weight + ")",  remarks: "岗位名称")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   

        }

        addUniqueConstraint(columnNames:"position_id,lang",tableName:"hpfm_position_tl",constraintName: "hpfm_position_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_position_tl") {
        addColumn(tableName: 'hpfm_position_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}