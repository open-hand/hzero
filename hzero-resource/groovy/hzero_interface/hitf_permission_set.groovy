package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_permission_set.groovy') {
    changeSet(author: "he.chen@hand-china.com", id: "2020-07-10_hitf_permission_set") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_permission_set_S', startValue: "10001")
        }
        createTable(tableName: "hitf_permission_set", remarks: "接口权限集") {
            column(name: "id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_permission_set_PK")
            }
            column(name: "role_id", type: "bigint", remarks: "角色id")
            column(name: "permission_id", type: "bigint", remarks: "权限id")
            column(name: "type", type: "varchar(" + 30 * weight + ")", defaultValue: "HITF_ITF", remarks: "权限类型") {
                constraints(nullable: "false")
            }
            column(name: "create_flag", type: "varchar(" + 1 * weight + ")", defaultValue: "Y", remarks: "创建标识")
            column(name: "inherit_flag", type: "varchar(" + 1 * weight + ")", defaultValue: "N", remarks: "继承标识")
        }
        addUniqueConstraint(columnNames: "role_id,permission_id,type", tableName: "hitf_permission_set", constraintName: "hitf_permission_set_U1")
    }
}