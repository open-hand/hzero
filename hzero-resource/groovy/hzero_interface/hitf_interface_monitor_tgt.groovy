package script.db

databaseChangeLog(logicalFilePath: 'script/db/hitf_interface_monitor_tgt.groovy') {
    changeSet(author: "minghui.qiu@hand-china.com ", id: "2020-02-14-hitf_interface_monitor_tgt") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hitf_interface_monitor_tgt_s', startValue:"1")
        }
        createTable(tableName: "hitf_interface_monitor_tgt", remarks: "接口运维配置预警目标") {
            column(name: "target_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键")  {constraints(primaryKey: true)}
            column(name: "interface_monitor_id", type: "bigint",  remarks: "接口运维配置hitf_interface_monitor.interface_monitor_id")  {constraints(nullable:"false")}
            column(name: "type_code", type: "varchar(" + 30 * weight + ")",  remarks: "模版类型，值集:HMSG.MESSAGE_TYPE")   {constraints(nullable:"false")}
            column(name: "user_id", type: "varchar(" + 30 * weight + ")",  remarks: "当通知类型是短信、邮件、站内信时为平台userId，否则外部系统用户ID")   {constraints(nullable:"false")}
            column(name: "agent_id", type: "bigint",  remarks: "外部系统应用ID（企业微信、钉钉）")
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}
        }

        addUniqueConstraint(columnNames:"interface_monitor_id,type_code,user_id",tableName:"hitf_interface_monitor_tgt",constraintName: "hitf_interface_monitor_tgt_u1")
    }

    // 修复租户越权问题
    changeSet(author: "xiaolong.li@hand-china.com", id: "2020-06-11-hitf_interface_monitor_tgt") {
        // 添加租户ID
        addColumn(tableName: 'hitf_interface_monitor_tgt') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID") {
                constraints(nullable: "false")
            }
        }

        // 删除重建唯一性索引，添加租户ID
        dropUniqueConstraint(uniqueColumns:"interface_monitor_id,type_code,user_id",tableName:"hitf_interface_monitor_tgt",constraintName: "hitf_interface_monitor_tgt_u1")
        addUniqueConstraint(columnNames:"interface_monitor_id,type_code,user_id,tenant_id",tableName:"hitf_interface_monitor_tgt",constraintName: "hitf_interface_monitor_tgt_u1")
    }
}
