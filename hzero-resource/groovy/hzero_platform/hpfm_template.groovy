package script.db

databaseChangeLog(logicalFilePath: 'script/db/hpfm_template.groovy') {
    def weight = 1
    if(helper.isSqlServer()){
        weight = 2
    } else if(helper.isOracle()){
        weight = 3
    }
    changeSet(author: "xiaoyu.zhao@hand-china.com", id: "2019-06-26-hpfm_template") {
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hpfm_template_s', startValue: "1")
        }
        createTable(tableName: "hpfm_template", remarks: "模板维护") {
            column(name: "template_id", type: "bigint", autoIncrement: true, remarks: "模板ID") {
                constraints(primaryKey: true)
            }
            column(name: "template_code", type: "varchar(" + 30 * weight + ")", remarks: "模板编码") { constraints(nullable: "false") }
            column(name: "template_name", type: "varchar("+ 255 * weight +")", remarks: "模板名称") { constraints(nullable: "false") }
            column(name: "template_avatar", type: "varchar("+ 480 * weight +")", remarks: "模板缩略图") { constraints(nullable: "false") }
            column(name: "template_path", type: "varchar("+ 60 * weight +")", remarks: "模板路径") { constraints(nullable: "false") }
            column(name: "template_level_code", type: "varchar("+30 * weight+")", defaultValue: "SITE", remarks: "模板层级 HPFM.DATA_TENANT_LEVEL") { constraints(nullable: "false") }
            column(name: "enabled_flag", type: "tinyint", defaultValue: "0", remarks: "启用标识") {
                constraints(nullable: "false")
            }
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

        addUniqueConstraint(columnNames: "template_code, tenant_id", tableName: "hpfm_template", constraintName: "hpfm_template_u1")
    }
}