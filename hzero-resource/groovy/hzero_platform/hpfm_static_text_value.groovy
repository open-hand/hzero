package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_static_text_value.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_static_text_value") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_static_text_value_s', startValue:"1")
        }
        createTable(tableName: "hpfm_static_text_value", remarks: "平台静态信息") {
            column(name: "text_value_id", type: "bigint", autoIncrement: true ,   remarks: "表主键ID")  {constraints(primaryKey: true)} 
            column(name: "text_id", type: "bigint",  remarks: "文本ID")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 30 * weight + ")",  remarks: "语言 HPFM.LANGUAGE")  {constraints(nullable:"false")}  
            column(name: "title", type: "varchar(" + 120 * weight + ")",  remarks: "标题")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 240 * weight + ")",  remarks: "描述")   
            column(name: "text", type: "longtext",  remarks: "文本，富文本")  {constraints(nullable:"false")}  

        }

    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-05-hpfm_static_text_value") {
        addColumn(tableName: 'hpfm_static_text_value') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}