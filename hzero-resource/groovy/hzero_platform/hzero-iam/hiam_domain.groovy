package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiam_domain.groovy') {
    def weight = 1
    if (helper.isSqlServer()) {
        weight = 2
    } else if (helper.isOracle()) {
        weight = 3
    }
    changeSet(author: "minghui.qiu@hand-china.com", id: "2019-06-27-hiam_domain") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiam_domain_s', startValue: "1")
        }
        createTable(tableName: "hiam_domain", remarks: "单点二级域名") {
            column(name: "domain_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint", remarks: "客户租户ID") { constraints(nullable: "false") }
            column(name: "company_id", type: "bigint", defaultValue: "0", remarks: "客户公司ID，HPFM_COMPANY.COMPANY_ID")
            column(name: "domain_url", type: "varchar("+240 * weight+")", remarks: "域名") { constraints(nullable: "false") }
            column(name: "sso_type_code", type: "varchar("+60 * weight+")", remarks: "CAS|AUTH|SAML|IDM|NULL")
            column(name: "sso_server_url", type: "varchar("+1000 * weight+")", remarks: "单点认证服务器地址")
            column(name: "sso_login_url", type: "varchar("+1000 * weight+")", remarks: "单点登录地址")
            column(name: "sso_client_id", type: "varchar("+255 * weight+")", remarks: "oauth2单点ClientId")
            column(name: "sso_client_pwd", type: "varchar("+255 * weight+")", remarks: "oauth2单点Client密码")
            column(name: "sso_user_info", type: "varchar("+1000 * weight+")", remarks: "sso获取用户信息地址")
            column(name: "client_host_url", type: "varchar("+1000 * weight+")", remarks: "客户端URL")
            column(name: "remark", type: "varchar("+1000 * weight+")", remarks: "备注说明")
            column(name: "object_version_number", type: "bigint", defaultValue: "1", remarks: "行版本号，用来处理锁") {
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

        addUniqueConstraint(columnNames: "domain_url", tableName: "hiam_domain", constraintName: "hiam_domain_u1")
    }

    changeSet(author: 'minghui.qiu@hand-china.com', id: '2019-08-06-hiam_domain-add') {
        addColumn(tableName: 'hiam_domain') {
            column(name: "sso_logout_url", type: "varchar("+500 * weight+")",  remarks: "单点登出地址", afterColumn: 'sso_login_url')
        }
    }

    changeSet(author: 'minghui.qiu@hand-china.com', id: '2019-09-05-hiam_domain-add') {
        addColumn(tableName: 'hiam_domain') {
            column(name: "saml_meta_url", type: "varchar("+500 * weight+")",  remarks: "saml元数据地址", afterColumn: 'sso_user_info')
        }
    }
	
	changeSet(author: 'hzero@hand-china.com', id: '2020-05-27-hiam_domain') {
        addColumn(tableName: 'hiam_domain') {
            column(name: "login_name_field", type: "varchar(60)",  remarks: "登录名属性", afterColumn: 'saml_meta_url') { constraints(nullable: "true") }
        }
    }
}