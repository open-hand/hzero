package script.db

databaseChangeLog(logicalFilePath: 'script/db/act_hi_varinst.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-04-24-act_hi_varinst") {
	    createIndex(tableName: "act_hi_varinst", indexName: "hwfp_act_idx_hi_execution_id") {
            column(name: "execution_id_")
        }
    }
}