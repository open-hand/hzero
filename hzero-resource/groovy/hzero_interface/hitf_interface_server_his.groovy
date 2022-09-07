package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_server_his.groovy') {

    changeSet(author: "changwen.yu@hand-china.com", id: "2020-08-05_HITF_INTERFACE_SERVER_HIS") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'HITF_INTERFACE_SERVER_HIS_S', startValue: "10001")
        }
        createTable(tableName: "HITF_INTERFACE_SERVER_HIS", remarks: "服务配置历史表") {
            column(name: "interface_server_his_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，供其他表做外键") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "HITF_INTERFACE_SERVER_HIS_PK")
            }
            column(name: "interface_server_id", type: "bigint", remarks: "服务配置ID, hitf_interface_server") {
                constraints(nullable: "false")
            }
            column(name: "tenant_id", type: "bigint", remarks: "租户ID") {
                constraints(nullable: "false")
            }
            column(name: "server_code", type: "varchar(" + 128 * weight + ")", remarks: "服务代码")
            column(name: "server_name", type: "varchar(" + 250 * weight + ")", remarks: "服务名称") {
                constraints(nullable: "false")
            }
            column(name: "service_type", type: "varchar(30)", remarks: "服务类型，代码：HITF.SERVICE_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "domain_url", type: "varchar(" + 200 * weight + ")", remarks: "服务地址")
            column(name: "soap_namespace", type: "varchar(" + 255 * weight + ")", remarks: "SOAP命名空间")
            column(name: "soap_element_prefix", type: "varchar(30)", remarks: "SOAP参数前缀标识")
            column(name: "enabled_flag", type: "Tinyint", defaultValue: "1", remarks: "是否启用。1启用，0未启用") {
                constraints(nullable: "false")
            }
            column(name: "service_category", type: "varchar(30)", defaultValue: "EXTERNAL", remarks: "服务类别")
            column(name: "namespace", type: "varchar(30)", defaultValue: "HZERO", remarks: "命名空间，默认租户编码，与服务代码一起构成唯一") {
                constraints(nullable: "false")
            }
            column(name: "enabled_certificate_flag", type: "Tinyint", defaultValue: "0", remarks: "启用证书") {
                constraints(nullable: "false")
            }
            column(name: "certificate_id", type: "bigint", remarks: "CA证书ID")
            column(name: "swagger_url", type: "varchar(" + 600 * weight + ")", remarks: "Swagger地址")
            column(name: "public_flag", type: "Tinyint", defaultValue: "0", remarks: "接口鉴权标识") {
                constraints(nullable: "false")
            }
            column(name: "request_content_type", type: "varchar(" + 80 * weight + ")", remarks: "请求报文Content-Type类型")
            column(name: "response_content_type", type: "varchar(" + 80 * weight + ")", remarks: "响应报文Content-Type类型")
            column(name: "soap_data_node", type: "varchar(" + 100 * weight + ")", remarks: "soap响应数据节点标签")
            column(name: "auth_id", type: "bigint", remarks: "服务认证信息主键ID hitf_http_authorization.auth_id")
            column(name: "invoke_verify_sign_flag", type: "Tinyint", defaultValue: "0", remarks: "签名校验标识") {
                constraints(nullable: "false")
            }
            column(name: "status", type: "varchar(30)", remarks: "状态，代码HITF.INTERFACE_SERVER.STATUS： NEW新建，PUBLISHED已发布，OFFLINE已下线") {
                constraints(nullable: "false")
            }
            column(name: "version", type: "Int", remarks: "服务配置版本") {
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

    // 补充非主键索引
    changeSet(author: "xiaolong.li@hand-china.com", id: "2021-01-07_hitf_interface_server_his--add-nonpri-idx") {
        createIndex(tableName: "hitf_interface_server_his", indexName: "hitf_interface_server_his_n1") {
            column(name: "interface_server_id")
        }
    }

    changeSet(author: "he.chen@hand-china.com", id: "hitf_interface_server_his-2021-08-05-version-3") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        modifyDataType (tableName: "hitf_interface_server_his", columnName: "domain_url", newDataType: "varchar(" + 1200* weight + ")")
    }
}