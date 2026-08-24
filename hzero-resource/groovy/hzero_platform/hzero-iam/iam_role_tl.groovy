package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_role_tl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_role_tl") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        createTable(tableName: "iam_role_tl", remarks: "") {
            column(name: "lang", type: "varchar(" + 8 * weight + ")",  remarks: "语言code")  {constraints(nullable:"false")}  
            column(name: "id", type: "bigint",  remarks: "role表id")  {constraints(nullable:"false")}  
            column(name: "name", type: "varchar(" + 64 * weight + ")",  remarks: "多语言字段")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"id,lang",tableName:"iam_role_tl",constraintName: "iam_role_tl_pk")
    }
	
	changeSet(author: 'jiangzhou.bo@hand-china.com', id: '2020-04-30-iam_role_tl') {
		def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'iam_role_tl') {
            column(name: 'tpl_role_name', type: "varchar(" + 64 * weight + ")", remarks: '模板角色的子角色名称') {constraints(nullable:"true")}
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-iam_role_tl") {
        addColumn(tableName: 'iam_role_tl') {
            column(name: "h_tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}