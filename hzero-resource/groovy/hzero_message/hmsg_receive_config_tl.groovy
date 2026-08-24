package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_receive_config_tl.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2019-01-02-hmsg_receive_config_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hmsg_receive_config_tl", remarks: "接收配置多语言") {
            column(name: "receive_id", type: "bigint",  remarks: "hmsg_receive_config主键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 16 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "receive_name", type: "varchar(" + 120 * weight + ")",  remarks: "接收配置名称(TL)")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"receive_id,lang",tableName:"hmsg_receive_config_tl",constraintName: "hmsg_receive_config_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hmsg_receive_config_tl") {
        addColumn(tableName: 'hmsg_receive_config_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}