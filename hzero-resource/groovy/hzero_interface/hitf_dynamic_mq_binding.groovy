package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_dynamic_mq_binding.groovy') {

    changeSet(author: "Admin", id: "2020-05-08_hitf_dynamic_mq_binding") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_dynamic_mq_binding_S', startValue: "10001")
        }
        createTable(tableName: "hitf_dynamic_mq_binding", remarks: "动态消息队列绑定") {
            column(name: "binding_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_dynamic_mq_binding_PK")
            }
            column(name: "binder_id", type: "bigint", remarks: "动态消息队列中间件配置主键，hitf_dynamic_mq_binder.binder_id") {
                constraints(nullable: "false")
            }
            column(name: "binding_name", type: "varchar(" + 100 + ")", remarks: "绑定名称") {
                constraints(nullable: "false")
            }
            column(name: "binding_type", type: "varchar(" + 10 + ")", remarks: "绑定类型，值集：HITF.DYNAMIC_MQ.BINDING_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "destination", type: "varchar(" + 50 + ")", remarks: "路由目标（topic）") {
                constraints(nullable: "false")
            }
            column(name: "binding_group", type: "varchar(" + 50 + ")", remarks: "生产或消费组") {
                constraints(nullable: "false")
            }
            column(name: "content_type", type: "varchar(" + 30 + ")", remarks: "内容类型")
            column(name: "charset", type: "varchar(" + 30 + ")", remarks: "字符集，值集：HITF.CHARSET")
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
        addUniqueConstraint(columnNames: "binding_name,tenant_id", tableName: "hitf_dynamic_mq_binding", constraintName: "hitf_dynamic_mq_binding_U1")
    }
}