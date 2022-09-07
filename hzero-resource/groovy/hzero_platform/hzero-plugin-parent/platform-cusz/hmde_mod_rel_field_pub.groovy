package script.db

databaseChangeLog(logicalFilePath: 'hmde_mod_rel_field_pub.groovy') {

    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }

    changeSet(author: "jiameng.cao", id: "2020-01-07_hmde_mod_rel_field_pub") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HMDE_MOD_REL_FIELD_PUB_S', startValue: "10001")
        }
        createTable(tableName: "hmde_mod_rel_field_pub", remarks: "模型关系关联字段发布") {
            column(name: "id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hmde_mod_rel_field_pub_pk")
            }
            column(name: "relation_id", type: "bigint", remarks: "模型关系ID，hmde_model_relation_pub.id") {
                constraints(nullable: "false")
            }
            column(name: "master_model_field_code", type: "varchar(" + 32 * weight + ")", remarks: "主字段代码，hmde_model_field_pub.code") {
                constraints(nullable: "false")
            }
            column(name: "relation_model_field_code", type: "varchar(" + 32 * weight + ")", remarks: "关联字段代码，hmde_model_field_pub.code") {
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
        addUniqueConstraint(columnNames: "relation_id,master_model_field_code,relation_model_field_code", tableName: "hmde_mod_rel_field_pub", constraintName: "hmde_mod_rel_field_pub_u1")

    }

}