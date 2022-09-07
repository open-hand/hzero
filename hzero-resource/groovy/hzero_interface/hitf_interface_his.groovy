package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_his.groovy') {
    changeSet(author: "Admin", id: "2020-08-05_HITF_INTERFACE_HIS") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_interface_his_s', startValue: "10001")
        }
        createTable(tableName: "hitf_interface_his", remarks: "接口配置历史表") {
            column(name: "interface_his_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hitf_interface_his_pk")
            }
            column(name: "interface_id", type: "bigint", remarks: "接口配置id, hitf_interface") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "interface_server_id", type: "bigint", remarks: "服务配置ID, hitf_interface_server") {
                constraints(nullable: "false")
            }
            column(name: "interface_code", type: "varchar(" + 128 * weight + ")", remarks: "接口代码")
            column(name: "interface_name", type: "varchar(" + 250 * weight + ")", remarks: "接口名称") {
                constraints(nullable: "false")
            }
            column(name: "interface_url", type: "varchar(" + 255 * weight + ")", remarks: "接口地址")
            column(name: "publish_type", type: "varchar(30)", defaultValue: "REST", remarks: "发布类型，代码：HITF.SERVICE_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "request_method", type: "varchar(30)", remarks: "请求方式，代码：HITF.REQUEST_METHOD")
            column(name: "request_header", type: "varchar(" + 1800 + ")", remarks: "请求头")
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "是否启用，1代表启用，0代表禁用") {
                constraints(nullable: "false")
            }
            column(name: "soap_version", type: "varchar(" + 50 * weight + ")", remarks: "SOAP版本，代码：HITF.SOAP_VERSION")
            column(name: "soap_action", type: "varchar(" + 255 * weight + ")", remarks: "SOAPACTION")
            column(name: "status", type: "varchar(30)", remarks: "状态，代码：HITF.INTERFACE_STATUS, ENABLED/DISABLED/DISABLE_INPROGRESS") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "clob", remarks: "备注说明")
            column(name: "mapping_class", type: "clob", remarks: "映射类，处理请求参数及响应格式的映射")
            column(name: "request_transform_id", type: "bigint", remarks: "请求数据映射ID")
            column(name: "response_transform_id", type: "bigint", remarks: "响应数据映射ID")
            column(name: "request_cast_id", type: "bigint", remarks: "请求数据转化ID")
            column(name: "response_cast_id", type: "bigint", remarks: "响应数据转化ID")
            column(name: "retry_times", type: "bigint", remarks: "重试次数")
            column(name: "retry_interval", type: "bigint", remarks: "重试间隔(s)")
            column(name: "assert_json", type: "varchar(" + 500 * weight + ")", remarks: "重试断言")
            column(name: "body_namespace_flag", type: "Tinyint", defaultValue: "1", remarks: "是否设置body命名空间") {
                constraints(nullable: "false")
            }
            column(name: "version", type: "Int", defaultValue: "1", remarks: "接口配置版本") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }

    }

    changeSet(author: "changwen.yu@hand-china.com", id: "2020-08-12-hitf_interface_his") {
        addColumn(tableName: 'hitf_interface_his') {
            column(name: "async_flag", type: "Tinyint", defaultValue: "0", remarks: "是否异步调用")
        }
    }

    // 补充非主键索引
    changeSet(author: "xiaolong.li@hand-china.com", id: "2021-01-07_hitf_interface_his--add-nonpri-idx") {
        createIndex(tableName: "hitf_interface_his", indexName: "hitf_interface_his_n1") {
            column(name: "interface_id")
        }
    }
}