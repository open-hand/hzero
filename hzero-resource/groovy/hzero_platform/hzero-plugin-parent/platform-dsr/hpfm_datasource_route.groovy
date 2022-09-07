package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_datasource_route.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-04-28-hpfm_datasource_route") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_datasource_route_s', startValue: "1")
        }
        createTable(tableName: "hpfm_datasource_route", remarks: "数据源路由规则") {
            column(name: "ds_route_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_num", type: "varchar(" + 60 * weight + ")", remarks: "租户编码") {
                constraints(nullable: "false")
            }
            column(name: "service_code", type: "varchar(" + 60 * weight + ")", remarks: "服务编码，hadm_service.service_code") {
                constraints(nullable: "false")
            }
            column(name: "url", type: "varchar(" + 240 * weight + ")", remarks: "路由接口") {
                constraints(nullable: "false")
            }
            column(name: "method", type: "varchar(" + 30 * weight + ")", remarks: "路由接口请求方式") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "tinyint", defaultValue:"0", remarks: "启用标识") {
                constraints(nullable: "false")
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
        addUniqueConstraint(columnNames: "tenant_num,service_code,url,method", tableName: "hpfm_datasource_route", constraintName: "hpfm_datasource_route_u1")
    }

}