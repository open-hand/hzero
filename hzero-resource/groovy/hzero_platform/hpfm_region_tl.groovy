package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_region_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_region_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hpfm_region_tl", remarks: "地区定义多语言") {
            column(name: "region_id", type: "bigint",  remarks: "表id，主键，供其他表做外键")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言编码")  {constraints(nullable:"false")}  
            column(name: "region_name", type: "varchar(" + 120 * weight + ")",  remarks: "区域名称")  {constraints(nullable:"false")}  
            column(name: "quick_index", type: "varchar(" + 30 * weight + ")",  remarks: "快速检索")  {constraints(nullable:"false")}  

        }
   createIndex(tableName: "hpfm_region_tl", indexName: "hpfm_region_tl_n1") {
            column(name: "quick_index")
        }

        addUniqueConstraint(columnNames:"region_id,lang",tableName:"hpfm_region_tl",constraintName: "hpfm_region_tl_pk")
    }

    changeSet(id: '2019-03-14-hpfm_region_tl', author: 'zhiying.dong@hand-china.com') {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        dropNotNullConstraint(tableName: 'hpfm_region_tl', columnName: 'quick_index', columnDataType: "varchar(" + 30 * weight + ")")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_region_tl") {
        addColumn(tableName: 'hpfm_region_tl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}