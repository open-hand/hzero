package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_industry_category_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_industry_category_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_industry_category_tl", remarks: "行业品类信息多语言") {
            column(name: "category_id", type: "bigint",  remarks: "行业类别ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "category_name", type: "varchar(" + 120 * weight + ")",  remarks: "品类名称")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"category_id,lang",tableName:"hpfm_industry_category_tl",constraintName: "hpfm_industry_category_tl_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_industry_category_tl") {
        addColumn(tableName: 'hpfm_industry_category_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}