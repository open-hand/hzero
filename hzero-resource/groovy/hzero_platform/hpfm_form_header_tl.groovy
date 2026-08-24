package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_form_header_tl.groovy') {
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-19-hpfm_form_header_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_form_header_tl", remarks: "表单配置头多语言") {
            column(name: "form_header_id", type: "bigint", remarks: "表单配置头Id，hpfm_form_header.form_header_id")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}
            column(name: "form_name", type: "varchar(" + 255 * weight + ")", remarks: "表单头名称")  {constraints(nullable:"false")}
            column(name: "form_description", type: "varchar(" + 480 * weight + ")", remarks: "表单头说明")
        }

        addUniqueConstraint(columnNames:"form_header_id,lang",tableName:"hpfm_form_header_tl",constraintName: "hpfm_form_header_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_form_header_tl") {
        addColumn(tableName: 'hpfm_form_header_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}