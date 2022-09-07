package script.db

databaseChangeLog(logicalFilePath: 'script/db/halt_alert_addon.groovy') {
    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-06-09-halt_alert_addon") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'halt_alert_addon_s', startValue:"1")
        }
        createTable(tableName: "halt_alert_addon", remarks: "告警附加信息") {
            column(name: "alert_addon_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID")  {constraints(nullable:"false")}
            column(name: "addon_type", type: "varchar(" + 30 * weight + ")",   defaultValue:"LABEL",   remarks: "附加信息类型，快速编码HALT.ALERT_ADDON_TYPE，LABEL/ANNOTATION")  {constraints(nullable:"false")}
            column(name: "addon_code", type: "varchar(" + 255 * weight + ")",  remarks: "代码")  {constraints(nullable:"false")}
            column(name: "addon_value", type: "varchar(" + 255 * weight + ")",  remarks: "值")  {constraints(nullable:"false")}
            column(name: "use_code", type: "varchar(" + 30 * weight + ")",   defaultValue:"RULE",   remarks: "用途代码，快速编码HALT.ALERT_ADDON_USE_CODE")  {constraints(nullable:"false")}
            column(name: "use_id", type: "bigint",  remarks: "用途ID，例如，规则，则此处是alert_rule_id，其他同理")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}

        }

        addUniqueConstraint(columnNames:"use_code,use_id,addon_type,tenant_id,addon_code",tableName:"halt_alert_addon",constraintName: "halt_alert_addon_u1")
    }

    changeSet(author: "xingxing.wu@hand-china.com", id: "2020-07-30-halt_alert_addon") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        modifyDataType(tableName: "halt_alert_addon", columnName: 'addon_code', newDataType: "varchar(" + 500 * weight + ")")
        modifyDataType(tableName: "halt_alert_addon", columnName: 'addon_value', newDataType: "varchar(" + 500 * weight + ")")
    }
}