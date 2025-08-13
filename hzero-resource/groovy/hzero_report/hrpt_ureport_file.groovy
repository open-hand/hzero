package script.db

databaseChangeLog(logicalFilePath: 'script/db/hrpt_ureport_file.groovy') {
    changeSet(author: "hzero", id: "2020-09-04-hrpt_ureport_file") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hrpt_ureport_file_s', startValue: "1")
        }
        createTable(tableName: "hrpt_ureport_file", remarks: "ureport报表文件") {
            column(name: "file_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "file_name", type: "varchar(" + 180 * weight + ")", remarks: "文件名") { constraints(nullable: "false") }
            column(name: "file_url", type: "varchar(" + 480 * weight + ")", remarks: "文件url") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        addUniqueConstraint(columnNames: "file_name,tenant_id", tableName: "hrpt_ureport_file", constraintName: "hrpt_ureport_file_u1")
    }
}