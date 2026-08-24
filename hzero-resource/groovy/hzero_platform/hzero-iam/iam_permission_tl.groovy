package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_permission_tl.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "hzero@hand-china.com", id: "2019-11-26-iam_permission_tl") {
		createTable(tableName: "iam_permission_tl", remarks: "权限多语言表") {
			column(name: "id", type: "bigint",  remarks: "iam_permission.id")  {constraints(nullable:"false")}  
            column(name: "lang", type: "varchar(" + 8 * weight + ")",  remarks: "语言")  {constraints(nullable:"false")}             
            column(name: "description", type: "varchar(" + 1024 * weight + ")",  remarks: "描述")  {constraints(nullable:"false")}
        }
		
		addUniqueConstraint(columnNames:"id,lang",tableName:"iam_permission_tl",constraintName: "iam_permission_tl_u1")
    }

    changeSet(author: "qingsheng.chen@hand-china.com", id: "2020-04-21-iam_permission_tl") {
        dropNotNullConstraint(tableName: "iam_permission_tl", columnName: "description", columnDataType: "varchar(" + 1024 * weight + ")")
    }
}