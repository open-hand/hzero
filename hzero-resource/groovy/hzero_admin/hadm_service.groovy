package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_service.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hadm_service") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hadm_service_s', startValue: "1")
        }
        createTable(tableName: "hadm_service", remarks: "服务") {
            column(name: "service_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "service_code", type: "varchar(" + 60 * weight + ")", remarks: "服务编码") {
                constraints(nullable: "false")
            }
            column(name: "service_name", type: "varchar(" + 90 * weight + ")", remarks: "服务名称") {
                constraints(nullable: "false")
            }
            column(name: "service_logo", type: "varchar(" + 255 * weight + ")", remarks: "服务图标")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
        }
        addUniqueConstraint(columnNames: "service_code", tableName: "hadm_service", constraintName: "hadm_service_u1")
    }

}