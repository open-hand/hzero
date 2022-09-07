package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_proxy.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_proxy") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_proxy_s', startValue: "10001")
        }
        createTable(tableName: "hebk_proxy", remarks: "代理") {
            column(name: "proxy_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_proxy_pk")
            }
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用") {
                constraints(nullable: "false")
            }
            column(name: "name", type: "varchar(" + 120 * weight + ")", remarks: "名称") {
                constraints(nullable: "false")
            }
            column(name: "host", type: "varchar(" + 30 * weight + ")", remarks: "地址") {
                constraints(nullable: "false")
            }
            column(name: "port", type: "int", remarks: "端口") {
                constraints(nullable: "false")
            }
            column(name: "ssl_enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用SSL") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 240 * weight + ")", remarks: "备注说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "tenant_id,name", tableName: "hebk_proxy", constraintName: "hebk_proxy_u1")
    }
}