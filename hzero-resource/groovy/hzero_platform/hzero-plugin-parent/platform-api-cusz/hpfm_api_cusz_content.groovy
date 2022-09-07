package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_api_cusz_content.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-31-hpfm_api_cusz_content") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_api_cusz_content_s', startValue: "1")
        }
        createTable(tableName: "hpfm_api_cusz_content", remarks: "API个性化内容") {
            column(name: "customize_content_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "customize_id", type: "bigint", remarks: "个性化ID，表hpfm_api_cusz.customize_id") { constraints(nullable: "false") }
            column(name: "content_type", type: "varchar(" + 30 * weight + ")", remarks: "个性化内容类型，HPFM.API_CUSTOMIZE_CONTENT_TYPE") { constraints(nullable: "false") }
            column(name: "customize_content", type: "longtext", remarks: "个性化内容") { constraints(nullable: "false") }
            column(name: "version_number", type: "int", remarks: "个性化内容的版本号") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", remarks: "所属租户ID") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "customize_id,version_number", tableName: "hpfm_api_cusz_content", constraintName: "hpfm_api_cusz_content_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hpfm_api_cusz_content") {
        addColumn(tableName: "hpfm_api_cusz_content") {
            column(name: "current_version_flag", type: "tinyint", defaultValue: "0", remarks: "是否为当前版本")  {constraints(nullable:"false")}
        }
    }
}