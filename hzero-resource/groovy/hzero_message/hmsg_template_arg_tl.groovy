package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_template_arg_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-10-08-hmsg_template_arg_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hmsg_template_arg_tl", remarks: "消息模板参数多语言") {
            column(name: "arg_id", type: "bigint",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "参数描述")

        }

        addUniqueConstraint(columnNames:"arg_id,lang",tableName:"hmsg_template_arg_tl",constraintName: "hmsg_template_arg_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hmsg_template_arg_tl") {
        addColumn(tableName: 'hmsg_template_arg_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}