package script.db

databaseChangeLog(logicalFilePath: 'hmde_model_field_pub.groovy') {

    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }

    changeSet(author: "jiameng.cao", id: "2020-01-07_hmde_model_field_pub") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HMDE_MODEL_FIELD_PUB_S', startValue: "10001")
        }
        createTable(tableName: "hmde_model_field_pub", remarks: "模型字段发布") {
            column(name: "id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hmde_model_field_pub_pk")
            }
            column(name: "model_object_id", type: "bigint", remarks: "模型对象ID，hmde_model_object_pub.id") {
                constraints(nullable: "false")
            }
            column(name: "code", type: "varchar(" + 32 * weight + ")", remarks: "字段代码") {
                constraints(nullable: "false")
            }
            column(name: "field_name", type: "varchar(" + 120 * weight + ")", remarks: "字段名称") {
                constraints(nullable: "false")
            }
            column(name: "display_name", type: "varchar(" + 120 * weight + ")", remarks: "显示名称") {
                constraints(nullable: "false")
            }
            column(name: "data_type", type: "varchar(" + 30 * weight + ")", remarks: "字段数据类型") {
                constraints(nullable: "false")
            }
            column(name: "data_size", type: "int", remarks: "字段数据长度") {
                constraints(nullable: "false")
            }
            column(name: "default_value", type: "varchar(" + 255 * weight + ")", remarks: "字段默认值")
            column(name: "description", type: "varchar(" + 255 * weight + ")", remarks: "字段描述")
            column(name: "required_flag", type: "tinyint", defaultValue: "0", remarks: "是否必输。1是，0不是") {
                constraints(nullable: "false")
            }
            column(name: "change_required_flag", type: "tinyint", defaultValue: "0", remarks: "是否可修改必输。1是，0不是") {
                constraints(nullable: "false")
            }
            column(name: "primary_flag", type: "tinyint", defaultValue: "0", remarks: "是否主键。1是，0不是") {
                constraints(nullable: "false")
            }
            column(name: "multi_language_flag", type: "tinyint", defaultValue: "0", remarks: "是否多语言字段。1是，0不是") {
                constraints(nullable: "false")
            }
            column(name: "field_type", type: "varchar(" + 20 * weight + ")", remarks: "字段类型") {
                constraints(nullable: "false")
            }
            column(name: "formula_type", type: "varchar(" + 20 * weight + ")", remarks: "公式类型")
            column(name: "formula_content", type: "clob", remarks: "公式内容")
            column(name: "value_list_type", type: "varchar(" + 20 * weight + ")", remarks: "值集类型")
            column(name: "value_list_code", type: "varchar(" + 60 * weight + ")", remarks: "值集编码或值集视图编码")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP") {
                constraints(nullable: "false")
            }
        }
        addUniqueConstraint(columnNames: "model_object_id,field_name", tableName: "hmde_model_field_pub", constraintName: "hmde_model_field_pub_u1")
        addUniqueConstraint(columnNames: "code", tableName: "hmde_model_field_pub", constraintName: "hmde_model_field_pub_u2")

    }

}