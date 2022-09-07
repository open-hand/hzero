package script.db

databaseChangeLog(logicalFilePath: 'script/db/horc_transform_his.groovy') {
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-07-15_horc_transform_his") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'horc_transform_his_S', startValue: "10001")
        }
        createTable(tableName: "horc_transform_his", remarks: "映射转化历史配置") {
            column(name: "transform_his_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "horc_transform_his_PK")
            }
            column(name: "transform_id", type: "bigint", remarks: "映射转化ID") {
                constraints(nullable: "false")
            }
            column(name: "transform_code", type: "varchar(80)", remarks: "映射转化代码") {
                constraints(nullable: "false")
            }
            column(name: "transform_name", type: "varchar(" + 80 * weight + ")", remarks: "映射转化名称") {
                constraints(nullable: "false")
            }
            column(name: "transform_type", type: "varchar(30)", defaultValue: "REST_TO_REST", remarks: "映射转化类型") {
                constraints(nullable: "false")
            }
            column(name: "transform_script", type: "clob", remarks: "映射转化脚本") {
                constraints(nullable: "false")
            }
            column(name: "source_structure", type: "clob", remarks: "映射来源结构")
            column(name: "target_structure", type: "clob", remarks: "映射目标结构")
            column(name: "version", type: "decimal(8,4)", remarks: "映射转化配置版本") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "varchar(" + 255 * weight + ")", remarks: "备注说明")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }
}