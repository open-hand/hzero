package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_service_config_his.groovy'){
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hadm_service_config_his") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hadm_service_config_his_s', startValue: "1")
        }
        createTable(tableName: "hadm_service_config_his", remarks: "服务配置") {
            column(name: "service_config_his_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "service_config_id", type: "bigint", remarks: "服务配置ID") { constraints(nullable: "false") }
            column(name: "service_config_his_code", type: "varchar(" + 30 * weight + ")", remarks: "配置历史编码") {
                constraints(nullable: "false")
            }
            column(name: "config_yaml", type: "longtext", remarks: "yaml配置")
            column(name: "config_value", type: "longtext", remarks: "json配置")
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
        addUniqueConstraint(columnNames: "service_config_his_code", tableName: "hadm_service_config_his", constraintName: "hadm_service_config_his_u1")
    }

}