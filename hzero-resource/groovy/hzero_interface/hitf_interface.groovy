package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-28-hitf_interface") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_interface_s', startValue: "1")
        }
        createTable(tableName: "hitf_interface", remarks: "接口配置") {
            column(name: "interface_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "interface_server_id", type: "bigint", remarks: "服务配置ID") { constraints(nullable: "false") }
            column(name: "interface_code", type: "varchar(" + 30 * weight + ")", remarks: "接口代码") { constraints(nullable: "false") }
            column(name: "interface_name", type: "varchar(" + 250 * weight + ")", remarks: "接口名称") { constraints(nullable: "false") }
            column(name: "interface_url", type: "varchar(" + 255 * weight + ")", remarks: "接口地址")
            column(name: "publish_type", type: "varchar(" + 30 * weight + ")", defaultValue: "REST", remarks: "发布类型，代码：HITF.SERVICE_TYPE") { constraints(nullable: "false") }
            column(name: "mapping_class", type: "varchar(" + 255 * weight + ")", remarks: "映射类，处理请求参数及响应格式的映射")
            column(name: "request_method", type: "varchar(" + 30 * weight + ")", remarks: "请求方式，代码：HITF.REQUEST_METHOD")
            column(name: "request_header", type: "varchar(1800)", remarks: "请求头")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用，1代表启用，0代表禁用") { constraints(nullable: "false") }
            column(name: "soap_version", type: "varchar(" + 50 * weight + ")", remarks: "SOAP版本，代码：HITF.SOAP_VERSION")
            column(name: "soap_action", type: "varchar(" + 255 * weight + ")", remarks: "SOAPACTION")
            column(name: "invoke_record_details", type: "tinyint", defaultValue: "0", remarks: "是否记录调用详情") { constraints(nullable: "false") }
            column(name: "status", type: "varchar(" + 30 * weight + ")", defaultValue: "ENABLED", remarks: "状态，代码：HITF.INTERFACE_STATUS, ENABLED/DISABLED/DISABLE_INPROGRESS") { constraints(nullable: "false") }
            column(name: "remark", type: "longtext", remarks: "备注说明")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }

        }
        createIndex(tableName: "hitf_interface", indexName: "hitf_interface_n1") {
            column(name: "interface_server_id")
            column(name: "enabled_flag")
        }

        addUniqueConstraint(columnNames: "interface_server_id,interface_code", tableName: "hitf_interface", constraintName: "hitf_interface_u1")
    }

    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-06-24-hitf_interface") {
        if (helper.isSqlServer()) {
            dropDefaultValue(tableName: 'hitf_interface', columnName: 'invoke_record_details')
        }
        dropColumn(tableName: "hitf_interface", columnName: "invoke_record_details")
    }
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-08-22-hitf_interface") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        modifyDataType(tableName: "hitf_interface", columnName: "interface_code", newDataType: "varchar(" + 128 * weight + ")")
    }
    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-09-16-hitf_interface") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        dropColumn(tableName: "hitf_interface", columnName: "mapping_class")
        addColumn(tableName: 'hitf_interface') {
            column(name: "mapping_class", type: "longtext", remarks: "映射类，处理请求参数及响应格式的映射")
        }
    }
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-06-29-hitf_interface") {
        addColumn(tableName: 'hitf_interface') {
            column(name: "request_transform_id", type: "bigint", remarks: "请求数据映射ID")
        }
        addColumn(tableName: 'hitf_interface') {
            column(name: "response_transform_id", type: "bigint", remarks: "响应数据映射ID")
        }
    }
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-07-08-hitf_interface") {
        addColumn(tableName: 'hitf_interface') {
            column(name: "request_cast_id", type: "bigint", remarks: "请求数据转化ID")
        }
        addColumn(tableName: 'hitf_interface') {
            column(name: "response_cast_id", type: "bigint", remarks: "响应数据转化ID")
        }
    }

    changeSet(author: "he.chen@hand-china.com", id: "2020-07-28-hitf_interface") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hitf_interface') {
            column(name: "retry_times", type: "bigint", remarks: "重试次数")
        }
        addColumn(tableName: 'hitf_interface') {
            column(name: "retry_interval", type: "bigint", remarks: "重试间隔(s)")
        }
        addColumn(tableName: 'hitf_interface') {
            column(name: "assert_json", type: "varchar(" + 500 * weight + ")", remarks: "重试断言")
        }
    }

    changeSet(author: "changwen.yu@hand-china.com", id: "2020-08-04-hitf_interface") {
        addColumn(tableName: 'hitf_interface') {
            column(name: "version", type: "int", remarks: "接口配置版本", defaultValue: "1") { constraints(nullable: "false") }
        }
    }

    changeSet(author: "mingke.yan@hand-china.com", id: "2020-08-04-hitf_interface") {
        addColumn(tableName: 'hitf_interface') {
            column(name: "body_namespace_flag", type: "tinyint", defaultValue: "1", remarks: "是否设置body命名空间") { constraints(nullable: "false") }
        }
    }

    changeSet(author: "mingke.yan@hand-china.com", id: "2020-08-07-hitf_interface") {
        addColumn(tableName: 'hitf_interface') {
            column(name: "async_flag", type: "tinyint", defaultValue: "0", remarks: "是否异步调用") { constraints(nullable: "false") }
        }
    }
}