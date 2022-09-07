package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_log.groovy') {

    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-02-28-hitf_interface_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hitf_interface_log_s', startValue: "1")
        }
        createTable(tableName: "hitf_interface_log", remarks: "") {
            column(name: "interface_log_id", type: "bigint", autoIncrement: true, remarks: "") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint", remarks: "") { constraints(nullable: "false") }
            column(name: "invoke_key", type: "varchar(" + 255 * weight + ")", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "application_code", type: "varchar(" + 30 * weight + ")", remarks: "")
            column(name: "application_name", type: "varchar(" + 250 * weight + ")", remarks: "")
            column(name: "server_code", type: "varchar(" + 30 * weight + ")", remarks: "")
            column(name: "server_name", type: "varchar(" + 250 * weight + ")", remarks: "")
            column(name: "client_id", type: "varchar(" + 255 * weight + ")", remarks: "")
            column(name: "interface_type", type: "varchar(" + 30 * weight + ")", remarks: "")
            column(name: "interface_url", type: "varchar(" + 255 * weight + ")", remarks: "")
            column(name: "interface_request_method", type: "varchar(" + 30 * weight + ")", remarks: "")
            column(name: "interface_request_time", type: "datetime", remarks: "")
            column(name: "interface_response_time", type: "bigint", remarks: "")
            column(name: "interface_response_code", type: "varchar(" + 10 * weight + ")", remarks: "")
            column(name: "interface_response_status", type: "varchar(" + 255 * weight + ")", remarks: "")
            column(name: "request_method", type: "varchar(" + 30 * weight + ")", remarks: "")
            column(name: "request_time", type: "datetime", remarks: "")
            column(name: "response_time", type: "bigint", remarks: "")
            column(name: "response_code", type: "varchar(" + 10 * weight + ")", remarks: "")
            column(name: "response_status", type: "varchar(" + 255 * weight + ")", remarks: "")
            column(name: "ip", type: "varchar(" + 40 * weight + ")", remarks: "")
            column(name: "referer", type: "varchar(" + 250 * weight + ")", remarks: "")
            column(name: "user_agent", type: "varchar(" + 250 * weight + ")", remarks: "")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") {
                constraints(nullable: "false")
            }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") {
                constraints(nullable: "false")
            }
        }
    }
    changeSet(author: "xingxing.wu@hand-china.com", id: "2019-06-11-hitf_interface_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hitf_interface_log') {
            column(name: "invoke_type", type: "varchar(" + 30 * weight + ")", defaultValue: "NORMAL", remarks: "调用类型，代码HITF.INVOKE_TYPE") {
                constraints(nullable: "false")
            }
        }
        addColumn(tableName: 'hitf_interface_log') {
            column(name: "invoke_source_id", type: "bigint", remarks: "调用来源ID，例如，测试用例类型，此处即为测试用例ID")
        }
        addColumn(tableName: 'hitf_interface_log') {
            column(name: "interface_code", type: "varchar(" + 30 * weight + ")", remarks: "接口代码")
        }
    }
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-08-29-hitf_interface_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        modifyDataType(tableName: "hitf_interface_log", columnName: "server_code", newDataType: "varchar(" + 128 * weight + ")")
        modifyDataType(tableName: "hitf_interface_log", columnName: "interface_code", newDataType: "varchar(" + 128 * weight + ")")
    }

    changeSet(author: "junlin.zhu@hand-china.com", id: "2020-03-31-update-hitf_interface_log") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        addColumn(tableName: 'hitf_interface_log') {
            column(name: "interface_id", type: "bigint", remarks: "接口主键, hitf_interface.interface_id")
        }
        addColumn(tableName: 'hitf_interface_log') {
            column(name: "interface_server_id", type: "bigint", remarks: "服务主键, hitf_interface_server.interface_server_id")
        }
        addColumn(tableName: 'hitf_interface_log') {
            column(name: "interface_name", type: "varchar(" + 250 * weight + ")", remarks: "接口名称")
        }
    }

    changeSet(author: "qigang.qin@hand-china.com", id: "hitf_interface_log-2021-03-04-version-2") {

        if(helper.isOracle()){
            sql {
                "ALTER TABLE hitf_interface_log RENAME COLUMN ip TO ip_bak;" +
                        "ALTER TABLE hitf_interface_log ADD ip clob;" +
                        "COMMENT ON COLUMN hitf_interface_log.ip IS 'IP地址';" +
                        "UPDATE hitf_interface_log SET ip = ip_bak;" +
                        "ALTER TABLE hitf_interface_log DROP COLUMN ip_bak;"+

                        "ALTER TABLE hitf_interface_log RENAME COLUMN referer TO referer_bak;" +
                        "ALTER TABLE hitf_interface_log ADD referer clob;" +
                        "UPDATE hitf_interface_log SET referer = referer_bak;" +
                        "ALTER TABLE hitf_interface_log DROP COLUMN referer_bak;"+

                        "ALTER TABLE hitf_interface_log RENAME COLUMN user_agent TO user_agent_bak;" +
                        "ALTER TABLE hitf_interface_log ADD user_agent clob;" +
                        "UPDATE hitf_interface_log SET user_agent = user_agent_bak;" +
                        "ALTER TABLE hitf_interface_log DROP COLUMN user_agent_bak;"
            }
        } else {
            modifyDataType (tableName: "hitf_interface_log", columnName: "ip", newDataType: "longtext")
            modifyDataType (tableName: "hitf_interface_log", columnName: "referer", newDataType: "longtext")
            modifyDataType (tableName: "hitf_interface_log", columnName: "user_agent", newDataType: "longtext")
        }

    }

    changeSet(author: "he.chen@hand-china.com", id: "2020-12-18-hitf_interface_log") {
        createIndex(tableName: "hitf_interface_log", indexName: "hitf_interface_log_N1") {
            column(name: "request_time")
        }
    }

    changeSet(author: "he.chen@hand-china.com", id: "2021-03-15-hitf_interface_log") {
        addUniqueConstraint(columnNames:"invoke_key",tableName:"hitf_interface_log",constraintName: "hitf_interface_log_U1")
    }
}