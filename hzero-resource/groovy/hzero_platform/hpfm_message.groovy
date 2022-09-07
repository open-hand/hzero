package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_message.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-hpfm_message") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hpfm_message_s', startValue:"1")
        }
        createTable(tableName: "hpfm_message", remarks: "后端消息") {
            column(name: "message_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "code", type: "varchar(" + 180 * weight + ")",  remarks: "消息编码")  {constraints(nullable:"false")}  
            column(name: "type", type: "varchar(" + 10 * weight + ")",  remarks: "消息类型: info/warn/error")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 10 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}  
            column(name: "description", type: "varchar(" + 1200 * weight + ")",  remarks: "消息描述")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"code,lang",tableName:"hpfm_message",constraintName: "hpfm_message_u1")
    }
}