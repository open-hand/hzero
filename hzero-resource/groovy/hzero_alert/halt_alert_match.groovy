package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_match.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_match") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_match_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_match", remarks: "告警匹配信息") {
            column(name: "alert_match_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)} 
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}  
            column(name: "match_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"LABEL",   remarks: "附加信息类型，快速编码HALT.ALERT_MATCH_TYPE，LABEL/ANNOTATION")  {constraints(nullable:"false")}  
            column(name: "match_code", type: "varchar(" + 255 * weight + ")",  remarks: "代码")  {constraints(nullable:"false")}  
            column(name: "match_value", type: "varchar(" + 255 * weight + ")",  remarks: "值")  {constraints(nullable:"false")}  
            column(name: "match_regex_flag", type: "tinyint",   defaultValue:"0",   remarks: "正则匹配标识，默认非正则匹配")  {constraints(nullable:"false")}  
            column(name: "use_code", type: "varchar(" + 30 * weight + ")",  remarks: "用途代码，快速编码HALT.ALERT_MATCH_USE_CODE，INHIBIT/SILENCE")  {constraints(nullable:"false")}  
            column(name: "use_id", type: "bigint",  remarks: "用途ID，例如，INHIBIT，则此处是alert_inhibit_id，其他同理")  {constraints(nullable:"false")}  
            column(name: "use_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"DEFAULT",   remarks: "使用类型，SOURCE/TARGET，父子值集HOPS.OPAM.MATCH_USE_TYPE；INHIBIT使用两种；SILENCE使用TARGET。")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

    }
}