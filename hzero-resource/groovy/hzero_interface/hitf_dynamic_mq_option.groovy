package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_dynamic_mq_option.groovy') {

    changeSet(author: "Admin", id: "2020-05-08_hitf_dynamic_mq_option") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_dynamic_mq_option_S', startValue: "10001")
        }
        createTable(tableName: "hitf_dynamic_mq_option", remarks: "动态消息队列选项配置") {
            column(name: "option_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_dynamic_mq_option_PK")
            }
            column(name: "option_class", type: "varchar(" + 10 + ")", remarks: "选项类型：值集：HITF.DYNAMIC_MQ.OPTION_CLASS") {
                constraints(nullable: "false")
            }
            column(name: "source_id", type: "bigint", remarks: "消息中间件主键或消息绑定主键") {
                constraints(nullable: "false")
            }
            column(name: "property_key", type: "varchar(" + 255 + ")", remarks: "属性键") {
                constraints(nullable: "false")
            }
            column(name: "property_value", type: "varchar(" + 255 * weight + ")", remarks: "属性值")
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
        addUniqueConstraint(columnNames: "option_class,source_id,property_key,tenant_id", tableName: "hitf_dynamic_mq_option", constraintName: "hitf_dynamic_mq_option_U1")
    }
}