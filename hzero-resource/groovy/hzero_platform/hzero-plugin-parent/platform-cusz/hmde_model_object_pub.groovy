package script.db

databaseChangeLog(logicalFilePath: 'hmde_model_object_pub.groovy') {

    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }

    changeSet(author: "jiameng.cao", id: "2020-01-07_hmde_model_object_pub") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HMDE_MODEL_OBJECT_PUB_S', startValue: "10001")
        }
        createTable(tableName: "hmde_model_object_pub", remarks: "模型对象发布") {
            column(name: "id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hmde_model_object_pub_pk")
            }
            column(name: "code", type: "varchar(" + 32 * weight + ")", remarks: "模型代码") {
                constraints(nullable: "false")
            }
            column(name: "name", type: "varchar(" + 120 * weight + ")", remarks: "模型名称") {
                constraints(nullable: "false")
            }
            column(name: "data_source_type", type: "varchar(" + 30 * weight + ")", remarks: "数据来源类型。如基础表、接口、文件等", defaultValue: "TABLE") {
                constraints(nullable: "false")
            }
            column(name: "ref_table_code", type: "varchar(" + 32 * weight + ")", remarks: "引用表代码，hmde_table.code")
            column(name: "ref_service_name", type: "varchar(" + 120 * weight + ")", remarks: "引用表所属服务名称，hmde_table.service_name")
            column(name: "ref_database_name", type: "varchar(" + 120 * weight + ")", remarks: "引用表所属数据库，hmde_table.database_name")
            column(name: "ref_database_type", type: "varchar(" + 30 * weight + ")", remarks: "引用表所属数据库类型，hmde_table.database_type")
            column(name: "ref_table_name", type: "varchar(" + 30 * weight + ")", remarks: "引用表名称，hmde_table.name")
            column(name: "primary_key", type: "varchar(" + 120 * weight + ")", remarks: "引用表主键字段，hmde_table_column.name")
            column(name: "multi_language_flag", type: "tinyint", defaultValue: "0", remarks: "是否为多语言表。1是，0不是")
            column(name: "description", type: "varchar(" + 255 * weight + ")", remarks: "模型描述")
            column(name: "type", type: "varchar(" + 20 * weight + ")", remarks: "模型类型", defaultValue: "TABLE") {
                constraints(nullable: "false")
            }
            column(name: "publish_version", type: "bigint", defaultValue: "1", remarks: "模型版本号") {
                constraints(nullable: "false")
            }
            column(name: "app_id", type: "bigint", remarks: "应用ID", defaultValue: "0") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "tenant_flag", type: "tinyint", defaultValue: "0", remarks: "是否是租户级。1是，0不是") {
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
        addUniqueConstraint(columnNames: "code", tableName: "hmde_model_object_pub", constraintName: "hmde_model_object_pub_u1")
    }
}