package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_financial_code.groovy') {
    changeSet(author: "jianbo.li@hand-china.com", id: "2019-02-20-hpfm_financial_code") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_financial_code_s', startValue: "1")
        }
        createTable(tableName: "hpfm_financial_code", remarks: "财务代码设置") {
            column(name: "code_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
            column(name: "code", type: "varchar(30)", remarks: "代码") { constraints(nullable: "false") }
            column(name: "name", type: "varchar(255)", remarks: "名称") { constraints(nullable: "false") }
            column(name: "type", type: "varchar(600)", remarks: "类型，逗号分隔，值引用LOV：HPFM.FINANCIAL_CODE_TYPE") {
                constraints(nullable: "false")
            }
            column(name: "parent_id", type: "bigint", remarks: "父级ID")
            column(name: "level_path", type: "varchar(600)", remarks: "层级路径，父级层级路径/当前代码")
            column(name: "enabled_flag", type: "tinyint", defaultValue: "1", remarks: "是否启用。1启用，0未启用") {
                constraints(nullable: "false")
            }
            column(name: "remark", type: "longtext", remarks: "备注说明")
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
        createIndex(tableName: "hpfm_financial_code", indexName: "hpfm_financial_code_n1") {
            column(name: "type")
            column(name: "tenant_id")
        }
        createIndex(tableName: "hpfm_financial_code", indexName: "hpfm_financial_code_n2") {
            column(name: "level_path")
            column(name: "tenant_id")
        }

        addUniqueConstraint(columnNames: "code,tenant_id", tableName: "hpfm_financial_code", constraintName: "hpfm_financial_code_u1")
    }
    changeSet(author: "jianbo_li@hand-china.com", id: "2019-03-19-hpfm_financial_code") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        modifyDataType(tableName: "hpfm_financial_code", columnName: "code", newDataType: "varchar(" + 30 * weight + ")")
        modifyDataType(tableName: "hpfm_financial_code", columnName: "name", newDataType: "varchar(" + 255 * weight + ")")
        modifyDataType(tableName: "hpfm_financial_code", columnName: "type", newDataType: "varchar(" + 600 * weight + ")")
        modifyDataType(tableName: "hpfm_financial_code", columnName: "level_path", newDataType: "varchar(" + 600 * weight + ")")
    }

    changeSet(author: "hzero@hand-china.com", id: "2019-07-19-hpfm_financial_code") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute1", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute2", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute3", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute4", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute5", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute6", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute7", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute8", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute9", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute10", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute11", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute12", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute13", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute14", type: "varchar(" + 150 * weight + ")")
        }
        addColumn(tableName: 'hpfm_financial_code') {
            column(name: "attribute15", type: "varchar(" + 150 * weight + ")")
        }
    }
}