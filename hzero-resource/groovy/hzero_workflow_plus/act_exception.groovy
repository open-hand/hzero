package script.db

databaseChangeLog(logicalFilePath: 'script/db/act_exception.groovy') {
    changeSet(author: "qingsheng.chen@hand-china.com", id: "2018-10-25-act_exception") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'act_exception_s', startValue: "1")
        }
        createTable(tableName: "act_exception", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true, remarks: "") { constraints(primaryKey: true) }
            column(name: "proc_id", type: "varchar(225)", remarks: "流程id") { constraints(nullable: "false") }
            column(name: "message", type: "longblob", remarks: "异常信息") { constraints(nullable: "false") }
            column(name: "due_date", type: "datetime", remarks: "执行时间") { constraints(nullable: "false") }
            column(name: "message_head", type: "varchar(300)", remarks: "读取状态")
        }
    }
}