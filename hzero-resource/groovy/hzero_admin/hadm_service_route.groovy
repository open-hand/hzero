package script.db

databaseChangeLog(logicalFilePath: 'script/db/hadm_service_route.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2019-11-01-hadm_service_route") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hadm_service_route_s', startValue: "1")
        }
        createTable(tableName: "hadm_service_route", remarks: "服务路由配置") {
            column(name: "service_route_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "service_id", type: "bigint", remarks: "服务ID") { constraints(nullable: "false") }
            column(name: "service_code", type: "varchar(" + 60 * weight + ")", remarks: "路由对应的服务") {
                constraints(nullable: "false")
            }
            column(name: "name", type: "varchar(" + 60 * weight + ")", remarks: "服务id，Route的标识，对应Route的id字段") {
                constraints(nullable: "false")
            }
            column(name: "path", type: "varchar(" + 120 * weight + ")", remarks: "服务路径") {
                constraints(nullable: "false")
            }
            column(name: "url", type: "varchar(" + 240 * weight + ")", remarks: "物理路径")
            column(name: "strip_prefix", type: "tinyint", defaultValue: "1", remarks: "是否去前缀")
            column(name: "custom_sensitive_headers", type: "tinyint", defaultValue: "0", remarks: "是否自定义敏感头")
            column(name: "sensitive_headers", type: "varchar(" + 240 * weight + ")", remarks: "敏感头部列表")
            column(name: "extend_config_map", type: "mediumtext", remarks: "路由额外配置") {
                constraints(nullable: "true")
            }
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
        addUniqueConstraint(columnNames: "name", tableName: "hadm_service_route", constraintName: "hadm_service_route_u1")
        addUniqueConstraint(columnNames: "path", tableName: "hadm_service_route", constraintName: "hadm_service_route_u2")
    }

}