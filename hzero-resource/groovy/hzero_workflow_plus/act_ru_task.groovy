package script.db

databaseChangeLog(logicalFilePath: 'script/db/act_ru_task.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-06-28-act_ru_task") {
        addColumn(tableName: "act_ru_task") {
            column(name: "comment_", type: "varchar(" + 600 * weight + ")", remarks: "审批意见")
        }
    }
}
