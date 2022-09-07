package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_report_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hrpt_report_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "hrpt_report_tl", remarks: "报表信息多语言") {
            column(name: "report_id", type: "bigint",  remarks: "报表Id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "report_name", type: "varchar(" + 120 * weight + ")",  remarks: "报表名称")  {constraints(nullable:"false")}  
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"report_id,lang",tableName:"hrpt_report_tl",constraintName: "hrpt_report_tl_u1")
    }
}
