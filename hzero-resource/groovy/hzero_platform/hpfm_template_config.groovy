package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_template_config.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-06-27-hpfm_template_config") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_template_config_s', startValue: "1")
        }
        createTable(tableName: "hpfm_template_config", remarks: "模板配置") {
            column(name: "config_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "template_assign_id", type: "bigint", remarks: "模板配置ID，hpfm_template_assign.template_assign_id") {
                constraints(nullable: "false")
            }
            column(name: "config_type_code", type: "varchar(" + 30 * weight + ")", remarks: "配置类型编码 HPFM.CONFIG_TYPE_CODE") { constraints(nullable: "false") }
            column(name: "config_code", type: "varchar("+ 60 * weight +")", remarks: "配置编码") { constraints(nullable: "false") }
            column(name: "config_value", type: "longtext", remarks: "配置值")
            column(name: "remark", type: "varchar("+ 1200 * weight +")", remarks: "备注说明")
            column(name: "link", type: "varchar("+ 480 * weight +")", remarks: "链接")
            column(name: "order_seq", type: "int", remarks: "排序标识，用于为配置排序")
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant") {
                constraints(nullable: "false")
            }
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

        createIndex(tableName: "hpfm_template_config", indexName: "hpfm_template_config_n1"){
            column(name: "template_assign_id")
            column(name: "config_code")
        }
    }
}