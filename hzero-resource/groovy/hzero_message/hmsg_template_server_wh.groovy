package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_template_server_wh.groovy') {
    changeSet(author: "hzero@hand-china.com", id: "2020-05-19-hmsg_template_server_wh") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_template_server_wh_s', startValue:"1")
        }
        createTable(tableName: "hmsg_template_server_wh", remarks: "消息发送配置webhook") {
            column(name: "temp_server_wh_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)}
            column(name: "temp_server_id", type: "bigint",  remarks: "消息发送配置Id,hpfm_template_server.temp_server_id")  {constraints(nullable:"false")}
            column(name: "temp_server_line_id", type: "bigint",  remarks: "消息发送配置行Id，hpfm_template_server_line.temp_server_line_id")  {constraints(nullable:"false")}
            column(name: "server_code", type: "varchar(" + 30 * weight + ")",  remarks: "关联账户配置编码")  {constraints(nullable:"false")}  
            column(name: "ext_info", type: "varchar(" + 480 * weight + ")",  remarks: "扩展字段")   
            column(name: "tenant_id", type: "bigint",   defaultValue:"0",   remarks: "租户ID,hpfm_tenant.tenant_id")  {constraints(nullable:"false")}
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"temp_server_id,temp_server_line_id,server_code,tenant_id",tableName:"hmsg_template_server_wh",constraintName: "hmsg_template_server_wh_u1")
    }
}