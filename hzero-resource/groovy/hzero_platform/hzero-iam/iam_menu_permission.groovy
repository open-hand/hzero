package script.db

databaseChangeLog(logicalFilePath: 'script/db/iam_menu_permission.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-03-01-iam_menu_permission") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'iam_menu_permission_s', startValue:"1")
        }
        createTable(tableName: "iam_menu_permission", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true ,   remarks: "")  {constraints(primaryKey: true)} 
            column(name: "menu_id", type: "bigint",  remarks: "菜单id")   
            column(name: "permission_code", type: "varchar(" + 128 * weight + ")",  remarks: "权限的标识")   

        }

        addUniqueConstraint(columnNames:"menu_id,permission_code",tableName:"iam_menu_permission",constraintName: "iam_menu_permission_u1")
    }

    changeSet(author: "yuqing.zhang@hand-china.com", id: "2020-05-29-iam_menu_permission") {
        addColumn(tableName: 'iam_menu_permission') {
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }
        addColumn(tableName: 'iam_menu_permission') {
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
        }
        addColumn(tableName: 'iam_menu_permission') {
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }
        addColumn(tableName: 'iam_menu_permission') {
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
        }
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-iam_menu_permission") {
        addColumn(tableName: 'iam_menu_permission') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}
