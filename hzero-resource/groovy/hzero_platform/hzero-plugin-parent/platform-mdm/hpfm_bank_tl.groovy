package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_bank_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_bank_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_bank_tl", remarks: "银行信息多语言") {
            column(name: "bank_id", type: "bigint",  remarks: "表ID，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "bank_name", type: "varchar(" + 120 * weight + ")",  remarks: "银行名称")  {constraints(nullable:"false")}  
            column(name: "bank_short_name", type: "varchar(" + 60 * weight + ")",  remarks: "银行简称")   

        }

        addUniqueConstraint(columnNames:"bank_id,lang",tableName:"hpfm_bank_tl",constraintName: "hpfm_bank_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_bank_tl") {
        addColumn(tableName: 'hpfm_bank_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}