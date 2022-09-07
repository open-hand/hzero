package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_cusz_unit_group.groovy') {
    changeSet(author: "peng.yu01@hand-china.com", id: "2020-01-14_hpfm_cusz_unit_group") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_cusz_unit_group_s', startValue: "1")
        }
        createTable(tableName: "hpfm_cusz_unit_group") {
            column(name: "unit_group_id", type: "bigint", autoIncrement: "true", remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "group_code", type: "varchar(" + 255 * weight + ")", remarks: "组编码") {
                constraints(nullable: false)
            }
            column(name: "group_name", type: "varchar(" + 255 * weight + ")", remarks: "组名称") {
                constraints(nullable: false)
            }
            column(name: "group_menu_code", type: "varchar(" + 255 * weight + ")", remarks: "菜单编码") {
                constraints(nullable: false)
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: false)
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "group_code", tableName: "hpfm_cusz_unit_group", constraintName: "hpfm_cusz_unit_group_U1")
    }
}