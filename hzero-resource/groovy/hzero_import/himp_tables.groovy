package script.db

databaseChangeLog(logicalFilePath: 'classpath:config/db/himp_tables.xml') {
    changeSet(author: "hzero@hand-china.com", id: "2019-04-11-himp_tables") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'himp_data_s', startValue:"1")
        }
        createTable(tableName: "himp_data", remarks: "导入临时数据") {
            column(name: "id", type: "bigint", autoIncrement: true)  {constraints(primaryKey: true)}
            column(name: "batch", type: "varchar(" + 60 * weight + ")",  remarks: "批次")  {constraints(nullable:"false")}
            column(name: "template_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板编码")  {constraints(nullable:"false")}
            column(name: "data_status", type: "varchar(" + 30 * weight + ")",  remarks: "数据状态[NEW(Excel导入),VALID_SUCCESS(验证成功),VALID_FAILED(验证失败),IMPORT_SUCCESS(导入成功),IMPORT_FAILED(导入失败)]")  {constraints(nullable:"false")}
            column(name: "sheet_index", type: "int",    remarks: "页码")  {constraints(nullable:"false")}
            column(name: "error_msg", type: "varchar(" + 255 * weight + ")",  remarks: "错误信息")
            column(name: "data", type: "longtext",  remarks: "数据")
        }

        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'himp_import_s', startValue:"1")
        }
        createTable(tableName: "himp_import", remarks: "") {
            column(name: "import_id", type: "bigint", autoIncrement: true,  remarks: "主键 表ID")  {constraints(primaryKey: true)}
            column(name: "batch", type: "varchar(" + 60 * weight + ")",  remarks: "批次")  {constraints(nullable:"false")}
            column(name: "status", type: "varchar(" + 30 * weight + ")",  remarks: "当前状态")  {constraints(nullable:"false")}
            column(name: "data_count", type: "int",    remarks: "数据数量")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",  remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",  remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   remarks: "")  {constraints(nullable:"false")}
        }

        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'himp_local_template_s', startValue:"1")
        }
        createTable(tableName: "himp_local_template", remarks: "") {
            column(name: "id", type: "bigint", autoIncrement: true,  remarks: "主键")  {constraints(primaryKey: true)}
            column(name: "template_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板编码")  {constraints(nullable:"false")}
            column(name: "tenant_id", type: "bigint",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "template_json", type: "longtext",  remarks: "模板JSON内容")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",  remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",  remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   remarks: "")  {constraints(nullable:"false")}
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-08-12-himp_tables") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'himp_import') {
            column(name: "template_code", type: "varchar(" + 30 * weight + ")",  remarks: "模板编码",   defaultValue:"")  {constraints(nullable:"false")}
        }
        addColumn(tableName: 'himp_import') {
            column(name: "tenant_id", type: "bigint",  remarks: "租户",   defaultValue:"0")  {constraints(nullable:"false")}
        }
        addColumn(tableName: 'himp_import') {
            column(name: "param", type: "longtext",  remarks: "自定义参数")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-12-19-himp_data") {
        addColumn(tableName: 'himp_data') {
            column(name: "back_info", type: "longtext",  remarks: "回写信息")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-01-06-himp_data") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "himp_data",  columnName: "batch",    newDataType:"varchar(" + 120 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-01-06-himp_import") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType(tableName: "himp_import",  columnName: "batch",    newDataType:"varchar(" + 120 * weight + ")")
    }

    changeSet(author: 'hzero@hand-china.com', id: '2020-05-18-himp_data') {
        createIndex(tableName: "himp_data", indexName: "himp_data_n1") {
            column(name: "batch")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-18-himp_import") {
        addUniqueConstraint(tableName: "himp_import", constraintName: "himp_import_u1", columnNames: "batch")
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-05-18-himp_local_template") {
        addUniqueConstraint(tableName: "himp_local_template", constraintName: "himp_local_template_u1", columnNames: "template_code,tenant_id")
    }


}