package script.db

databaseChangeLog(logicalFilePath: 'script/db/hmsg_email_filter.groovy') {
    changeSet(author: "shuangfei.zhu@hand-china.com", id: "2019-06-04-hmsg_email_filter") {
        def weight = 1
        if(helper.isSqlServer()){
            weight = 2
        } else if(helper.isOracle()){
            weight = 3
        }
        if(helper.dbType().isSupportSequence()){
            createSequence(sequenceName: 'hmsg_email_filter_s', startValue:"1")
        }
        createTable(tableName: "hmsg_email_filter", remarks: "邮箱账户黑白名单") {
            column(name: "email_filter_id", type: "bigint", autoIncrement: true ,   remarks: "表ID，主键，供其他表做外键")  {constraints(primaryKey: true)} 
            column(name: "server_id", type: "bigint",  remarks: "邮箱账户id，hmsg_email_server.server_id")  {constraints(nullable:"false")}  
            column(name: "address", type: "varchar(" + 30 * weight + ")",  remarks: "地址")  {constraints(nullable:"false")}  
            column(name: "object_version_number", type: "bigint",   defaultValue:"1",   remarks: "行版本号，用来处理锁")  {constraints(nullable:"false")}  
            column(name: "creation_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "created_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_updated_by", type: "bigint",   defaultValue:"-1",   remarks: "")  {constraints(nullable:"false")}  
            column(name: "last_update_date", type: "datetime",   defaultValueComputed:"CURRENT_TIMESTAMP",   remarks: "")  {constraints(nullable:"false")}  

        }

        addUniqueConstraint(columnNames:"server_id,address",tableName:"hmsg_email_filter",constraintName: "hmsg_email_filter_u1")
    }
    changeSet(author: "hzero@hand-china.com", id: "2020-06-11-hmsg_email_filter") {
        addColumn(tableName: 'hmsg_email_filter') {
            column(name: "tenant_id", type: "bigint", defaultValue: "0", remarks: "租户ID,hpfm_tenant.tenant_id") {
                constraints(nullable: "false")
            }
        }
    }
}