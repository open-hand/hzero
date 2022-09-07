package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_lov_view_line_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-08-27-hpfm_lov_view_line_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_lov_view_line_tl", remarks: "值集视图行多语言") {
            column(name: "view_line_id", type: "bigint", remarks: "视图行Id，hpfm_lov_view_line.view_line_id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}
            column(name: "display", type: "varchar(" + 30 * weight + ")",  remarks: "显示名称，hpfm_lov_view_line.display")  {constraints(nullable:"false")}
        }
        addUniqueConstraint(columnNames:"view_line_id,lang",tableName:"hpfm_lov_view_line_tl", constraintName: "hpfm_lov_view_line_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_lov_view_line_tl") {
        addColumn(tableName: 'hpfm_lov_view_line_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-10-29-hpfm_lov_view_line_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "hpfm_lov_view_line_tl", columnName: 'display', newDataType: "varchar(" + 240 * weight + ")")
    }
}