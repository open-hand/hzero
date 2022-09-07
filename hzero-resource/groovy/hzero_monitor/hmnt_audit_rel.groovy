package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmnt_audit_rel.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-08-17-hmnt_audit_rel") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hmnt_audit_rel_s', startValue: "1")
        }
        createTable(tableName: "hmnt_audit_rel", remarks: "审计配置关联") {
            column(name: "audit_rel_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "audit_op_config_id", type: "bigint", remarks: "操作审计配置ID") { constraints(nullable: "false") }
            column(name: "audit_data_config_id", type: "bigint", remarks: "数据审计配置ID") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID，hpfm_tenant.tenant_id") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }

        addUniqueConstraint(columnNames: "audit_op_config_id,audit_data_config_id,tenant_id", tableName: "hmnt_audit_rel", constraintName: "hmnt_audit_rel_u1")
    }
}