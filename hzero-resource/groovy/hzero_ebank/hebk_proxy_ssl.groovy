package script.db

databaseChangeLog(logicalFilePath: 'script/db/hebk_proxy_ssl.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-02-06_hebk_proxy_ssl") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hebk_proxy_ssl_s', startValue: "10001")
        }
        createTable(tableName: "hebk_proxy_ssl", remarks: "代理SSL") {
            column(name: "proxy_id", type: "bigint", autoIncrement: "true", startWith: "10001", remarks: "表ID，主键，hebk_proxy.proxy_id") {
                constraints(nullable: "false", primaryKey: "true", primaryKeyName: "hebk_proxy_ssl_pk")
            }
            column(name: "trusted_ca_file", type: "text", remarks: "CA证书文件") {
                constraints(nullable: "false")
            }
            column(name: "cert_file", type: "text", remarks: "公钥文件") {
                constraints(nullable: "false")
            }
            column(name: "key_file", type: "text", remarks: "私钥文件") {
                constraints(nullable: "false")
            }
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁")
            column(name: "created_by", type: "bigint", defaultValue: "-1")
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1")
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP")
        }
    }

    changeSet(author: "hzero@hand-china.com", id: "2020-06-12_hebk_proxy_ssl") {
        addColumn(tableName: 'hebk_proxy_ssl') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID", afterColumn: 'key_file')
        }
    }
}
