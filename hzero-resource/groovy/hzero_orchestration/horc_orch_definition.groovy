package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_orch_definition.groovy') {

    changeSet(author: "aaron.yi", id: "2020-06-04_horc_orch_definition") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_orch_definition_S', startValue: "10001")
        }
        createTable(tableName: "horc_orch_definition", remarks: "服务编排定义") {
            column(name: "definition_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_orch_definition_PK")
            }
            column(name: "definition_name", type: "varchar(" + 255 * weight + ")", remarks: "编排定义名称") {
                constraints(nullable: "false")
            }
            column(name: "status_code", type: "varchar(" + 30 + ")", remarks: "状态码, 值集：horc.ORCH.DEF_STATUS，0:OFFLINE,1:ONLINE") {
                constraints(nullable: "false")
            }
            column(name: "definition_json", type: "clob", remarks: "编排定义配置json") {
                constraints(nullable: "false")
            }
            column(name: "global_param", type: "clob", remarks: "全局参数")
            column(name: "graph_layout", type: "clob", remarks: "编排定义布局") {
                constraints(nullable: "false")
            }
            column(name: "alert_receiver", type: "clob", remarks: "通知收件人")
            column(name: "alert_receiver_cc", type: "clob", remarks: "通知抄送人")
            column(name: "timeout", type: "bigint", defaultValue: "0", remarks: "超时时间") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 255 * weight + ")", remarks: "备注说明")
            column(name: "version", type: "bigint", defaultValue: "0", remarks: "版本") {
                constraints(nullable: "false")
            }
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
        addUniqueConstraint(columnNames:"definition_name,version,tenant_id",tableName:"horc_orch_definition",constraintName: "horc_orch_definition_U2")
    }
}