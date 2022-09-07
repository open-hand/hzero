package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_dynamic_mq_binder.groovy') {

    changeSet(author: "Admin", id: "2020-05-08_hitf_dynamic_mq_binder") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_dynamic_mq_binder_S', startValue: "10001")
        }
        createTable(tableName: "hitf_dynamic_mq_binder", remarks: "动态消息队列中间件配置") {
            column(name: "binder_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_dynamic_mq_binder_PK")
            }
            column(name: "binder_name", type: "varchar(" + 100 + ")", remarks: "中间件名称") {
                constraints(nullable: "false")
            }
            column(name: "binder_type", type: "varchar(" + 30 + ")", remarks: "中间件类型，值集：HITF.DYNAMIC_MQ.BINDER_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "启用标识") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 255 * weight + ")", remarks: "备注说明")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
        addUniqueConstraint(columnNames: "binder_name,tenant_id", tableName: "hitf_dynamic_mq_binder", constraintName: "hitf_dynamic_mq_binder_U1")
    }
}