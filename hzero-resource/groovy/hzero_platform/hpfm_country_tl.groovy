package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_country_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_country_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_country_tl", remarks: "国家定义多语言") {
            column(name: "country_id", type: "bigint",  remarks: "表id，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "country_name", type: "varchar(" + 120 * weight + ")",  remarks: "国家名称")  {constraints(nullable:"false")}  
            column(name: "quick_index", type: "varchar(" + 30 * weight + ")",  remarks: "快速检索")  {constraints(nullable:"false")}  
            column(name: "abbreviation", type: "varchar(" + 30 * weight + ")",  remarks: "简称")   

        }
   createIndex(tableName: "hpfm_country_tl", indexName: "hpfm_country_tl_n1") {
            column(name: "quick_index")
        }

        addUniqueConstraint(columnNames:"country_id,lang",tableName:"hpfm_country_tl",constraintName: "hpfm_country_tl_pk")
    }

    changeSet(id: '2019-03-14-hpfm_country_tl', author: 'zhiying.dong@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropNotNullConstraint(tableName: 'hpfm_country_tl', columnName: 'quick_index', columnDataType: "varchar(" + 30 * weight + ")")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hpfm_country_tl") {
        addColumn(tableName: 'hpfm_country_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}