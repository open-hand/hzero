package script.db

databaseChangeLog(logicalFilePath: 'script/db/hiot_gw_edgink.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-09-03-hiot_gw_edgink") {
        def weight = 1
        if (helper.isSqlServer()) {
            weight = 2
        } else if (helper.isOracle()) {
            weight = 3
        }
        if (helper.dbType().isSupportSequence()) {
            createSequence(sequenceName: 'hiot_gw_edgink_s', startValue: "1")
        }
        createTable(tableName: "hiot_gw_edgink", remarks: "Edgink子设备") {
            column(name: "EDGINK_ID", type: "bigint", autoIncrement: true, remarks: "表ID，主键") { constraints(primaryKey: true) }
            column(name: "REL_ID", type: "bigint", remarks: "网关和设备关系ID, hiot_gateway_thing_rel.REL_ID") { constraints(nullable: "false") }
            column(name: "EDGINK_NAME", type: "varchar(" + 45 * weight + ")", remarks: "Edgink对应设备名称") { constraints(nullable: "false") }
            column(name: "REPORTED_WAY", type: "varchar(" + 30 * weight + ")", remarks: "上报方式, 取自快码 HIOT.COMMUNICATION_WAY") { constraints(nullable: "false") }
            column(name: "REPORTED_TOPIC", type: "varchar(" + 100 * weight + ")", remarks: "MQTT上报为topic")
            column(name: "REPORTED_CONFIG", type: "varchar(" + 500 * weight + ")", remarks: "如果是Webservice,则上报相关信息使用json存在该字段，包括url,username,password")
            column(name: "DESIRED_WAY", type: "varchar(" + 30 * weight + ")", remarks: "下发方式, 取自快码 HIOT.COMMUNICATION_WAY") { constraints(nullable: "false") }
            column(name: "DESIRED_TOPIC", type: "varchar(" + 100 * weight + ")", remarks: "MQTT下发为topic")
            column(name: "DESIRED_CONFIG", type: "varchar(" + 500 * weight + ")", remarks: "如果是Webservice,则下发相关信息使用json存在该字段，包括url,username,password")
            column(name: "DESCRIPTION", type: "varchar(" + 100 * weight + ")", remarks: "说明")
            column(name: "TENANT_ID", type: "bigint", remarks: "租户ID") { constraints(nullable: "false") }
            column(name: "OBJECT_VERSION_NUMBER", type: "bigint", defaultValue: "1", remarks: "") { constraints(nullable: "false") }
            column(name: "creation_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
            column(name: "created_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_updated_by", type: "bigint", defaultValue: "-1", remarks: "") { constraints(nullable: "false") }
            column(name: "last_update_date", type: "datetime", defaultValueComputed: "CURRENT_TIMESTAMP", remarks: "") { constraints(nullable: "false") }
        }


        addUniqueConstraint(columnNames: "REL_ID,EDGINK_NAME", tableName: "hiot_gw_edgink", constraintName: "hiot_gw_edgink_u1")
    }
}