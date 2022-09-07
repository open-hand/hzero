package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_model_request_param.groovy') {
    changeSet(author: "heng.zhang06@hand-china.com", id: "2020-03-12_hitf_model_request_param") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_model_request_param_S', startValue: "10001")
        }
        createTable(tableName: "hitf_model_request_param", remarks: "模型请求参数") {
            column(name: "param_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_model_request_param_PK")
            }
            column(name: "model_id", type: "bigint", remarks: "模型(hitf_model_config.MODEL_ID)") {
                constraints(nullable: "false")
            }
            column(name: "param_name", type: "varchar(" + 100 + ")", remarks: "参数名") {
                constraints(nullable: "false")
            }
            column(name: "param_type", type: "varchar(" + 30 + ")", remarks: "参数类型(STRING,NUMBER)，值集：HITF.SVC.PARAM_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "param_desc", type: "varchar(" + 255 * weight + ")", remarks: "描述")
            column(name: "field_id", type: "bigint", remarks: "模型字段(hitf_model_field.FIELD_ID)") {
                constraints(nullable: "false")
            }
            column(name: "required_flag", type: "Tinyint", defaultValue: "0", remarks: "是否必输(1是，0否)") {
                constraints(nullable: "false")
            }
            column(name: "default_value", type: "varchar(" + 255 * weight + ")", remarks: "默认值")
            column(name: "sample_value", type: "varchar(" + 255 * weight + ")", remarks: "样例值")
            column(name: "operator_code", type: "varchar(" + 30 + ")", remarks: "操作符(大于，小于，小于等于，包含，不包含等),值集：HITF.SVC.MODEL_OPERATOR")
            column(name: "seq_num", type: "bigint", remarks: "参数顺序") {
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
        addUniqueConstraint(columnNames: "model_id,field_id,operator_code,tenant_id", tableName: "hitf_model_request_param", constraintName: "hitf_model_request_param_U1")
        addUniqueConstraint(columnNames: "model_id,param_name,tenant_id", tableName: "hitf_model_request_param", constraintName: "hitf_model_request_param_U2")
    }
}