package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_label_tl.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-02-25-iam_label_tl") {
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_label_tl_s', startValue:"1")
        }
        createTable(tableName: "iam_label_tl", remarks: "") {
            column(name: "id", type: "bigint",  remarks: "")  {constraints(nullable:"false")}
            column(name: "lang", type: "varchar(" + 16 * weight + ")",  remarks: "")  {constraints(nullable:"false")}
            column(name: 'description', type: "varchar(" + 128 * weight + ")", remarks: '描述')
        }

        addUniqueConstraint(columnNames:"id,lang",tableName:"iam_label_tl",constraintName: "iam_label_tl_u1")
    }
}