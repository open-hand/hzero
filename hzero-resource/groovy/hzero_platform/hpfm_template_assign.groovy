package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_template_assign.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-07-01-hpfm_template_assign") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_template_assign_s', startValue: "1")
        }
        createTable(tableName: "hpfm_template_assign", remarks: "模板配置") {
            column(name: "template_assign_id", type: "bigint", autoIncrement: true, remarks: "表ID，主键，供其他表做外键") {
                constraints(primaryKey: true)
            }
            column(name: "source_type", type: "varchar("+ 30 * weight +")", remarks: "关联来源类型，DOMAIN") { constraints(nullable: "false") }
            column(name: "source_key", type: "varchar("+ 30 * weight +")", remarks: "关联模板的来源KEY") { constraints(nullable: "false") }
            column(name: "template_id", type: "bigint", remarks: "模板Id，hpfm_template.template_id") { constraints(nullable: "false") }
            column(name: "tenant_id", type: "bigint", remarks: "租户Id") {
                constraints(nullable: "false")
            }
            column(name: "default_flag", type: "tinyint", defaultValue: "0", remarks: "默认标识 1:默认 0:非默认") {
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

       createIndex(tableName: "hpfm_template_assign", indexName: "hpfm_template_assign_n1") {
           column(name: "source_type")
           column(name: "source_key")
           column(name: "template_id")
           column(name: "tenant_id")
       }
    }
}