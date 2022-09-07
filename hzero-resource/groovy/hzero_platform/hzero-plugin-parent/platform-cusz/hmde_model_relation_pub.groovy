package script.db

databaseChangeLog(logicalFilePath: 'hmde_model_relation_pub.groovy') {

    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }

    changeSet(author: "jiameng.cao", id: "2020-01-07_hmde_model_relation_pub") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HMDE_MODEL_RELATION_PUB_S', startValue: "10001")
        }
        createTable(tableName: "hmde_model_relation_pub", remarks: "模型关系发布") {
            column(name: "id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hmde_model_relation_pub_pk")
            }
            column(name: "code", type: "varchar(" + 32 * weight + ")", remarks: "关系代码") {
                constraints(nullable: "false")
            }
            column(name: "name", type: "varchar(" + 120 * weight + ")", remarks: "关系名称") {
                constraints(nullable: "false")
            }
            column(name: "relation_type", type: "varchar(" + 20 * weight + ")", remarks: "关系类型。包括ONE_TO_ONE，ONE_TO_MANY") {
                constraints(nullable: "false")
            }
            column(name: "master_model_object_code", type: "varchar(" + 32 * weight + ")", remarks: "主模型代码，hmde_model_object_pub.code") {
                constraints(nullable: "false")
            }
            column(name: "relation_model_object_code", type: "varchar(" + 32 * weight + ")", remarks: "关联模型代码，hmde_model_object_pub.code") {
                constraints(nullable: "false")
            }
            column(name: "description", type: "varchar(" + 255 * weight + ")", remarks: "关系描述")
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
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
        addUniqueConstraint(columnNames: "master_model_object_code,relation_model_object_code", tableName: "hmde_model_relation_pub", constraintName: "hmde_model_relation_pub_u2")
        addUniqueConstraint(columnNames: "code", tableName: "hmde_model_relation_pub", constraintName: "hmde_model_relation_pub_u3")

    }

}