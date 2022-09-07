package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_api_cusz.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-07-31-hpfm_api_cusz") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_api_cusz_s', startValue: "1")
        }
        createTable(tableName: "hpfm_api_cusz", remarks: "API个性化配置") {
            column(name: "customize_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") { constraints(primaryKey: true) }
            column(name: "customize_content_id", type: "bigint", remarks: "个性化内容，表hpfm_api_cusz_content.customize_content_id") { constraints(nullable: "false") }
            column(name: "customize_code", type: "varchar(" + 60 * weight + ")", remarks: "API个性化编码") { constraints(nullable: "false") }
            column(name: "customize_name", type: "varchar(" + 240 * weight + ")", remarks: "API个性化名称") { constraints(nullable: "false") }
            column(name: "service_name", type: "varchar(" + 60 * weight + ")", remarks: "服务名") { constraints(nullable: "false") }
            column(name: "package_name", type: "varchar(" + 240 * weight + ")", remarks: "包名") { constraints(nullable: "false") }
            column(name: "class_name", type: "varchar(" + 180 * weight + ")", remarks: "类名") { constraints(nullable: "false") }
            column(name: "method_name", type: "varchar(" + 180 * weight + ")", remarks: "方法名") { constraints(nullable: "false") }
            column(name: "method_args", type: "longtext", remarks: "方法参数列表")
            column(name: "customize_position", type: "varchar(" + 30 * weight + ")", remarks: "API个性化位置，HPFM.API_CUSTOMIZE_POSITION") { constraints(nullable: "false") }
            column(name: "sync_flag", type: "tinyint", defaultValue: "1", remarks: "是否同步调用 1：同步；0异步；默认1；") { constraints(nullable: "false") }
            column(name: "customize_status", type: "varchar(" + 30 * weight + ")", defaultValue: "NEW", remarks: "API个性化状态，HPFM.API_CUSTOMIZE_STATUS") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", remarks: "所属租户ID") { constraints(nullable: "false") }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }

        createIndex(tableName: "hpfm_api_cusz", indexName: "hpfm_api_cusz_n1") {
            column(name: "service_name")
            column(name: "package_name")
            column(name: "class_name")
            column(name: "method_name")
        }

        addUniqueConstraint(columnNames: "customize_code,tenant_id", tableName: "hpfm_api_cusz", constraintName: "hpfm_api_cusz_u1")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-08-31-hpfm_api_cusz") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: "hpfm_api_cusz") {
            column(name: "method_return", type: "varchar(" + 180 * weight + ")", remarks: "方法返回值类型")
        }
    }

    changeSet(id: '2020-09-04-hpfm_api_cusz', author: 'hzero@hand-china.com') {
        dropColumn(tableName: 'hpfm_api_cusz', columnName: 'customize_content_id')
    }

    changeSet(id: '2020-09-03-hpfm_api_cusz', author: 'hzero@hand-china.com') {
        dropDefaultValue(tableName: "hpfm_api_cusz", columnName: "customize_status")
        addDefaultValue(tableName: "hpfm_api_cusz", columnName: "customize_status", defaultValue: "UN_APPLIED")
    }
}